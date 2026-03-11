package com.example.insightflow_lite.consumer;

import com.example.insightflow_lite.model.MonitorData;
import com.example.insightflow_lite.service.AnomalyDetectionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MonitorDataConsumer {
    private static final String QUEUE_KEY = "monitor:queue:server-data";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StringRedisTemplate redisTemplate;
    private final AnomalyDetectionService anomalyDetectionService;

    // 运行标志，用于控制循环退出
    private volatile boolean running = true;

    public MonitorDataConsumer(StringRedisTemplate redisTemplate,
                               AnomalyDetectionService anomalyDetectionService) {
        this.redisTemplate = redisTemplate;
        this.anomalyDetectionService = anomalyDetectionService;
    }

    @PostConstruct
    public void startConsume() {
        try {
            // 尝试连接Redis，检查连接状态
            redisTemplate.getConnectionFactory().getConnection().ping();
            log.info("成功连接到Redis服务器");
            executor.submit(this::consumeLoop);
            log.info("监控数据消费者已启动，开始监听队列：{}", QUEUE_KEY);
        } catch (Exception e) {
            log.warn("Redis连接失败，监控数据消费者将不启动：{}", e.getMessage());
        }
    }

    @PreDestroy
    public void stopConsume() {
        log.info("正在停止监控数据消费者...");
        running = false;                     // 设置停止标志
        executor.shutdown();                  // 不再接受新任务
        try {
            // 等待现有任务完成，最多 5 秒
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();       // 强制中断
                log.warn("消费者线程未在超时内结束，已强制中断");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("监控数据消费者已停止");
    }

    private void consumeLoop() {
        while (running) {   // 使用自定义运行标志
            try {
                // 使用较短的超时（1秒）以便及时响应 running 标志变化
                String dataJson = redisTemplate.opsForList()
                        .rightPop(QUEUE_KEY, 1, TimeUnit.SECONDS);

                if (dataJson != null) {
                    MonitorData monitorData = objectMapper.readValue(dataJson, MonitorData.class);
                    anomalyDetectionService.processMonitorData(monitorData);
                    log.info("成功消费监控数据：{}", monitorData);
                }
            } catch (JsonProcessingException e) {
                log.error("JSON解析失败，数据: {}", e.getMessage());
            } catch (IllegalStateException e) {
                // Redis 连接工厂已关闭（如 STOPPING 或 destroyed）
                log.warn("Redis连接已不可用，消费者将继续运行：{}", e.getMessage());
                // 不退出，继续运行
                try {
                    Thread.sleep(5000); // 休眠5秒后重试
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            } catch (RedisConnectionFailureException e) {
                // 其他 Redis 连接失败情况
                log.warn("Redis连接失败，消费者将继续运行：{}", e.getMessage());
                // 不退出，继续运行
                try {
                    Thread.sleep(5000); // 休眠5秒后重试
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            } catch (Exception e) {
                log.error("消费消息发生未知异常", e);
                // 可根据需要决定是否继续运行，此处选择继续并短暂休眠避免空转
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
        }
        log.info("消费者循环退出");
    }
}
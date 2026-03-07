package com.example.insightflow_lite.task;

import com.example.insightflow_lite.model.MonitorData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@EnableScheduling
public class MonitorDataGenerator {
    // 队列名称，必须和消费者完全一致
    private static final String QUEUE_KEY = "monitor:queue:server-data";
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StringRedisTemplate redisTemplate;

    // 构造器注入
    public MonitorDataGenerator(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 每秒执行一次，生成监控数据
    @Scheduled(fixedRate = 1000)
    public void generateMonitorData() {
        try {
            // 生成随机监控数据
            double cpu = 20 + random.nextDouble() * 60;
            double mem = 60 + random.nextDouble() * 30;
            double disk = 70 + random.nextDouble() * 25;
            MonitorData data = new MonitorData(cpu, mem, disk);

            // 写入 Redis List 队列（左进右出）
            String dataJson = objectMapper.writeValueAsString(data);
            redisTemplate.opsForList().leftPush(QUEUE_KEY, dataJson);

            log.info("生成监控数据并写入队列：{}", dataJson);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
        } catch (Exception e) {
            log.error("生成监控数据失败", e);
        }
    }
}
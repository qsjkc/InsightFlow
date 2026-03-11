package com.example.insightflow_lite.task;

import com.example.insightflow_lite.model.MonitorData;
import com.example.insightflow_lite.service.AnomalyDetectionService;
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
    private final AnomalyDetectionService anomalyDetectionService;

    // 构造器注入
    public MonitorDataGenerator(StringRedisTemplate redisTemplate, AnomalyDetectionService anomalyDetectionService) {
        this.redisTemplate = redisTemplate;
        this.anomalyDetectionService = anomalyDetectionService;
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

            try {
                // 尝试将数据写入Redis队列
                String dataJson = objectMapper.writeValueAsString(data);
                redisTemplate.opsForList().leftPush(QUEUE_KEY, dataJson);
                log.info("Redis连接正常，生成监控数据并写入队列：{}", dataJson);
            } catch (Exception redisEx) {
                // Redis连接失败，直接处理数据
                log.warn("Redis连接失败，直接处理监控数据: {}", redisEx.getMessage());
                anomalyDetectionService.processMonitorData(data);
                log.info("生成监控数据并直接处理：{}", data);
            }
        } catch (Exception e) {
            log.error("生成监控数据失败", e);
        }
    }
}
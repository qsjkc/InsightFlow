package com.example.insightflow_lite.websocket;

import com.example.insightflow_lite.service.AnomalyDetectionService.MonitorAnomaly;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket推送服务：将监控数据推送给前端
 */
@Slf4j
@Service
public class WebSocketPushService {
    // 推送目的地：前端订阅这个地址接收数据
    private static final String PUSH_DESTINATION = "/topic/monitor/data";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 推送监控数据（包含异常检测结果）
     */
    public void pushMonitorData(MonitorAnomaly anomaly) {
        try {
            // 转成JSON字符串推送
            String json = objectMapper.writeValueAsString(anomaly);
            messagingTemplate.convertAndSend(PUSH_DESTINATION, json);
            log.info("WebSocket推送监控数据：{}", json);
        } catch (JsonProcessingException e) {
            log.error("WebSocket推送数据序列化失败", e);
        }
    }
}
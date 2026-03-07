package com.example.insightflow_lite.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置：使用STOMP协议（简化WebSocket开发）
 */
@Configuration
@EnableWebSocketMessageBroker // 开启WebSocket消息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册WebSocket端点：前端通过这个地址连接
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 端点地址：/ws/monitor，允许跨域
        registry.addEndpoint("/ws/monitor")
                .setAllowedOriginPatterns("*") // 允许所有域名访问（开发环境）
                .withSockJS(); // 降级支持：如果浏览器不支持WebSocket，自动用SockJS
    }

    /**
     * 配置消息代理：定义推送消息的前缀
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单消息代理（内存级），推送消息的前缀为 /topic
        registry.enableSimpleBroker("/topic");
        // 前端发送消息的前缀（本项目暂时用不到）
        registry.setApplicationDestinationPrefixes("/app");
    }
}
package com.example.insightflow_lite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 健康检查接口：验证项目是否正常启动
     */
    @GetMapping("/")
    public String health() {
        return "✅ InsightFlow_lite 项目启动成功！当前运行环境：" + System.getProperty("spring.profiles.active");
    }

    /**
     * Redis连通性测试接口：验证本地/容器能否正常连接虚拟机Redis
     */
    @GetMapping("/test/redis")
    public String testRedis() {
        try {
            // 写入测试数据
            redisTemplate.opsForValue().set("project:test:key", "InsightFlow连接成功");
            // 读取测试数据
            String value = redisTemplate.opsForValue().get("project:test:key");
            return "✅ Redis连接成功！写入并读取到数据：" + value;
        } catch (Exception e) {
            return "❌ Redis连接失败！错误信息：" + e.getMessage();
        }
    }
}
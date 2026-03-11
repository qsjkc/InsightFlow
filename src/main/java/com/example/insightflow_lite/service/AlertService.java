package com.example.insightflow_lite.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 报警服务：处理异常报警，如邮件通知
 */
@Slf4j
@Service
public class AlertService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * 发送报警邮件
     * @param message 报警信息
     */
    public void sendAlert(String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo("admin@example.com"); // 接收邮箱
            mailMessage.setSubject("InsightFlow 监控系统报警");
            mailMessage.setText(message);
            mailMessage.setFrom("alert@insightflow.com"); // 发送邮箱
            
            mailSender.send(mailMessage);
            log.info("报警邮件发送成功: {}", message);
        } catch (Exception e) {
            log.error("报警邮件发送失败", e);
        }
    }
    
    /**
     * 发送监控异常报警
     * @param serverId 服务器ID
     * @param metric 指标名称
     * @param value 指标值
     * @param threshold 阈值
     */
    public void sendMonitorAlert(String serverId, String metric, double value, double threshold) {
        String message = String.format(
            "【监控异常】\n" +
            "服务器: %s\n" +
            "指标: %s\n" +
            "当前值: %.2f\n" +
            "阈值: %.2f\n" +
            "时间: %s",
            serverId, metric, value, threshold, java.time.LocalDateTime.now()
        );
        sendAlert(message);
    }
}
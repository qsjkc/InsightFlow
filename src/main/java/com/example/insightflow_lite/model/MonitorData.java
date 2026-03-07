package com.example.insightflow_lite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 监控数据实体类：封装服务器CPU、内存、磁盘使用率
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorData {
    // 服务器ID（模拟多台服务器，这里固定为server-01）
    private String serverId = "server-01";
    // 采集时间
    private String collectTime;
    // CPU使用率（0-100）
    private double cpuUsage;
    // 内存使用率（0-100）
    private double memUsage;
    // 磁盘使用率（0-100）
    private double diskUsage;

    // 构造方法：自动生成时间和随机数据
    public MonitorData(double cpuUsage, double memUsage, double diskUsage) {
        this.collectTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.cpuUsage = Math.round(cpuUsage * 100) / 100.0; // 保留两位小数
        this.memUsage = Math.round(memUsage * 100) / 100.0;
        this.diskUsage = Math.round(diskUsage * 100) / 100.0;
    }
}
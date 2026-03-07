package com.example.insightflow_lite.service;

import com.example.insightflow_lite.model.MonitorData;
import com.example.insightflow_lite.websocket.WebSocketPushService; // 关键：补上这个导入
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AnomalyDetectionService {
    private static final int WINDOW_SIZE = 50;
    private final Map<String, List<Double>> historyData = new HashMap<>();

    @Autowired
    private WebSocketPushService webSocketPushService;

    public AnomalyDetectionService() {
        historyData.put("cpu", new ArrayList<>());
        historyData.put("mem", new ArrayList<>());
        historyData.put("disk", new ArrayList<>());
    }

    public void processMonitorData(MonitorData data) {
        collectHistoryData("cpu", data.getCpuUsage());
        collectHistoryData("mem", data.getMemUsage());
        collectHistoryData("disk", data.getDiskUsage());

        AnomalyResult cpuResult = detectAnomaly("cpu", data.getCpuUsage());
        AnomalyResult memResult = detectAnomaly("mem", data.getMemUsage());
        AnomalyResult diskResult = detectAnomaly("disk", data.getDiskUsage());

        MonitorAnomaly anomaly = new MonitorAnomaly(data, cpuResult, memResult, diskResult);
        webSocketPushService.pushMonitorData(anomaly);

        if (anomaly.isAnyAnomaly()) {
            log.error("检测到异常数据：{}", anomaly);
        }
    }

    private void collectHistoryData(String key, double value) {
        List<Double> list = historyData.get(key);
        list.add(value);
        if (list.size() > WINDOW_SIZE) {
            list.remove(0);
        }
    }

    private AnomalyResult detectAnomaly(String key, double currentValue) {
        List<Double> dataList = historyData.get(key);
        if (dataList.size() < 10) {
            return new AnomalyResult(false, 0.0, 0.0);
        }

        double avg = dataList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double std = calculateStandardDeviation(dataList, avg);
        double lower = avg - 3 * std;
        double upper = avg + 3 * std;

        boolean isAnomaly = currentValue < lower || currentValue > upper;
        return new AnomalyResult(isAnomaly, avg, std);
    }

    private double calculateStandardDeviation(List<Double> dataList, double avg) {
        double sum = 0.0;
        for (double value : dataList) {
            sum += Math.pow(value - avg, 2);
        }
        return Math.sqrt(sum / dataList.size());
    }

    // 内部类保持不变
    @lombok.Data
    public static class AnomalyResult {
        private boolean isAnomaly;
        private double avg;
        private double std;

        public AnomalyResult(boolean isAnomaly, double avg, double std) {
            this.isAnomaly = isAnomaly;
            this.avg = Math.round(avg * 100) / 100.0;
            this.std = Math.round(std * 100) / 100.0;
        }
    }

    @lombok.Data
    public static class MonitorAnomaly {
        private MonitorData rawData;
        private AnomalyResult cpuResult;
        private AnomalyResult memResult;
        private AnomalyResult diskResult;

        public MonitorAnomaly(MonitorData rawData, AnomalyResult cpuResult, AnomalyResult memResult, AnomalyResult diskResult) {
            this.rawData = rawData;
            this.cpuResult = cpuResult;
            this.memResult = memResult;
            this.diskResult = diskResult;
        }

        public boolean isAnyAnomaly() {
            return cpuResult.isAnomaly() || memResult.isAnomaly() || diskResult.isAnomaly();
        }
    }
}
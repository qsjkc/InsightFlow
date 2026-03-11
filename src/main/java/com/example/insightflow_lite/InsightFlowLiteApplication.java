package com.example.insightflow_lite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InsightFlowLiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsightFlowLiteApplication.class, args);
    }

}

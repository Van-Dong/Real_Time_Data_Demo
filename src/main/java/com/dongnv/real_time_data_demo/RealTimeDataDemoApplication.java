package com.dongnv.real_time_data_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class RealTimeDataDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealTimeDataDemoApplication.class, args);
    }

}

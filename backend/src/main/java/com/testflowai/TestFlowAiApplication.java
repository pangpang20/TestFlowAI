package com.testflowai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TestFlowAI 后端应用启动类
 * @author TestFlowAI
 */
@SpringBootApplication
@MapperScan("com.testflowai.mapper")
public class TestFlowAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestFlowAiApplication.class, args);
    }
}

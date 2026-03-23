package com.testflowai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CORS 跨域配置类
 * @author TestFlowAI
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // 从配置文件读取 CORS 配置
    @Value("${spring.web.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Value("${spring.web.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${spring.web.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${spring.web.cors.allow-credentials:true}")
    private Boolean allowCredentials;

    @Value("${spring.web.cors.max-age:3600}")
    private Long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 从配置文件读取允许的源
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
            .map(String::trim)
            .collect(Collectors.toList());

        // 允许的方法
        List<String> methods = Arrays.stream(allowedMethods.split(","))
            .map(String::trim)
            .collect(Collectors.toList());

        // 允许的头部
        List<String> headers = Arrays.asList(allowedHeaders);

        registry.addMapping("/**")
            // 允许的源
            .allowedOrigins(origins.toArray(new String[0]))
            // 允许的方法
            .allowedMethods(methods.toArray(new String[0]))
            // 允许的头部
            .allowedHeaders(headers.toArray(new String[0]))
            // 允许携带凭证
            .allowCredentials(allowCredentials)
            // 预检请求缓存时间
            .maxAge(maxAge);
    }
}

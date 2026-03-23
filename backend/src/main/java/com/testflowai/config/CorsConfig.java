package com.testflowai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域配置类
 * @author TestFlowAI
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            // 允许的源
            .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000")
            // 允许的方法
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            // 允许的头部
            .allowedHeaders("*")
            // 允许携带凭证
            .allowCredentials(true)
            // 预检请求缓存时间
            .maxAge(3600);
    }
}

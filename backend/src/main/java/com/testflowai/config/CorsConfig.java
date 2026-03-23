package com.testflowai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域配置类
 * @author TestFlowAI
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.access-url:/api/files}")
    private String accessUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            // 允许的源
            .allowedOrigins(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:3001",
                "http://172.16.1.137:3001"
            )
            // 允许的方法
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            // 允许的头部
            .allowedHeaders("*")
            // 允许携带凭证
            .allowCredentials(true)
            // 预检请求缓存时间
            .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射本地上传目录到 URL
        registry.addResourceHandler(accessUrl + "/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}

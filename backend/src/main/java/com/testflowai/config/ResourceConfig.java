package com.testflowai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源映射配置类
 * 用于映射本地文件上传目录到 URL
 * @author TestFlowAI
 */
@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.access-url:/api/files}")
    private String accessUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射本地上传目录到 URL
        registry.addResourceHandler(accessUrl + "/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}

package com.testflowai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.access-url:/api/files}")
    private String accessUrl;

    public String uploadFile(MultipartFile file, String folder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            String objectName = folder + "/" + filename;

            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir, folder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件到本地
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("文件上传成功：{}", filePath);

            // 返回访问 URL
            return accessUrl + "/" + objectName;
        } catch (IOException e) {
            logger.error("文件上传失败：{}", e.getMessage());
            throw new RuntimeException("文件上传失败：" + e.getMessage(), e);
        }
    }

    public String uploadAvatar(MultipartFile file, String userId) {
        return uploadFile(file, "avatars/" + userId);
    }
}

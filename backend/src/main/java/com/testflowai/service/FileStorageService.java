package com.testflowai.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * 文件存储服务 - 使用 MinIO
 * @author TestFlowAI
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    public FileStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * 上传文件
     * @param file 上传的文件
     * @param folder 文件夹路径
     * @return 文件访问 URL
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            String objectName = folder + "/" + filename;

            // 上传到 MinIO
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            inputStream.close();

            logger.info("文件上传成功：{}/{}", bucket, objectName);

            // 生成预签名 URL（有效期 7 天）
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );

            return url;
        } catch (Exception e) {
            logger.error("文件上传失败：{}", e.getMessage());
            throw new RuntimeException("文件上传失败：" + e.getMessage(), e);
        }
    }

    /**
     * 上传头像
     * @param file 头像文件
     * @param userId 用户 ID
     * @return 头像访问 URL
     */
    public String uploadAvatar(MultipartFile file, String userId) {
        return uploadFile(file, "avatars/" + userId);
    }
}

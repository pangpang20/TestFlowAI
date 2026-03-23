package com.testflowai.controller;

import com.testflowai.common.Result;
import com.testflowai.entity.User;
import com.testflowai.service.FileStorageService;
import com.testflowai.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 处理头像等文件上传请求
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final FileStorageService fileStorageService;
    private final UserService userService;

    public FileUploadController(FileStorageService fileStorageService, UserService userService) {
        this.fileStorageService = fileStorageService;
        this.userService = userService;
    }

    /**
     * 上传头像
     * @param file 头像文件
     * @param username 用户名（从 Token 中获取）
     * @return 头像访问 URL
     */
    @PostMapping("/avatar")
    public Result<Map<String, Object>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal String username) {

        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        // 验证文件大小（限制 5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("文件大小不能超过 5MB");
        }

        try {
            // 获取用户 ID
            User user = userService.getByUsername(username);
            if (user == null || user.getUserId() == null) {
                return Result.error("用户不存在");
            }

            // 上传头像到 MinIO
            String avatarUrl = fileStorageService.uploadAvatar(file, user.getUserId());

            // 更新用户头像 URL 到数据库
            userService.updateAvatar(user.getUserId(), avatarUrl);

            Map<String, Object> result = new HashMap<>();
            result.put("avatarUrl", avatarUrl);

            logger.info("用户 {} 上传头像成功：{}", username, avatarUrl);
            return Result.success("头像上传成功", result);
        } catch (Exception e) {
            logger.error("上传头像失败：{}", e.getMessage());
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}

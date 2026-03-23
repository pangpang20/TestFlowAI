package com.testflowai.controller;

import com.testflowai.common.Result;
import com.testflowai.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、登出等认证相关请求
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     * @param loginRequest 登录请求（用户名、密码）
     * @return 登录结果（包含 Token）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = authService.login(loginRequest.username, loginRequest.password);
        return Result.success("登录成功", result);
    }

    /**
     * 用户登出
     * @param username 用户名（从 Token 中获取）
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(@AuthenticationPrincipal String username) {
        authService.logout(username);
        return Result.success("登出成功", null);
    }

    /**
     * 获取当前登录用户信息
     * @param username 用户名（从 Token 中获取）
     * @return 用户信息
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal String username) {
        if (username == null) {
            return Result.error("未登录");
        }
        Map<String, Object> userInfo = authService.login(username, "");
        return Result.success(userInfo);
    }

    /**
     * 修改密码
     * @param username 用户名（从 Token 中获取）
     * @param request 修改密码请求
     * @return 修改结果
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @AuthenticationPrincipal String username,
            @RequestBody ChangePasswordRequest request) {
        authService.changePassword(username, request.oldPassword, request.newPassword);
        return Result.success("密码修改成功", null);
    }

    /**
     * 登录请求 DTO
     */
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 修改密码请求 DTO
     */
    public static class ChangePasswordRequest {
        @NotBlank(message = "旧密码不能为空")
        private String oldPassword;

        @NotBlank(message = "新密码不能为空")
        @Size(min = 8, max = 20, message = "密码长度必须在 8-20 之间")
        // 密码必须包含大小写字母、数字和特殊字符
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,20}$",
                 message = "密码必须包含大小写字母、数字和特殊字符")
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}

package com.testflowai.controller;

import com.testflowai.common.Result;
import com.testflowai.service.AuthService;
import jakarta.validation.constraints.NotBlank;
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
}

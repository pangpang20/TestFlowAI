package com.testflowai.controller;

import com.testflowai.common.Result;
import com.testflowai.entity.User;
import com.testflowai.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * 处理用户相关的 REST 请求
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping
    public Result<List<User>> getAll() {
        List<User> users = userService.getAll();
        return Result.success(users);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<java.util.Map<String, Object>> getUserInfo(@AuthenticationPrincipal String username) {
        if (username == null) {
            return Result.error("未登录");
        }
        User user = userService.getByUsername(username);
        java.util.Map<String, Object> userInfo = new java.util.HashMap<>();
        userInfo.put("id", user.getUserId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", "admin"); // TODO: 从角色表获取
        return Result.success(userInfo);
    }

    /**
     * 根据 ID 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable String id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<User> create(@RequestBody CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.username);
        user.setPassword(request.password);
        user.setEmail(request.email);
        user.setStatus("active");

        User created = userService.create(user);
        return Result.success("创建成功", created);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<User> update(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        User user = new User();
        user.setUsername(request.username);
        user.setPassword(request.password);
        user.setEmail(request.email);
        user.setStatus(request.status != null ? (request.status == 1 ? "active" : "disabled") : null);

        User updated = userService.update(id, user);
        return Result.success("更新成功", updated);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return Result.success("删除成功", null);
    }

    /**
     * 创建用户请求 DTO
     */
    public static class CreateUserRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 32, message = "用户名长度必须在 3-32 之间")
        private String username;

        @NotBlank(message = "密码不能为空")
        @Size(min = 8, max = 20, message = "密码长度必须在 8-20 之间")
        // 密码必须包含大小写字母、数字和特殊字符
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,20}$",
                 message = "密码必须包含大小写字母、数字和特殊字符")
        private String password;

        @Email(message = "邮箱格式不正确")
        private String email;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * 更新用户请求 DTO
     */
    public static class UpdateUserRequest {
        @Size(min = 3, max = 32, message = "用户名长度必须在 3-32 之间")
        private String username;

        @Size(min = 6, max = 32, message = "密码长度必须在 6-32 之间")
        private String password;

        @Email(message = "邮箱格式不正确")
        private String email;

        private Integer status;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}

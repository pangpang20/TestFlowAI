package com.testflowai.service;

import com.testflowai.common.BusinessException;
import com.testflowai.common.ResultCode;
import com.testflowai.entity.User;
import com.testflowai.mapper.UserMapper;
import com.testflowai.util.JwtUtil;
import com.testflowai.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务类
 * 负责用户登录、登出和 JWT 令牌管理
 * @author TestFlowAI
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordUtil passwordUtil, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 包含 Token 和用户信息的 Map
     */
    public Map<String, Object> login(String username, String password) {
        // 查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            logger.warn("用户不存在：{}", username);
            throw new BusinessException(ResultCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // 验证密码
        if (!passwordUtil.matches(password, user.getPassword())) {
            logger.warn("密码错误：{}", username);
            throw new BusinessException(ResultCode.LOGIN_FAILED, "用户名或密码错误");
        }

        // 检查用户状态
        if ("disabled".equals(user.getStatus())) {
            logger.warn("用户已被禁用：{}", username);
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 生成 JWT Token
        String token = jwtUtil.generateToken(username);

        // 返回结果（不返回密码）
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", getUserWithoutPassword(user));

        logger.info("用户登录成功：{}", username);
        return result;
    }

    /**
     * 用户登出
     * @param username 用户名
     */
    public void logout(String username) {
        logger.info("用户登出：{}", username);
        // 无状态认证，登出时只需让客户端删除 Token 即可
        // 如需实现 Token 黑名单，可在此处添加逻辑
    }

    /**
     * 验证 Token
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * 从 Token 获取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    /**
     * 获取不包含密码的用户信息
     */
    private User getUserWithoutPassword(User user) {
        User result = new User();
        result.setUserId(user.getUserId());
        result.setUsername(user.getUsername());
        result.setEmail(user.getEmail());
        result.setAvatar(user.getAvatar());
        result.setStatus(user.getStatus());
        result.setCreatedAt(user.getCreatedAt());
        result.setUpdatedAt(user.getUpdatedAt());
        return result;
    }

    /**
     * 修改密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || username.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }

        // 查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 验证旧密码
        if (!passwordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        String encodedPassword = passwordUtil.encode(newPassword);
        user.setPassword(encodedPassword);
        userMapper.update(user);

        logger.info("用户 {} 密码修改成功", username);
    }
}

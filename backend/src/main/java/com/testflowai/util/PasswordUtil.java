package com.testflowai.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类
 * 使用 BCrypt 算法进行密码加密和验证
 * @author TestFlowAI
 */
@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder encoder;

    public PasswordUtil() {
        // BCrypt 强度因子为 10
        this.encoder = new BCryptPasswordEncoder(10);
    }

    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}

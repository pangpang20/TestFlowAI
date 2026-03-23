package com.testflowai;

import com.testflowai.common.BusinessException;
import com.testflowai.common.ResultCode;
import com.testflowai.entity.User;
import com.testflowai.mapper.UserMapper;
import com.testflowai.service.AuthService;
import com.testflowai.util.JwtUtil;
import com.testflowai.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试类
 * @author TestFlowAI
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordUtil passwordUtil;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setUserId("user-test-001");
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMye"); // 加密后的密码
        testUser.setEmail("test@example.com");
        testUser.setStatus("active");
        testUser.setDeleted(0);
    }

    /**
     * 测试登录成功
     */
    @Test
    void login_Success() {
        // 准备测试数据
        String username = "testuser";
        String password = "password123";

        // Mock 行为
        when(userMapper.selectByUsername(username)).thenReturn(testUser);
        when(passwordUtil.matches(password, testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(username)).thenReturn("mock_jwt_token");

        // 执行测试
        Map<String, Object> result = authService.login(username, password);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("token"));
        assertTrue(result.containsKey("user"));
        assertEquals("mock_jwt_token", result.get("token"));

        // 验证用户信息不包含密码
        User returnedUser = (User) result.get("user");
        assertNull(returnedUser.getPassword());
        assertEquals(testUser.getUsername(), returnedUser.getUsername());
        assertEquals(testUser.getEmail(), returnedUser.getEmail());
    }

    /**
     * 测试登录失败 - 用户不存在
     */
    @Test
    void login_UserNotFound() {
        // 准备测试数据
        String username = "nonexistent";
        String password = "password123";

        // Mock 行为
        when(userMapper.selectByUsername(username)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> authService.login(username, password)
        );

        assertEquals(ResultCode.LOGIN_FAILED.getCode(), exception.getCode());
    }

    /**
     * 测试登录失败 - 密码错误
     */
    @Test
    void login_WrongPassword() {
        // 准备测试数据
        String username = "testuser";
        String password = "wrongpassword";

        // Mock 行为
        when(userMapper.selectByUsername(username)).thenReturn(testUser);
        when(passwordUtil.matches(password, testUser.getPassword())).thenReturn(false);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> authService.login(username, password)
        );

        assertEquals(ResultCode.LOGIN_FAILED.getCode(), exception.getCode());
    }

    /**
     * 测试登录失败 - 用户被禁用
     */
    @Test
    void login_UserDisabled() {
        // 准备测试数据
        String username = "testuser";
        String password = "password123";

        // 设置用户状态为禁用
        testUser.setStatus("disabled");

        // Mock 行为
        when(userMapper.selectByUsername(username)).thenReturn(testUser);
        when(passwordUtil.matches(password, testUser.getPassword())).thenReturn(true);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> authService.login(username, password)
        );

        assertEquals(ResultCode.USER_DISABLED.getCode(), exception.getCode());
    }

    /**
     * 测试登出
     */
    @Test
    void logout() {
        String username = "testuser";

        // 执行测试（不抛出异常即为成功）
        assertDoesNotThrow(() -> authService.logout(username));
    }

    /**
     * 测试验证 Token
     */
    @Test
    void validateToken_Valid() {
        String token = "valid_token";

        when(jwtUtil.validateToken(token)).thenReturn(true);

        assertTrue(authService.validateToken(token));
    }

    /**
     * 测试验证无效 Token
     */
    @Test
    void validateToken_Invalid() {
        String token = "invalid_token";

        when(jwtUtil.validateToken(token)).thenReturn(false);

        assertFalse(authService.validateToken(token));
    }

    /**
     * 测试从 Token 获取用户名
     */
    @Test
    void getUsernameFromToken() {
        String token = "mock_token";

        when(jwtUtil.getUsernameFromToken(token)).thenReturn("testuser");

        String username = authService.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }
}

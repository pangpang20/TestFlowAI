package com.testflowai;

import com.testflowai.common.BusinessException;
import com.testflowai.common.ResultCode;
import com.testflowai.entity.User;
import com.testflowai.mapper.UserMapper;
import com.testflowai.service.UserService;
import com.testflowai.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试类
 * @author TestFlowAI
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordUtil passwordUtil;

    @InjectMocks
    private UserService userService;

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
     * 测试根据 ID 查询用户成功
     */
    @Test
    void getById_Success() {
        when(userMapper.selectById("user-test-001")).thenReturn(testUser);

        User result = userService.getById("user-test-001");

        assertNotNull(result);
        assertEquals("user-test-001", result.getUserId());
        assertEquals("testuser", result.getUsername());
        // 验证密码被隐藏
        assertNull(result.getPassword());
    }

    /**
     * 测试根据 ID 查询用户 - 用户不存在
     */
    @Test
    void getById_NotFound() {
        when(userMapper.selectById("user-nonexistent")).thenReturn(null);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.getById("user-nonexistent")
        );

        assertEquals(ResultCode.NOT_FOUND.getCode(), exception.getCode());
    }

    /**
     * 测试根据用户名查询用户
     */
    @Test
    void getByUsername() {
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        User result = userService.getByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        // 验证密码被隐藏
        assertNull(result.getPassword());
    }

    /**
     * 测试查询所有用户
     */
    @Test
    void getAll() {
        List<User> userList = Arrays.asList(testUser);
        when(userMapper.selectAll()).thenReturn(userList);

        List<User> result = userService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        // 验证密码被隐藏
        assertNull(result.get(0).getPassword());
    }

    /**
     * 测试创建用户成功
     */
    @Test
    void create_Success() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setEmail("new@example.com");

        // Mock 行为
        when(userMapper.existsByUsername("newuser")).thenReturn(false);
        when(userMapper.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordUtil.encode("password123")).thenReturn("$2a$10$encoded");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // 执行创建
        User result = userService.create(newUser);

        // 验证结果
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        // 验证返回的用户不包含密码
        assertNull(result.getPassword());

        // 验证 insert 被调用
        verify(userMapper).insert(any(User.class));

        // 验证 newUser 对象的密码已被加密（在 create 方法中设置）
        // 注意：由于 create 方法最后会将密码设置为 null 后返回，
        // 所以 newUser 对象的密码也会变成 null（同一个对象引用）
        // 这里改为验证密码确实被加密过
        verify(passwordUtil).encode("password123");
        assertEquals("active", newUser.getStatus());
    }

    /**
     * 测试创建用户 - 用户名已存在
     */
    @Test
    void create_DuplicateUsername() {
        User newUser = new User();
        newUser.setUsername("existing");
        newUser.setPassword("password123");

        when(userMapper.existsByUsername("existing")).thenReturn(true);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.create(newUser)
        );

        assertEquals(ResultCode.DUPLICATE_USERNAME.getCode(), exception.getCode());
    }

    /**
     * 测试创建用户 - 邮箱已存在
     */
    @Test
    void create_DuplicateEmail() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setEmail("existing@example.com");

        when(userMapper.existsByUsername("newuser")).thenReturn(false);
        when(userMapper.existsByEmail("existing@example.com")).thenReturn(true);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.create(newUser)
        );

        assertEquals(ResultCode.DUPLICATE_EMAIL.getCode(), exception.getCode());
    }

    /**
     * 测试更新用户成功
     */
    @Test
    void update_Success() {
        User updateUser = new User();
        updateUser.setUsername("updateduser");
        updateUser.setStatus("disabled");

        when(userMapper.selectById("user-test-001")).thenReturn(testUser);
        when(userMapper.update(any(User.class))).thenReturn(1);

        User result = userService.update("user-test-001", updateUser);

        assertNotNull(result);
        verify(userMapper).update(any(User.class));
    }

    /**
     * 测试更新用户 - 用户不存在
     */
    @Test
    void update_NotFound() {
        User updateUser = new User();
        updateUser.setUsername("updateduser");

        when(userMapper.selectById("user-nonexistent")).thenReturn(null);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.update("user-nonexistent", updateUser)
        );

        assertEquals(ResultCode.NOT_FOUND.getCode(), exception.getCode());
    }

    /**
     * 测试删除用户成功
     */
    @Test
    void delete_Success() {
        when(userMapper.selectById("user-test-001")).thenReturn(testUser);
        when(userMapper.deleteById("user-test-001")).thenReturn(1);

        assertDoesNotThrow(() -> userService.delete("user-test-001"));

        verify(userMapper).deleteById("user-test-001");
    }

    /**
     * 测试删除用户 - 用户不存在
     */
    @Test
    void delete_NotFound() {
        when(userMapper.selectById("user-nonexistent")).thenReturn(null);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.delete("user-nonexistent")
        );

        assertEquals(ResultCode.NOT_FOUND.getCode(), exception.getCode());
    }

    /**
     * 测试密码验证成功
     */
    @Test
    void validatePassword_Success() {
        String rawPassword = "password123";
        String encodedPassword = "$2a$10$encoded";

        when(passwordUtil.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean result = userService.validatePassword(rawPassword, encodedPassword);

        assertTrue(result);
    }

    /**
     * 测试密码验证失败
     */
    @Test
    void validatePassword_Failure() {
        String rawPassword = "wrongpassword";
        String encodedPassword = "$2a$10$encoded";

        when(passwordUtil.matches(rawPassword, encodedPassword)).thenReturn(false);

        boolean result = userService.validatePassword(rawPassword, encodedPassword);

        assertFalse(result);
    }
}

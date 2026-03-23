package com.testflowai.service;

import com.testflowai.common.BusinessException;
import com.testflowai.common.ResultCode;
import com.testflowai.entity.User;
import com.testflowai.mapper.UserMapper;
import com.testflowai.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 用户服务类
 * @author TestFlowAI
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;

    public UserService(UserMapper userMapper, PasswordUtil passwordUtil) {
        this.userMapper = userMapper;
        this.passwordUtil = passwordUtil;
    }

    /**
     * 根据 ID 查询用户
     */
    public User getById(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return user;
    }

    /**
     * 根据用户名查询用户
     */
    public User getByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            // 不返回密码
            user.setPassword(null);
        }
        return user;
    }

    /**
     * 获取所有用户列表
     */
    public List<User> getAll() {
        List<User> users = userMapper.selectAll();
        // 不返回密码
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    /**
     * 创建用户
     */
    @Transactional(rollbackFor = Exception.class)
    public User create(User user) {
        // 检查用户名是否存在
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new BusinessException(ResultCode.DUPLICATE_USERNAME);
        }

        // 检查邮箱是否存在
        if (user.getEmail() != null && userMapper.existsByEmail(user.getEmail())) {
            throw new BusinessException(ResultCode.DUPLICATE_EMAIL);
        }

        // 加密密码
        user.setPassword(passwordUtil.encode(user.getPassword()));

        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus("active");
        }

        // 设置 UUID
        user.setUserId(UUID.randomUUID().toString());

        userMapper.insert(user);
        logger.info("创建用户成功：{}", user.getUsername());

        // 不返回密码
        user.setPassword(null);
        return user;
    }

    /**
     * 更新用户
     */
    @Transactional(rollbackFor = Exception.class)
    public User update(String userId, User user) {
        // 检查用户是否存在
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 如果修改了用户名，检查新用户名是否已存在
        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            if (userMapper.existsByUsername(user.getUsername())) {
                throw new BusinessException(ResultCode.DUPLICATE_USERNAME);
            }
        }

        // 如果修改了邮箱，检查新邮箱是否已存在
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userMapper.existsByEmail(user.getEmail())) {
                throw new BusinessException(ResultCode.DUPLICATE_EMAIL);
            }
        }

        // 如果修改了密码，需要加密
        if (user.getPassword() != null) {
            user.setPassword(passwordUtil.encode(user.getPassword()));
        }

        userMapper.update(user);
        logger.info("更新用户成功：{}", userId);

        // 不返回密码
        user.setPassword(null);
        return user;
    }

    /**
     * 删除用户（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        userMapper.deleteById(userId);
        logger.info("删除用户成功：{}", userId);
    }

    /**
     * 验证用户密码
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordUtil.matches(rawPassword, encodedPassword);
    }
}

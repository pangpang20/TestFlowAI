package com.testflowai.mapper;

import com.testflowai.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface UserMapper {

    /**
     * 根据 ID 查询用户
     * @param userId 用户 ID
     * @return 用户信息
     */
    User selectById(@Param("userId") String userId);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    User selectByEmail(@Param("email") String email);

    /**
     * 查询所有用户
     * @return 用户列表
     */
    List<User> selectAll();

    /**
     * 插入用户
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 更新用户
     * @param user 用户信息
     * @return 影响行数
     */
    int update(User user);

    /**
     * 根据 ID 删除用户（逻辑删除）
     * @param userId 用户 ID
     * @return 影响行数
     */
    int deleteById(@Param("userId") String userId);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(@Param("email") String email);
}

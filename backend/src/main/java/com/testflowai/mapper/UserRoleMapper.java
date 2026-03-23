package com.testflowai.mapper;

import com.testflowai.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface UserRoleMapper {

    /**
     * 根据用户 ID 查询角色列表
     * @param userId 用户 ID
     * @return 角色列表
     */
    List<Role> selectRolesByUserId(@Param("userId") String userId);

    /**
     * 根据角色 ID 查询用户 ID 列表
     * @param roleId 角色 ID
     * @return 用户 ID 列表
     */
    List<String> selectUserIdsByRoleId(@Param("roleId") String roleId);

    /**
     * 插入用户角色关联
     * @param userId 用户 ID
     * @param roleId 角色 ID
     * @return 影响行数
     */
    int insert(@Param("userId") String userId, @Param("roleId") String roleId);

    /**
     * 根据用户 ID 删除角色关联
     * @param userId 用户 ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") String userId);

    /**
     * 根据角色 ID 删除用户关联
     * @param roleId 角色 ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") String roleId);

    /**
     * 批量插入用户角色关联
     * @param userId 用户 ID
     * @param roleIds 角色 ID 列表
     * @return 影响行数
     */
    int batchInsert(@Param("userId") String userId, @Param("roleIds") List<String> roleIds);
}

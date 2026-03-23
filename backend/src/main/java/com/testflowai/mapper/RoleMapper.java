package com.testflowai.mapper;

import com.testflowai.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据 ID 查询角色
     * @param roleId 角色 ID
     * @return 角色信息
     */
    Role selectById(@Param("roleId") String roleId);

    /**
     * 根据角色编码查询角色
     * @param roleName 角色名称
     * @return 角色信息
     */
    Role selectByRoleName(@Param("roleName") String roleName);

    /**
     * 查询所有角色
     * @return 角色列表
     */
    List<Role> selectAll();

    /**
     * 插入角色
     * @param role 角色信息
     * @return 影响行数
     */
    int insert(Role role);

    /**
     * 更新角色
     * @param role 角色信息
     * @return 影响行数
     */
    int update(Role role);

    /**
     * 根据 ID 删除角色（逻辑删除）
     * @param roleId 角色 ID
     * @return 影响行数
     */
    int deleteById(@Param("roleId") String roleId);
}

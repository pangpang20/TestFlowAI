package com.testflowai.mapper;

import com.testflowai.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关联 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface RolePermissionMapper {

    /**
     * 根据角色 ID 查询权限列表
     * @param roleId 角色 ID
     * @return 权限列表
     */
    List<Permission> selectPermissionsByRoleId(@Param("roleId") String roleId);

    /**
     * 根据权限 ID 查询角色 ID 列表
     * @param permissionId 权限 ID
     * @return 角色 ID 列表
     */
    List<String> selectRoleIdsByPermissionId(@Param("permissionId") String permissionId);

    /**
     * 插入角色权限关联
     * @param roleId 角色 ID
     * @param permissionId 权限 ID
     * @return 影响行数
     */
    int insert(@Param("roleId") String roleId, @Param("permissionId") String permissionId);

    /**
     * 根据角色 ID 删除权限关联
     * @param roleId 角色 ID
     * @return 影响行数
     */
    int deleteByRoleId(@Param("roleId") String roleId);

    /**
     * 根据权限 ID 删除角色关联
     * @param permissionId 权限 ID
     * @return 影响行数
     */
    int deleteByPermissionId(@Param("permissionId") String permissionId);

    /**
     * 批量插入角色权限关联
     * @param roleId 角色 ID
     * @param permissionIds 权限 ID 列表
     * @return 影响行数
     */
    int batchInsert(@Param("roleId") String roleId, @Param("permissionIds") List<String> permissionIds);
}

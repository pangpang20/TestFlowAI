package com.testflowai.mapper;

import com.testflowai.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限 Mapper 接口
 * @author TestFlowAI
 */
@Mapper
public interface PermissionMapper {

    /**
     * 根据 ID 查询权限
     * @param permissionId 权限 ID
     * @return 权限信息
     */
    Permission selectById(@Param("permissionId") String permissionId);

    /**
     * 根据权限编码查询权限
     * @param permissionCode 权限编码
     * @return 权限信息
     */
    Permission selectByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 根据用户 ID 查询权限列表
     * @param userId 用户 ID
     * @return 权限列表
     */
    List<Permission> selectByUserId(@Param("userId") String userId);

    /**
     * 查询所有权限
     * @return 权限列表
     */
    List<Permission> selectAll();

    /**
     * 插入权限
     * @param permission 权限信息
     * @return 影响行数
     */
    int insert(Permission permission);

    /**
     * 更新权限
     * @param permission 权限信息
     * @return 影响行数
     */
    int update(Permission permission);

    /**
     * 根据 ID 删除权限（逻辑删除）
     * @param permissionId 权限 ID
     * @return 影响行数
     */
    int deleteById(@Param("permissionId") String permissionId);

    /**
     * 检查权限编码是否存在
     * @param permissionCode 权限编码
     * @return 是否存在
     */
    boolean existsByPermissionCode(@Param("permissionCode") String permissionCode);
}

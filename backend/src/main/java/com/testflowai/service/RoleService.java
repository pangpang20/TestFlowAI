package com.testflowai.service;

import com.testflowai.common.BusinessException;
import com.testflowai.common.ResultCode;
import com.testflowai.entity.Permission;
import com.testflowai.entity.Role;
import com.testflowai.mapper.PermissionMapper;
import com.testflowai.mapper.RoleMapper;
import com.testflowai.mapper.RolePermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 角色服务类
 * @author TestFlowAI
 */
@Service
public class RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    public RoleService(RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper, PermissionMapper permissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
    }

    /**
     * 根据 ID 查询角色
     */
    public Role getById(String roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        return role;
    }

    /**
     * 根据名称查询角色
     */
    public Role getByName(String roleName) {
        return roleMapper.selectByRoleName(roleName);
    }

    /**
     * 获取所有角色列表
     */
    public List<Role> getAll() {
        return roleMapper.selectAll();
    }

    /**
     * 创建角色
     */
    @Transactional(rollbackFor = Exception.class)
    public Role create(Role role) {
        // 检查角色名称是否存在
        if (roleMapper.existsByRoleName(role.getRoleName())) {
            throw new BusinessException("角色名称已存在");
        }

        // 设置 UUID
        role.setRoleId(UUID.randomUUID().toString());

        roleMapper.insert(role);
        logger.info("创建角色成功：{}", role.getRoleName());

        return role;
    }

    /**
     * 更新角色
     */
    @Transactional(rollbackFor = Exception.class)
    public Role update(String roleId, Role role) {
        // 检查角色是否存在
        Role existingRole = roleMapper.selectById(roleId);
        if (existingRole == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }

        // 如果修改了角色名称，检查新名称是否已存在
        if (role.getRoleName() != null && !role.getRoleName().equals(existingRole.getRoleName())) {
            if (roleMapper.existsByRoleName(role.getRoleName())) {
                throw new BusinessException("角色名称已存在");
            }
        }

        role.setRoleId(roleId);
        roleMapper.update(role);
        logger.info("更新角色成功：{}", roleId);

        return role;
    }

    /**
     * 删除角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }

        roleMapper.deleteById(roleId);
        logger.info("删除角色成功：{}", roleId);
    }

    /**
     * 获取角色的权限列表
     */
    public List<Permission> getRolePermissions(String roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        return rolePermissionMapper.selectPermissionsByRoleId(roleId);
    }

    /**
     * 给角色分配权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissionsToRole(String roleId, List<String> permissionIds) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }

        // 先删除角色原有的权限关联
        rolePermissionMapper.deleteByRoleId(roleId);

        // 批量插入新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.batchInsert(roleId, permissionIds);
        }

        logger.info("给角色分配权限成功：roleId={}, permissionIds={}", roleId, permissionIds);
    }

    /**
     * 移除角色的权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeRolePermissions(String roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }

        rolePermissionMapper.deleteByRoleId(roleId);
        logger.info("移除角色权限成功：roleId={}", roleId);
    }
}

package com.testflowai.service;

import com.testflowai.common.BusinessException;
import com.testflowai.common.ResultCode;
import com.testflowai.entity.Permission;
import com.testflowai.mapper.PermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 权限服务类
 * @author TestFlowAI
 */
@Service
public class PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    private final PermissionMapper permissionMapper;

    public PermissionService(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    /**
     * 根据 ID 查询权限
     */
    public Permission getById(String permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "权限不存在");
        }
        return permission;
    }

    /**
     * 根据编码查询权限
     */
    public Permission getByCode(String permissionCode) {
        return permissionMapper.selectByPermissionCode(permissionCode);
    }

    /**
     * 获取所有权限列表
     */
    public List<Permission> getAll() {
        return permissionMapper.selectAll();
    }

    /**
     * 根据用户 ID 获取权限列表
     */
    public List<Permission> getByUserId(String userId) {
        return permissionMapper.selectByUserId(userId);
    }

    /**
     * 创建权限
     */
    @Transactional(rollbackFor = Exception.class)
    public Permission create(Permission permission) {
        // 检查权限编码是否存在
        if (permissionMapper.existsByPermissionCode(permission.getPermissionCode())) {
            throw new BusinessException("权限编码已存在");
        }

        // 设置 UUID
        permission.setPermissionId(UUID.randomUUID().toString());

        permissionMapper.insert(permission);
        logger.info("创建权限成功：{}", permission.getPermissionCode());

        return permission;
    }

    /**
     * 更新权限
     */
    @Transactional(rollbackFor = Exception.class)
    public Permission update(String permissionId, Permission permission) {
        // 检查权限是否存在
        Permission existingPermission = permissionMapper.selectById(permissionId);
        if (existingPermission == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "权限不存在");
        }

        // 如果修改了权限编码，检查新编码是否已存在
        if (permission.getPermissionCode() != null && !permission.getPermissionCode().equals(existingPermission.getPermissionCode())) {
            if (permissionMapper.existsByPermissionCode(permission.getPermissionCode())) {
                throw new BusinessException("权限编码已存在");
            }
        }

        permission.setPermissionId(permissionId);
        permissionMapper.update(permission);
        logger.info("更新权限成功：{}", permissionId);

        return permission;
    }

    /**
     * 删除权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "权限不存在");
        }

        permissionMapper.deleteById(permissionId);
        logger.info("删除权限成功：{}", permissionId);
    }
}

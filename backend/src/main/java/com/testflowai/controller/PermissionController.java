package com.testflowai.controller;

import com.testflowai.common.Result;
import com.testflowai.entity.Permission;
import com.testflowai.service.PermissionService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 获取所有权限列表
     */
    @GetMapping
    public Result<List<Permission>> getAll() {
        List<Permission> permissions = permissionService.getAll();
        return Result.success(permissions);
    }

    /**
     * 根据 ID 获取权限详情
     */
    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable String id) {
        Permission permission = permissionService.getById(id);
        return Result.success(permission);
    }

    /**
     * 创建权限
     */
    @PostMapping
    public Result<Permission> create(@RequestBody CreatePermissionRequest request) {
        Permission permission = new Permission();
        permission.setPermissionCode(request.permissionCode);
        permission.setPermissionName(request.permissionName);
        permission.setResource(request.resource);
        permission.setAction(request.action);
        permission.setDescription(request.description);

        Permission created = permissionService.create(permission);
        return Result.success("创建成功", created);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    public Result<Permission> update(@PathVariable String id, @RequestBody CreatePermissionRequest request) {
        Permission permission = new Permission();
        permission.setPermissionCode(request.permissionCode);
        permission.setPermissionName(request.permissionName);
        permission.setResource(request.resource);
        permission.setAction(request.action);
        permission.setDescription(request.description);

        Permission updated = permissionService.update(id, permission);
        return Result.success("更新成功", updated);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        permissionService.delete(id);
        return Result.success("删除成功", null);
    }

    /**
     * 创建权限请求 DTO
     */
    public static class CreatePermissionRequest {
        @NotBlank(message = "权限编码不能为空")
        private String permissionCode;

        @NotBlank(message = "权限名称不能为空")
        private String permissionName;

        private String resource;
        private String action;
        private String description;

        public String getPermissionCode() {
            return permissionCode;
        }

        public void setPermissionCode(String permissionCode) {
            this.permissionCode = permissionCode;
        }

        public String getPermissionName() {
            return permissionName;
        }

        public void setPermissionName(String permissionName) {
            this.permissionName = permissionName;
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

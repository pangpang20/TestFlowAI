package com.testflowai.controller;

import com.testflowai.common.Result;
import com.testflowai.entity.Role;
import com.testflowai.service.RoleService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 * @author TestFlowAI
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取所有角色列表
     */
    @GetMapping
    public Result<List<Role>> getAll() {
        List<Role> roles = roleService.getAll();
        return Result.success(roles);
    }

    /**
     * 根据 ID 获取角色详情
     */
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable String id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<Role> create(@RequestBody CreateRoleRequest request) {
        Role role = new Role();
        role.setRoleName(request.roleName);
        role.setDisplayName(request.displayName);
        role.setDescription(request.description);

        Role created = roleService.create(role);
        return Result.success("创建成功", created);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<Role> update(@PathVariable String id, @RequestBody CreateRoleRequest request) {
        Role role = new Role();
        role.setRoleName(request.roleName);
        role.setDisplayName(request.displayName);
        role.setDescription(request.description);

        Role updated = roleService.update(id, role);
        return Result.success("更新成功", updated);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        roleService.delete(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/{id}/permissions")
    public Result<List<com.testflowai.entity.Permission>> getRolePermissions(@PathVariable String id) {
        List<com.testflowai.entity.Permission> permissions = roleService.getRolePermissions(id);
        return Result.success(permissions);
    }

    /**
     * 给角色分配权限
     */
    @PostMapping("/{id}/permissions")
    public Result<Void> assignPermissionsToRole(@PathVariable String id, @RequestBody AssignPermissionsRequest request) {
        roleService.assignPermissionsToRole(id, request.permissionIds);
        return Result.success("分配成功", null);
    }

    /**
     * 创建角色请求 DTO
     */
    public static class CreateRoleRequest {
        @NotBlank(message = "角色名称不能为空")
        private String roleName;

        private String displayName;
        private String description;

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * 分配权限请求 DTO
     */
    public static class AssignPermissionsRequest {
        private List<String> permissionIds;

        public List<String> getPermissionIds() {
            return permissionIds;
        }

        public void setPermissionIds(List<String> permissionIds) {
            this.permissionIds = permissionIds;
        }
    }
}

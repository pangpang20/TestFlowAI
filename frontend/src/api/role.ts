import request from '@/utils/request'

export interface Role {
  roleId: string
  roleName: string
  displayName: string
  description: string
  createdAt: string
  updatedAt: string
}

export interface CreateRoleDto {
  roleName: string
  displayName: string
  description: string
}

// 获取角色列表
export function getRolesApi() {
  return request({
    url: '/api/roles',
    method: 'get'
  })
}

// 获取角色详情
export function getRoleDetailApi(id: string) {
  return request({
    url: `/api/roles/${id}`,
    method: 'get'
  })
}

// 创建角色
export function createRoleApi(data: CreateRoleDto) {
  return request({
    url: '/api/roles',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRoleApi(id: string, data: CreateRoleDto) {
  return request({
    url: `/api/roles/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRoleApi(id: string) {
  return request({
    url: `/api/roles/${id}`,
    method: 'delete'
  })
}

// 获取角色的权限列表
export function getRolePermissionsApi(id: string) {
  return request({
    url: `/api/roles/${id}/permissions`,
    method: 'get'
  })
}

// 给角色分配权限
export function assignPermissionsToRoleApi(id: string, permissionIds: string[]) {
  return request({
    url: `/api/roles/${id}/permissions`,
    method: 'post',
    data: { permissionIds }
  })
}

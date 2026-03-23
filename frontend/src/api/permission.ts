import request from '@/utils/request'

export interface Permission {
  permissionId: string
  permissionCode: string
  permissionName: string
  resource: string
  action: string
  description: string
  createdAt: string
  updatedAt: string
}

export interface CreatePermissionDto {
  permissionCode: string
  permissionName: string
  resource: string
  action: string
  description: string
}

// 获取权限列表
export function getPermissionsApi() {
  return request({
    url: '/api/permissions',
    method: 'get'
  })
}

// 获取权限详情
export function getPermissionDetailApi(id: string) {
  return request({
    url: `/api/permissions/${id}`,
    method: 'get'
  })
}

// 创建权限
export function createPermissionApi(data: CreatePermissionDto) {
  return request({
    url: '/api/permissions',
    method: 'post',
    data
  })
}

// 更新权限
export function updatePermissionApi(id: string, data: CreatePermissionDto) {
  return request({
    url: `/api/permissions/${id}`,
    method: 'put',
    data
  })
}

// 删除权限
export function deletePermissionApi(id: string) {
  return request({
    url: `/api/permissions/${id}`,
    method: 'delete'
  })
}

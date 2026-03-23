import request from '@/utils/request'
import type { UserInfo } from '@/stores/user'

export interface User {
  userId: string
  username: string
  email: string
  avatar: string
  status: string
  createdAt: string
  updatedAt: string
}

export interface CreateUpdateUserDto {
  username: string
  password?: string
  email: string
  status?: string
}

// 获取用户信息
export function getUserInfoApi() {
  return request<UserInfo>({
    url: '/api/user/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data: Partial<UserInfo>) {
  return request({
    url: '/api/user/info',
    method: 'put',
    data
  })
}

// 获取用户列表
export function getUsersApi() {
  return request<User[]>({
    url: '/api/users',
    method: 'get'
  })
}

// 获取用户详情
export function getUserDetailApi(id: string) {
  return request<User>({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

// 创建用户
export function createUserApi(data: CreateUpdateUserDto) {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUserApi(id: string, data: CreateUpdateUserDto) {
  return request({
    url: `/api/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUserApi(id: string) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

// 获取用户角色
export function getUserRolesApi(id: string) {
  return request({
    url: `/api/users/${id}/roles`,
    method: 'get'
  })
}

// 分配角色给用户
export function assignRolesToUserApi(id: string, roleIds: string[]) {
  return request({
    url: `/api/users/${id}/roles`,
    method: 'post',
    data: { roleIds }
  })
}

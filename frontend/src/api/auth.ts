import request from '@/utils/request'
import type { UserInfo } from '@/stores/user'

// 登录接口
export function loginApi(username: string, password: string) {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data: { username, password }
  })
}

// 登出接口
export function logoutApi() {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

// 获取用户信息
export function getUserInfoApi() {
  return request<UserInfo>({
    url: '/api/users/info',
    method: 'get'
  })
}

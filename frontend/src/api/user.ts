import request from '@/utils/request'
import type { UserInfo } from '@/stores/user'

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

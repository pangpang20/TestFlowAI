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

// 上传头像
export function uploadAvatarApi(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request<{ avatarUrl: string }>({
    url: '/api/files/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 修改密码
export function changePasswordApi(oldPassword: string, newPassword: string) {
  return request({
    url: '/api/auth/change-password',
    method: 'post',
    data: { oldPassword, newPassword }
  })
}

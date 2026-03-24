import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, logoutApi, getUserInfoApi } from '@/api/auth'

export interface UserInfo {
  id: number
  username: string
  email: string
  role: string
  avatar?: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const role = computed(() => userInfo.value?.role || '')

  // 登录
  async function login(username: string, password: string) {
    try {
      const res = await loginApi(username, password)
      // 后端返回格式：{ code, message, data: { token, user }, timestamp }
      const tokenValue = res.data?.token || res.token
      if (!tokenValue) {
        throw new Error('Token not found in response')
      }
      token.value = tokenValue
      await getUserInfo()
      return res
    } catch (error) {
      console.error('Login error:', error)
      throw error
    }
  }

  // 获取用户信息
  async function getUserInfo() {
    try {
      const res = await getUserInfoApi()
      // 后端返回格式：{ code, message, data: { userInfo }, timestamp }
      userInfo.value = res.data || res
      return res
    } catch (error) {
      throw error
    }
  }

  // 登出
  async function logout() {
    try {
      await logoutApi()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      token.value = ''
      userInfo.value = null
    }
  }

  // 检查登录状态
  function checkAuth() {
    if (!token.value) {
      return false
    }
    // 可以在这里添加 token 过期检查逻辑
    return true
  }

  // 重置 store
  function reset() {
    token.value = ''
    userInfo.value = null
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    username,
    role,
    login,
    getUserInfo,
    logout,
    checkAuth,
    reset
  }
})

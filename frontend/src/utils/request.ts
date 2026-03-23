import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  (config: any) => {
    const userStore = useUserStore()
    const token = userStore.token
    console.log('[Request] URL:', config.baseURL + config.url)
    console.log('[Request] Method:', config.method)
    console.log('[Request] Headers:', config.headers)
    if (token) {
      config.headers = config.headers || {}
      config.headers['Authorization'] = `Bearer ${token}`
      console.log('[Request] Token:', token.substring(0, 20) + '...')
    }
    return config
  },
  (error: AxiosError) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    console.log('[Response] Status:', response.status, 'URL:', response.config.url)
    console.log('[Response] Data:', response.data)
    const res = response.data
    return res
  },
  (error: AxiosError) => {
    console.error('[Response Error] Full error:', error)
    console.error('[Response Error] Config:', error.config)
    console.error('[Response Error] Response:', error.response)

    if (error.response) {
      const status = error.response.status
      console.error('[Response Error] Status:', status)
      console.error('[Response Error] Data:', error.response.data)

      if (status === 401) {
        const userStore = useUserStore()
        userStore.logout()
        ElMessage.error('登录已过期，请重新登录')
      } else if (status === 403) {
        ElMessage.error('没有权限访问该资源')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (status === 500) {
        ElMessage.error('服务器错误')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请检查网络')
    } else {
      console.error('[Response Error] No response, network error:', error.message)
      ElMessage.error('网络错误，请检查网络连接')
    }

    return Promise.reject(error)
  }
)

export default service

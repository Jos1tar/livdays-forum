// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
// 后端地址来自 .env.development / .env.production 里的 VITE_API_BASE_URL
// 以前用过的地址：http://43.165.3.68:8080/ 、 http://118.25.14.2:8080/
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/',
  timeout: 120000
})

const goLogin = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.replace('/')   // 登录页的路由是 '/'（之前写成了不存在的 '/login'）
}

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')


    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }

    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    // 根据业务状态码处理响应
    if (res && res.code && res.code !== 200) {
      // 处理各种错误状态
      switch (res.code) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          goLogin()
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 500:
          ElMessage.error('服务器错误，请稍后再试')
          break
        default:
          ElMessage.error(res.msg || '未知错误')
      }
      return Promise.reject(res)
    }
    
    return res
  },
  error => {
    // 处理HTTP错误
    if (error.response) {
      const { status } = error.response
      
      switch (status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          goLogin()
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误，请稍后再试')
          break
        default:
          ElMessage.error(`请求失败: ${status}`)
      }
    } else if (error.message && error.message.includes('timeout')) {
      ElMessage.error('请求超时，请稍后再试')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default service
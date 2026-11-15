// src/api/modules/user.js
import request from '@/utils/request'

export const useUserApi = () => {
  // 登录
  const login = (data) => {
    return request({
      url: '/login',
      method: 'post',
      data
    })
  }
  
  // 退出登录
  const logout = () => {
    return request({
      url: '/logout',
      method: 'post'
    })
  }
  
  // 查看用户信息
  const getUserInfo = (id) => {
    return request({
      url: `/user/userInfo/${id}`,
      method: 'get'
    })
  }
  
  // 更新用户信息
  const updateUserInfo = (data) => {
    return request({
      url: `/user/userInfo/${data.id}`,
      method: 'put',
      data
    })
  }
  const getVerification = (data) => {
    return request({
      url: '/user/send-verification-code',
      method: 'post',
      // 用 params 让 axios 做 URL 编码（邮箱里的 + 号等字符直接拼接会出错）
      params: { email: data.email }
    })
  }
  
  // 注册用户
  const register = (data) => {
    return request({
      url: '/user/register',
      method: 'post',
      data
    })
  }
  
  return {
    login,
    logout,
    getUserInfo,
    updateUserInfo,
    register,
    getVerification
  }
}
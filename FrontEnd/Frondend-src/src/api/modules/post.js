// src/api/modules/post.js
import request from '@/utils/request'

export const usePostApi = () => {
  // 获取帖子详情
  const getPostDetail = (id) => {
    return request({
      url: `/posts/${id}`,
      method: 'get'
    })
  }
  
  // 分页查询帖子
  const getPostList = (params) => {
    return request({
      url: '/posts/list',
      method: 'get',
      params
    })
  }
  
  // 发布帖子
  const createPost = (data) => {
    return request({
      url: '/posts',
      method: 'post',
      data
    })
  }
  
  // 查看我的帖子
  const getMyPosts = (userId) => {
    return request({
      url: `user/userInfo/${userId}/posts`,
      method: 'get'
    })
  }
  
  // 点赞帖子
  // 点赞人由后端从 token 里取，不需要传 userId
  const likePost = (data) => {
    return request({
      url: `/like`,
      method: 'post',
      data: { targetId: data.postId, type: 'post' }
    })
  }
  
  return {
    getPostDetail,
    getPostList,
    createPost,
    getMyPosts,
    likePost
  }
}
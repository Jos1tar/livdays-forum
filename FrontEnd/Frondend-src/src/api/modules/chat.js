// src/api/modules/comment.js
import request from '@/utils/request'


export const chatApi = () => {
    // 发送私聊消息
// data:   {
    //    "receiverId": "",    
    //    "content": ""  
//    }
  const sendMessage = (data) => {
    return request({
      url: '/message/send',
      method: 'post',
      data
    })
  }

  //获取聊天记录
    //params: {
    //    page: 1,
    //    pageSize:999,
    //    userId: '',
    // targetUserId:""
    //}
  // 当前用户由后端从 token 里取，只需要传分页参数
  const messageList = (params) => {
    return request({
      url: `/message/list/${params.targetUserId}`,
      method: 'get',
      params: { page: params.page, pageSize: params.pageSize }
    })
  }

  // 会话列表
  const conversations = () => request({ url: '/message/conversations', method: 'get' })

  // 未读消息总数
  const unreadCount = () => request({ url: '/message/unread', method: 'get' })

  // 标记某个会话已读
  const markAsRead = (conversationId) => request({ url: `/message/read/${conversationId}`, method: 'put' })

  // 删除会话
  const deleteConversation = (conversationId) => request({ url: `/message/${conversationId}`, method: 'delete' })

  return {
    sendMessage,
    messageList,
    conversations,
    unreadCount,
    markAsRead,
    deleteConversation,
  }
}
// src/api/modules/post.js
import request from '@/utils/request'

export const useUploadApi = () => {
  // 上传文件，参数是 File 对象（例如 <input type="file"> 拿到的），返回 OSS 上的图片 URL
  const uploadFile = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/upload',
      method: 'post',
      data: formData
    })
  }

  return {
    uploadFile,
    useUploadApi: uploadFile // 旧名字，保留兼容
  }
}
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器：【核心修改】不再自动弹框，把错误抛给前端处理
request.interceptors.response.use(
  response => {
    // 正常返回：直接返回 data
    return response.data
  },
  error => {
    // 错误返回：只打印日志，不弹框（前端自己处理）
    console.error('请求异常：', error)
    return Promise.reject(error)
  }
)

export default request
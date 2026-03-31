import { createApp } from 'vue'
// 导入Element Plus UI库
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 导入根组件、路由、请求工具
import App from './App.vue'
import router from './router'
import request from './utils/request'

// 创建Vue实例
const app = createApp(App)

// 注册插件
app.use(router)
app.use(ElementPlus)

// 全局挂载请求工具，所有页面都可以通过this.$request调用
app.config.globalProperties.$request = request

// 挂载到页面
app.mount('#app')
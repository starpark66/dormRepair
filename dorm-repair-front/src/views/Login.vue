<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <h2 class="login-title">宿舍报修管理系统</h2>

      <el-tabs v-model="activeTab" type="card" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px" size="large">
            <el-form-item label="账号" prop="userId">
              <el-input v-model="loginForm.userId" placeholder="请输入学号/工号" clearable />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="submit-btn" @click="handleLogin" :loading="loading">
                登录
              </el-button>
            </el-form-item>
          </el-form>
          <div class="tip-text">
            <p>学生账号：3125/3225开头10位学号</p >
            <p>维修人员：0025开头10位工号</p >
            <p>管理员账号：0025000001 | 初始密码：123456</p >
          </div>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="80px" size="large">
            <el-form-item label="账号" prop="userId">
              <el-input v-model="registerForm.userId" placeholder="请输入学号/工号" clearable />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="请设置密码" show-password clearable />
            </el-form-item>
            <el-form-item label="确认密码" prop="rePassword">
              <el-input v-model="registerForm.rePassword" type="password" placeholder="请再次输入密码" show-password clearable />
            </el-form-item>
            <el-form-item label="姓名" prop="name">
              <el-input v-model="registerForm.name" placeholder="请输入真实姓名" clearable />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="registerForm.phone" placeholder="请输入手机号" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="submit-btn" @click="handleRegister" :loading="registerLoading">
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const activeTab = ref('login')

const loginForm = ref({
  userId: '',
  password: ''
})
const loading = ref(false)
const loginFormRef = ref(null)

const registerForm = ref({
  userId: '',
  password: '',
  rePassword: '',
  name: '',
  phone: ''
})
const registerLoading = ref(false)
const registerFormRef = ref(null)

const roleHomeMap = {
  1: '/student/dorm-bind',
  2: '/repair/task-current',
  3: '/admin/repair-all'
}

const loginRules = ref({
  userId: [
    { required: true, message: '账号不能为空', trigger: 'blur' },
    { 
      pattern: /^(3125|3225|0025)\d{6}$/, 
      message: '账号格式错误（学生：3125/3225开头；维修：0025开头+6位数字）', 
      trigger: 'blur' 
    }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { pattern: /^\w{6,16}$/, message: '密码需6-16位字母、数字、下划线', trigger: 'blur' }
  ]
})

const registerRules = ref({
  userId: [
    { required: true, message: '账号不能为空', trigger: 'blur' },
    { pattern: /^(3125|3225|0025)\d{6}$/, message: '账号格式错误', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { pattern: /^\w{6,16}$/, message: '密码需6-16位字母、数字、下划线', trigger: 'blur' }
  ],
  rePassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.value.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }],
  phone: [
    { required: true, message: '手机号不能为空', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ]
})

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const res = await request.post('/users/login', loginForm.value)
    
    // 后端返回 Result 结构：{ code, msg, data }
    if (res.code === 200) {
      localStorage.setItem('token', res.data)
      localStorage.setItem('userId', loginForm.value.userId)
      
      let role = 1
      if (loginForm.value.userId.startsWith('0025')) {
        role = loginForm.value.userId === '0025000001' ? 3 : 2
      }
      localStorage.setItem('userRole', role)
      
      ElMessage.success('登录成功')
      router.push(roleHomeMap[role])
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (error) {
    console.error('登录失败：', error)
    // 显示后端真实错误信息
    const msg = error.response?.data?.msg || error.message || '登录失败，请检查账号密码'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    registerLoading.value = true

    let role = 1
    if (registerForm.value.userId.startsWith('0025')) {
      role = 2 // 注册默认维修人员，或按你的逻辑改为1
    }
    const registerData = { ...registerForm.value, role }
    
    const res = await request.post('/users/register', registerData)
    
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      activeTab.value = 'login'
      registerForm.value = { userId: '', password: '', rePassword: '', name: '', phone: '' }
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  } catch (error) {
    console.error('注册失败：', error)
    const msg = error.response?.data?.msg || error.message || '注册失败'
    ElMessage.error(msg)
  } finally {
    registerLoading.value = false
  }
}

onMounted(() => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('userRole')
  if (token && role) {
    router.push(roleHomeMap[role])
  }
})
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  width: 450px;
  padding: 20px;
}
.login-title {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}
.login-tabs {
  margin-bottom: 10px;
}
.submit-btn {
  width: 100%;
}
.tip-text {
  margin-top: 20px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}
.tip-text p {
  margin: 5px 0;
}
</style>
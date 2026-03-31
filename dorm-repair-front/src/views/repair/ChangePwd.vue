<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">修改密码</div>
      </template>

      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="120px" size="large" style="max-width: 500px; margin: 0 auto;">
        <el-form-item label="当前旧密码" prop="oldPwd">
          <el-input v-model="pwdForm.oldPwd" type="password" placeholder="请输入当前旧密码" show-password clearable />
        </el-form-item>
        <el-form-item label="新密码" prop="newPwd">
          <el-input v-model="pwdForm.newPwd" type="password" placeholder="请输入新密码（6-16位）" show-password clearable />
        </el-form-item>
        <el-form-item label="确认新密码" prop="reNewPwd">
          <el-input v-model="pwdForm.reNewPwd" type="password" placeholder="请再次输入新密码" show-password clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="loading">
            确认修改
          </el-button>
          <el-button @click="resetForm">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
// 原有：获取当前登录学号
const userId = ref(localStorage.getItem('userId'))
const loading = ref(false)
const pwdFormRef = ref(null)

// 原有：密码表单
const pwdForm = ref({
  oldPwd: '',
  newPwd: '',
  reNewPwd: ''
})

// 原有：表单校验规则
const pwdRules = ref({
  oldPwd: [{ required: true, message: '当前旧密码不能为空', trigger: 'blur' }],
  newPwd: [
    { required: true, message: '新密码不能为空', trigger: 'blur' },
    // 【修改】对接后端RegEx的密码规则
    { pattern: /^\w{6,16}$/, message: '密码长度需在6-16位之间', trigger: 'blur' }
  ],
  reNewPwd: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.value.newPwd) {
          callback(new Error('两次输入的新密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 原有：修改密码
const handleUpdate = async () => {
  try {
    await pwdFormRef.value.validate()
    if (pwdForm.value.oldPwd === pwdForm.value.newPwd) {
      ElMessage.warning('新密码不能与旧密码相同')
      return
    }
    // 【修改】对接后端接口：/api/users/updatePwd PUT
    await request.put('/users/updatePwd', null, {
      params: { userId: userId.value, newPassword: pwdForm.value.newPwd }
    })
    ElMessage.success('密码修改成功，请重新登录')
    localStorage.clear()
    router.push('/login')
  } catch (error) {
    console.error('修改失败：', error)
  }
}

// 原有：重置表单
const resetForm = () => {
  pwdFormRef.value.resetFields()
  pwdForm.value = { oldPwd: '', newPwd: '', reNewPwd: '' }
}
</script>

<style scoped>
.page-container {
  padding: 10px;
}
.card-header {
  font-weight: bold;
  font-size: 16px;
}
</style>
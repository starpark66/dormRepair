<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">绑定/修改宿舍信息</div>
      </template>

      <el-form :model="dormForm" :rules="dormRules" ref="dormFormRef" label-width="100px" size="large" style="max-width: 500px; margin: 0 auto;">
        <el-form-item label="学生学号" prop="studentId">
          <el-input v-model="dormForm.studentId" disabled placeholder="自动填充当前登录学号"></el-input>
        </el-form-item>
        <el-form-item label="宿舍区域" prop="dormArea">
          <el-select v-model="dormForm.dormArea" placeholder="请选择宿舍区域" style="width: 100%;">
            <el-option label="西区" value="西区" />
            <el-option label="东区" value="东区" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋号" prop="dormBuilding">
          <el-input v-model="dormForm.dormBuilding" placeholder="请输入楼栋号（如：1栋）" clearable />
        </el-form-item>
        <el-form-item label="房间号" prop="dormRoom">
          <el-input v-model="dormForm.dormRoom" placeholder="请输入房间号（如：101）" clearable />
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input v-model="dormForm.studentName" placeholder="请输入你的姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleBind" :loading="loading">
            提交绑定
          </el-button>
          <el-button @click="handleQuery">
            查询我的宿舍
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider />

      <div v-if="dormInfo" class="dorm-info">
        <h4>我的宿舍信息</h4>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="宿舍区域">{{ dormInfo.dormArea }}</el-descriptions-item>
          <el-descriptions-item label="楼栋号">{{ dormInfo.dormBuilding }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ dormInfo.dormRoom }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ dormInfo.studentName }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-empty v-else description="暂无绑定的宿舍信息" :image-size="100" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

// 原有：获取当前登录学号
const userId = ref(localStorage.getItem('userId'))
const loading = ref(false)
const dormInfo = ref(null)
const dormFormRef = ref(null)

// 原有：宿舍表单
const dormForm = ref({
  studentId: userId.value,
  dormArea: '',
  dormBuilding: '',
  dormRoom: '',
  studentName: ''
})

// 原有：表单校验规则
const dormRules = ref({
  studentId: [
    { required: true, message: '学号不能为空', trigger: 'blur' },
    // 【修改】对接后端RegEx的学号规则
    { pattern: /^(3125|3225)\d{6}$/, message: '学号格式错误', trigger: 'blur' }
  ],
  dormRoom: [
    { required: true, message: '宿舍房号不能为空', trigger: 'blur' },
    // 【修改】对接后端RegEx的宿舍房号规则：3位数字
    { pattern: /^[1-7][0-3][1-9]$/, message: '房号格式错误（3位数字，如131、729）', trigger: 'blur' }
  ],
  dormArea: [{ required: true, message: '宿舍区域不能为空', trigger: 'blur' }],
  dormBuilding: [{ required: true, message: '宿舍楼不能为空', trigger: 'blur' }],
  studentName: [{ required: true, message: '姓名不能为空', trigger: 'blur' }]
})

// 原有：绑定/修改宿舍
const handleBind = async () => {
  try {
    await dormFormRef.value.validate()
    // 【修改】对接后端接口：/api/studentDorm/bind POST
    await request.post('/studentDorm/bind', dormForm.value)
    ElMessage.success('宿舍绑定/修改成功')
    handleQuery()
  } catch (error) {
    console.error('绑定失败：', error)
  }
}

// 原有：查询宿舍信息
const handleQuery = async () => {
  try {
    // 【修改】对接后端接口：/api/studentDorm/info/{studentId} GET
    const res = await request.get(`/studentDorm/info/${userId.value}`)
    if (res.data) {
      dormInfo.value = res.data
      dormForm.value = { ...dormForm.value, ...res.data }
    }
  } catch (err) {
    dormInfo.value = null
  }
}

// 原有：页面加载查询
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.page-container {
  padding: 10px;
}
.card-header {
  font-weight: bold;
  font-size: 16px;
}
.dorm-info {
  padding: 0 20px;
}
</style>
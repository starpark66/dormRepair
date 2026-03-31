<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">分配报修任务</div>
      </template>

      <el-form :model="assignForm" label-width="120px" size="large" style="max-width: 500px; margin: 0 auto 30px;">
        <el-form-item label="报修单ID">
          <el-input v-model="assignForm.repairId" placeholder="请输入要分配的报修单ID" clearable />
        </el-form-item>
        <el-form-item label="维修人员账号">
          <el-input v-model="assignForm.repairUserId" placeholder="请输入维修人员工号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAssign" :loading="loading">
            确认分配
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider content-position="left">待分配报修单列表</el-divider>
      <el-table :data="waitAssignList" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="repairId" label="报修ID" width="100" />
        <el-table-column prop="stuUserId" label="学生账号" width="120" />
        <el-table-column prop="deviceType" label="设备类型" width="180" />
        <el-table-column prop="description" label="故障描述" min-width="200" />
        <el-table-column prop="urgency" label="紧急程度" width="100">
          <template #default="scope">
            <el-tag :type="urgencyType[scope.row.urgency]">{{ urgencyText[scope.row.urgency] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="primary" text @click="fillForm(scope.row)">
              选择
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const waitAssignList = ref([])

const assignForm = ref({
  repairId: '',
  repairUserId: ''
})

// 原有：紧急程度映射
const urgencyText = { 1: '普通', 2: '紧急', 3: '非常紧急' }
const urgencyType = { 1: 'info', 2: 'warning', 3: 'danger' }

// 原有：填充表单
const fillForm = (row) => {
  assignForm.value.repairId = row.repairId
}

// 原有：分配任务
const handleAssign = async () => {
  const { repairId, repairUserId } = assignForm.value
  if (!repairId || !repairUserId) {
    ElMessage.warning('报修单ID和维修人员账号不能为空')
    return
  }
  loading.value = true
  try {
    // 【修改】对接后端接口：/api/repairs/assign/{repairId} POST
    await request.post(`/repairs/assign/${repairId}`, null, {
      params: { repairUserId: repairUserId }
    })
    ElMessage.success('报修单分配成功')
    assignForm.value = { repairId: '', repairUserId: '' }
    handleQuery()
  } finally {
    loading.value = false
  }
}

// 原有：查询待分配的报修单
const handleQuery = async () => {
  try {
    // 【修改】对接后端接口：/api/repairs/status/0 GET
    const res = await request.get('/repairs/status/0')
    waitAssignList.value = res.data || []
  } catch (err) {
    waitAssignList.value = []
  }
}

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
</style>
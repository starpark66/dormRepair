<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">删除报修单</div>
      </template>

      <el-alert
        title="仅待处理、已取消状态的报修单可以删除"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px;"
      />

      <el-form :model="deleteForm" label-width="150px" size="large" style="max-width: 500px; margin: 0 auto 30px;">
        <el-form-item label="报修单ID">
          <el-input v-model="deleteForm.repairId" placeholder="请输入要删除的报修单ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="handleDelete" :loading="loading">
            确认删除
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider content-position="left">可删除报修单列表</el-divider>
      <el-table :data="deleteList" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="repairId" label="报修ID" width="100" />
        <el-table-column prop="stuUserId" label="学生账号" width="120" />
        <el-table-column prop="deviceType" label="设备类型" width="180" />
        <el-table-column prop="status" label="当前状态" width="100">
          <template #default="scope">
            <el-tag :type="statusType[scope.row.status]">{{ statusText[scope.row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="danger" text @click="fillForm(scope.row)">
              选择删除
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
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const deleteList = ref([])

const deleteForm = ref({
  repairId: ''
})

const statusText = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已取消' }
const statusType = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }

// 查询可删除列表
const handleQuery = async () => {
  try {
    const res = await request.get('/repairs/all')
    deleteList.value = (res.data || []).filter(item => item.status === 0 || item.status === 3)
  } catch (err) {
    deleteList.value = []
  }
}

// 填充ID
const fillForm = (row) => {
  deleteForm.value.repairId = row.repairId
}

// 真正安全的删除（前端校验 + 后端校验）
const handleDelete = async () => {
  const id = deleteForm.value.repairId
  if (!id) {
    ElMessage.warning('请输入报修单ID')
    return
  }

  // 前端先校验：是否在可删除列表里
  const target = deleteList.value.find(item => item.repairId == id)
  if (!target) {
    ElMessage.error('无法删除！该报修单不是【待处理】或【已取消】状态')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除报修单【${id}】吗？`, '提示', {
      type: 'warning'
    })

    loading.value = true
    await request.post(`/repairs/admin/cancel/${id}`)
    ElMessage.success('删除成功！')
    deleteForm.value.repairId = ''
    handleQuery()
  } catch (e) {
    ElMessage.error('删除失败：该报修单无法删除')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.page-container { padding: 10px; }
.card-header { font-weight: bold; font-size: 16px; }
</style>
<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">删除维修人员</div>
      </template>

      <el-form :model="deleteForm" label-width="150px" size="large" style="max-width: 500px; margin: 0 auto 30px;">
        <el-form-item label="维修人员工号">
          <el-input v-model="deleteForm.userId" placeholder="请输入要删除的维修人员工号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="handleDelete" :loading="loading">
            确认删除
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider content-position="left">维修人员列表</el-divider>
      <el-table :data="repairmanList" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="userId" label="维修人员工号" width="100%" />
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
const repairmanList = ref([])

const deleteForm = ref({
  userId: ''
})

// 原有：填充表单
const fillForm = (row) => {
  deleteForm.value.userId = row.userId
}

// 原有：删除维修人员
const handleDelete = () => {
  if (!deleteForm.value.userId) {
    ElMessage.warning('请输入维修人员工号')
    return
  }
  ElMessageBox.confirm(`确定要删除维修人员【${deleteForm.value.userId}】吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      // 【修改】对接后端接口：/api/users/repairman/delete DELETE
      await request.delete('/users/repairman/delete', {
        params: { userId: deleteForm.value.userId }
      })
      ElMessage.success('删除成功')
      deleteForm.value = { userId: '' }
      handleQuery()
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

// 原有：查询维修人员列表
const handleQuery = async () => {
  try {
    // 【修改】对接后端接口：/api/users/repairman/list GET
    const res = await request.get('/users/repairman/list')
    repairmanList.value = (res.data || []).map(item => ({
      userId: item.user_id
    }))
  } catch (err) {
    repairmanList.value = []
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
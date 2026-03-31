<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">取消报修单</div>
      </template>

      <el-alert
        title="仅待处理状态的报修单可以取消"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px;"
      />

      <el-table :data="cancelList" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="repairId" label="报修ID" width="100" />
        <el-table-column prop="deviceType" label="设备类型" width="180" />
        <el-table-column prop="description" label="故障描述" min-width="200" />
        <el-table-column prop="createTime" label="报修时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="danger" text @click="handleCancel(scope.row)">
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="cancelList.length === 0" description="暂无可以取消的报修单" :image-size="120" style="margin-top: 20px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// 原有：获取当前登录学号
const userId = ref(localStorage.getItem('userId'))
const cancelList = ref([])

// 原有：查询可取消的报修单
const handleQuery = async () => {
  try {
    // 【修改】对接后端接口：/api/repairs/my/{stuUserId} GET
    const res = await request.get(`/repairs/my/${userId.value}`)
    // 原有：只保留待处理（状态0）的报修单
    cancelList.value = (res.data || []).filter(item => item.status === 0)
  } catch (err) {
    cancelList.value = []
  }
}

// 原有：取消报修单
const handleCancel = (row) => {
  ElMessageBox.confirm(`确定要取消报修ID【${row.repairId}】的报修单吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 【修改】对接后端接口：/api/repairs/cancel/{repairId} POST，带stuUserId参数
      await request.post(`/repairs/cancel/${row.repairId}`, null, {
        params: { stuUserId: userId.value }
      })
      ElMessage.success('报修单取消成功')
      handleQuery()
    } catch (err) {}
  }).catch(() => {})
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
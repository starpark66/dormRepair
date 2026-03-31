<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的报修记录</span>
          <el-button type="primary" @click="handleQuery">刷新</el-button>
        </div>
      </template>

      <el-table :data="repairList" stripe style="width: 100%">
        <el-table-column prop="repairId" label="报修ID" width="100" />
        <el-table-column prop="deviceType" label="设备类型" width="180" />
        <el-table-column prop="description" label="故障描述" min-width="200" />
        <el-table-column prop="urgency" label="紧急程度" width="100">
          <template #default="scope">
            <el-tag :type="urgencyType[scope.row.urgency]">{{ urgencyText[scope.row.urgency] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="当前状态" width="100">
          <template #default="scope">
            <el-tag :type="statusType[scope.row.status]">{{ statusText[scope.row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="repairUserId" label="维修人员" width="120">
          <template #default="scope">
            {{ scope.row.repairUserId || '暂无分配' }}
          </template>
        </el-table-column>
        <el-table-column prop="score" label="评分" width="80">
          <template #default="scope">
            {{ scope.row.score ? scope.row.score + '分' : '暂无' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报修时间" width="180" />
      </el-table>

      <el-empty v-if="repairList.length === 0" description="暂无报修记录" :image-size="120" style="margin-top: 20px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

// 原有：获取当前登录学号
const userId = ref(localStorage.getItem('userId'))
const repairList = ref([])

// 原有：状态/紧急程度映射
const statusText = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已取消' }
const statusType = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
const urgencyText = { 1: '普通', 2: '紧急', 3: '非常紧急' }
const urgencyType = { 1: 'info', 2: 'warning', 3: 'danger' }

// 原有：查询我的报修记录
const handleQuery = async () => {
  try {
    // 【修改】对接后端接口：/api/repairs/my/{stuUserId} GET
    const res = await request.get(`/repairs/my/${userId.value}`)
    repairList.value = res.data || []
  } catch (err) {
    repairList.value = []
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}
</style>
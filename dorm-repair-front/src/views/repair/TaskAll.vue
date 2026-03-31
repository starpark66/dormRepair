<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的全部维修任务</span>
          <el-select v-model="queryType" placeholder="筛选状态" style="width: 150px;" @change="handleQuery">
            <el-option label="全部任务" value="all" />
            <el-option label="仅处理中" value="processing" />
          </el-select>
        </div>
      </template>

      <el-table :data="taskList" stripe style="width: 100%">
        <el-table-column prop="repairId" label="报修ID" width="100" />
        <el-table-column prop="stuUserId" label="学生账号" width="120" />
        <el-table-column prop="deviceType" label="设备类型" width="180" />
        <el-table-column prop="description" label="故障描述" min-width="200" />
        <el-table-column prop="status" label="当前状态" width="100">
          <template #default="scope">
            <el-tag :type="statusType[scope.row.status]">{{ statusText[scope.row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="学生评分" width="100">
          <template #default="scope">
            {{ scope.row.score ? scope.row.score + '分' : '暂无' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>

      <el-empty v-if="taskList.length === 0" description="暂无维修任务" :image-size="120" style="margin-top: 20px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

// 原有：获取当前登录维修人员工号
const repairUserId = ref(localStorage.getItem('userId'))
const queryType = ref('all')
const taskList = ref([])

// 原有：状态映射
const statusText = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已取消' }
const statusType = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }

// 原有：查询任务
const handleQuery = async () => {
  try {
    let res
    if (queryType.value === 'processing') {
      // 【修改】对接后端接口：/api/repairs/tasks/processing/{repairUserId} GET
      res = await request.get(`/repairs/tasks/processing/${repairUserId.value}`)
    } else {
      // 【修改】对接后端接口：/api/repairs/tasks/{repairUserId} GET
      res = await request.get(`/repairs/tasks/${repairUserId.value}`)
    }
    taskList.value = res.data || []
  } catch (err) {
    taskList.value = []
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
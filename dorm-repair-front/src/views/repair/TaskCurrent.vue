<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的当前维修任务</span>
          <el-button type="primary" @click="handleQuery">刷新</el-button>
        </div>
      </template>

      <el-table :data="taskList" stripe style="width: 100%">
        <el-table-column prop="repairId" label="报修ID" width="100" />
        <el-table-column prop="stuUserId" label="学生账号" width="120" />
        <el-table-column prop="deviceType" label="设备类型" width="180" />
        <el-table-column prop="description" label="故障描述" min-width="200" />
        <el-table-column prop="urgency" label="紧急程度" width="100">
          <template #default="scope">
            <el-tag :type="urgencyType[scope.row.urgency]">{{ urgencyText[scope.row.urgency] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phonenum" label="联系电话" width="130" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>

      <el-empty v-if="taskList.length === 0" description="暂无当前维修任务" :image-size="120" style="margin-top: 20px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

// 原有：获取当前登录维修人员工号
const repairUserId = ref(localStorage.getItem('userId'))
const taskList = ref([])

// 原有：紧急程度映射
const urgencyText = { 1: '普通', 2: '紧急', 3: '非常紧急' }
const urgencyType = { 1: 'info', 2: 'warning', 3: 'danger' }

// 原有：查询处理中的任务
const handleQuery = async () => {
  try {
    // 【修改】对接后端接口：/api/repairs/tasks/processing/{repairUserId} GET
    const res = await request.get(`/repairs/tasks/processing/${repairUserId.value}`)
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
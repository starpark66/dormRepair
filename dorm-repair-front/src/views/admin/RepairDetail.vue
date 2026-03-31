<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <el-button type="info" @click="$router.back()">返回</el-button>
          <span>报修单详情</span>
        </div>
      </template>

      <el-descriptions title="报修单基础信息" :column="2" border v-if="repairDetail">
        <el-descriptions-item label="报修ID" :span="1">{{ repairDetail.repairId }}</el-descriptions-item>
        <el-descriptions-item label="学生账号" :span="1">{{ repairDetail.stuUserId }}</el-descriptions-item>
        <el-descriptions-item label="设备类型" :span="1">{{ repairDetail.deviceType }}</el-descriptions-item>
        <el-descriptions-item label="紧急程度" :span="1">
          <el-tag :type="urgencyType[repairDetail.urgency]">{{ urgencyText[repairDetail.urgency] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ repairDetail.description }}</el-descriptions-item>
        <el-descriptions-item label="联系电话" :span="1">{{ repairDetail.phonenum }}</el-descriptions-item>
        <el-descriptions-item label="当前状态" :span="1">
          <el-tag :type="statusType[repairDetail.status]">{{ statusText[repairDetail.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="维修人员账号" :span="1">{{ repairDetail.repairUserId || '暂无分配' }}</el-descriptions-item>
        <el-descriptions-item label="学生评分" :span="1">{{ repairDetail.score ? repairDetail.score + '分' : '暂无评分' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="1">{{ repairDetail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="最后更新时间" :span="1">{{ repairDetail.updateTime }}</el-descriptions-item>
      </el-descriptions>

      <el-empty v-else description="报修单不存在" :image-size="120" style="margin-top: 20px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '../../utils/request'

const route = useRoute()
const repairId = route.params.id
const repairDetail = ref(null)

// 原有：状态/紧急程度映射
const statusText = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已取消' }
const statusType = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
const urgencyText = { 1: '普通', 2: '紧急', 3: '非常紧急' }
const urgencyType = { 1: 'info', 2: 'warning', 3: 'danger' }

// 原有：查询详情
const handleQuery = async () => {
  try {
    // 【修改】对接后端接口：/api/repairs/detail/{repairId} GET
    const res = await request.get(`/repairs/detail/${repairId}`)
    repairDetail.value = res.data
  } catch (err) {
    repairDetail.value = null
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
  align-items: center;
  gap: 15px;
  font-weight: bold;
  font-size: 16px;
}
</style>
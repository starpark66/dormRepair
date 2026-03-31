<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">维修人员评分</div>
      </template>

      <el-alert
        title="仅已完成且未评分的报修单可以评分"
        type="info"
        :closable="false"
        style="margin-bottom: 20px;"
      />

      <el-table :data="scoreList" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="repairId" label="报修ID" width="100" />
        <el-table-column prop="deviceType" label="设备类型" width="180" />
        <el-table-column prop="description" label="故障描述" min-width="200" />
        <el-table-column prop="repairUserId" label="维修人员" width="120" />
        <el-table-column prop="createTime" label="完成时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-rate v-model="scope.row.tempScore" :max="5" />
            <el-button type="primary" text @click="handleScore(scope.row)" :disabled="!scope.row.tempScore">
              提交评分
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="scoreList.length === 0" description="暂无可评分的报修单" :image-size="120" style="margin-top: 20px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const userId = ref(localStorage.getItem('userId'))
const scoreList = ref([])

const handleQuery = async () => {
  try {
    const res = await request.get(`/repairs/my/${userId.value}`)
    scoreList.value = (res.data || [])
      .filter(item => item.status === 2 && (!item.score || item.score === 0))
      .map(item => ({
        ...item,
        tempScore: 0,
        repairId: item.repairId || item.repair_id,
        stuUserId: item.stuUserId || item.stu_user_id,
        repairUserId: item.repairUserId || item.repair_user_id
      }))
  } catch (err) {
    scoreList.value = []
  }
}

// 修复：提交评分（带了 stuUserId！）
const handleScore = async (row) => {
  try {
    await request.post(`/repairs/score/${row.repairId}`, null, {
      params: {
        score: row.tempScore,
        stuUserId: userId.value  //  这里终于传了！
      }
    })
    ElMessage.success('评分成功！')
    handleQuery()
  } catch (err) {
    ElMessage.error('评分失败！')
    console.error(err)
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
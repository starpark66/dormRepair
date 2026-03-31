<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>维修人员评分情况（降序）</span>
          <el-button type="primary" @click="handleQuery">刷新</el-button>
        </div>
      </template>

      <el-table :data="statList" stripe style="width: 100%">
        <el-table-column prop="rank" label="排名" width="80" />
        <el-table-column prop="repairUserId" label="维修人员工号" width="180" />
        <el-table-column prop="avgScore" label="平均评分" width="120">
          <template #default="scope">
            {{ scope.row.avgScore.toFixed(2) }} 分
          </template>
        </el-table-column>
        <el-table-column prop="count" label="评分次数" width="120" />
      </el-table>

      <el-empty v-if="statList.length === 0" description="暂无评分数据" :image-size="120" style="margin-top: 20px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const statList = ref([])

// 原有：查询评分统计
const handleQuery = async () => {
  try {
    // 【修改】对接后端接口：/api/repairs/scores GET
    const res = await request.get('/repairs/scores')
    // 原有：补充排名字段
    statList.value = (res.data || []).map((item, index) => ({
      ...item,
      rank: index + 1,
      repairUserId: item.repair_user_id,
      avgScore: item.avg_score,
      count: item.count
    }))
  } catch (err) {
    statList.value = []
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
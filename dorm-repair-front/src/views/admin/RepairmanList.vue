<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>所有维修人员</span>
          <el-button type="primary" @click="handleQuery">刷新</el-button>
        </div>
      </template>

      <el-table :data="repairmanList" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="userId" label="维修人员工号" width="100%" />
      </el-table>

      <el-empty v-if="repairmanList.length === 0" description="暂无维修人员数据" :image-size="120" style="margin-top: 20px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const repairmanList = ref([])

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
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}
</style>
<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>所有报修单</span>
          <div class="header-tools">
            <el-select v-model="queryStatus" placeholder="按状态筛选" style="width: 150px; margin-right: 10px;" @change="handleQuery">
              <el-option label="全部" value="" />
              <el-option label="待处理" :value="0" />
              <el-option label="处理中" :value="1" />
              <el-option label="已完成" :value="2" />
              <el-option label="已取消" :value="3" />
            </el-select>
            <el-select v-model="queryUrgency" placeholder="按紧急程度筛选" style="width: 150px; margin-right: 10px;" @change="handleQuery">
              <el-option label="全部" value="" />
              <el-option label="普通" :value="1" />
              <el-option label="紧急" :value="2" />
              <el-option label="非常紧急" :value="3" />
            </el-select>
            <el-button type="primary" @click="handleQuery">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="repairList" stripe style="width: 100%">   
        <el-table-column prop="repairId" label="报修ID" />
        <el-table-column prop="stuUserId" label="学生账号" />
        <el-table-column prop="deviceType" label="设备类型" width="180" />
        <el-table-column prop="description" label="故障描述" min-width="150" />
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
        <el-table-column prop="createTime" label="创建时间" width="180" />
        
        <!-- 操作列：已删除 删除按钮！只保留 详情 + 分配 -->
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="scope">
            <el-button type="primary" text @click="$router.push(`/admin/repair-detail/${scope.row.repairId}`)">
              详情
            </el-button>
            <el-button type="warning" text @click="$router.push('/admin/repair-assign')">
              分配
            </el-button>
          </template>
        </el-table-column>

      </el-table>

      <el-empty v-if="repairList.length === 0" description="暂无报修单数据" :image-size="120" style="margin-top: 20px;" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const repairList = ref([])
const queryStatus = ref('')
const queryUrgency = ref('')

// 状态/紧急程度映射
const statusText = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已取消' }
const statusType = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
const urgencyText = { 1: '普通', 2: '紧急', 3: '非常紧急' }
const urgencyType = { 1: 'info', 2: 'warning', 3: 'danger' }

// 查询所有报修单
const handleQuery = async () => {
  try {
    let res
    if (queryStatus.value !== '') {
      res = await request.get(`/repairs/status/${queryStatus.value}`)
    } else if (queryUrgency.value !== '') {
      res = await request.get(`/repairs/urgency/${queryUrgency.value}`)
    } else {
      res = await request.get('/repairs/all')
    }

    repairList.value = (res.data || []).map(item => ({
      repairId: item.repairId || item.repair_id,
      stuUserId: item.stuUserId || item.stu_user_id,
      deviceType: item.deviceType || item.device_type,
      description: item.description,
      urgency: item.urgency,
      status: item.status,
      repairUserId: item.repairUserId || item.repair_user_id,
      createTime: item.createTime || item.create_time
    }))

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
.header-tools {
  display: flex;
  align-items: center;
}
</style>
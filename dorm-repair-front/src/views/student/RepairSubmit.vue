<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">创建报修单</div>
      </template>

      <el-form :model="repairForm" :rules="repairRules" ref="repairFormRef" label-width="120px" size="large" style="max-width: 600px; margin: 0 auto;">
        <el-form-item label="学生学号" prop="stuUserId">
          <el-input v-model="repairForm.stuUserId" disabled placeholder="自动填充"></el-input>
        </el-form-item>
        <el-form-item label="故障设备类型" prop="deviceType">
          <el-select v-model="repairForm.deviceType" placeholder="请选择故障设备" style="width: 100%;">
            <el-option
              v-for="(item, index) in deviceTypeList"
              :key="index"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="故障详细描述" prop="description">
          <el-input
            v-model="repairForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述故障情况"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="联系电话" prop="phonenum">
          <el-input v-model="repairForm.phonenum" placeholder="请输入你的联系电话" clearable />
        </el-form-item>
        <el-form-item label="紧急程度">
          <el-tag :type="urgencyType[repairForm.urgency]">{{ urgencyText[repairForm.urgency] }}</el-tag>
          <div class="urgency-tip">* 系统会根据设备类型自动设置紧急程度</div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            提交报修单
          </el-button>
          <el-button @click="resetForm">
            重置表单
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

// 原有：获取当前登录学号
const userId = ref(localStorage.getItem('userId'))
const loading = ref(false)
const repairFormRef = ref(null)

// 原有：设备类型列表（和控制台完全一致）
const deviceTypeList = [
  "电器类 开关跳闸", "电器类 插座", "电器类 调速器", "电器类 按钮开关", "电器类 光管",
  "电器类 风扇", "电器类 阳台灯", "电器类 厕所灯", "电器类 排气扇", "电器类 电线",
  "电器类 学生宿舍空调",
  "水件类 水龙头", "水件类 水管", "水件类 沐浴器",
  "家具类 家具把手", "家具类 床", "家具类 书架", "家具类 衣柜", "家具类 椅子",
  "家具类 键盘托", "家具类 书桌", "家具类 床板", "家具类 蚊帐架",
  "门窗类 门", "门窗类 锁", "门窗类 窗", "门窗类 玻璃门", "门窗类 窗把手",
  "门窗类 门把手", "门窗类 插销",
  "土建类 天花板漏水", "土建类 墙体渗水", "土建类 瓷砖开裂",
  "排水类 下水道", "排水类 厕所",
  "消防类 消防设施",
  "其他类 其他",
  "宿舍空调 美的空调", "宿舍空调 小天鹅空调", "宿舍空调 TCL空调", "宿舍空调 格力空调",
  "宿舍空调 其它品牌"
]

// 原有：紧急程度映射
const urgencyText = { 1: '普通', 2: '紧急', 3: '非常紧急' }
const urgencyType = { 1: 'info', 2: 'warning', 3: 'danger' }

// 原有：报修单表单
const repairForm = ref({
  stuUserId: userId.value,
  deviceType: '',
  description: '',
  urgency: 1,
  phonenum: '',
  status: 0
})

// 原有：表单校验规则
const repairRules = ref({
  stuUserId: [
    { required: true, message: '学号不能为空', trigger: 'blur' },
    { pattern: /^(3125|3225)\d{6}$/, message: '学号格式错误', trigger: 'blur' }
  ],
  deviceType: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  description: [{ required: true, message: '请描述故障详情', trigger: 'blur' }],
  phonenum: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    // 【修改】对接后端RegEx的手机号规则
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ]
})

// 原有：选择设备后自动设置紧急程度（和控制台逻辑完全一致）
watch(() => repairForm.value.deviceType, (newVal) => {
  if (!newVal) return
  const index = deviceTypeList.findIndex(item => item === newVal)
  const highUrgency = [0,9,12,26,30,31,33,34,35]
  const midUrgency = [1,4,7,10,11,13,23,24,37,38,39,40,41]
  
  if (highUrgency.includes(index)) {
    repairForm.value.urgency = 3
  } else if (midUrgency.includes(index)) {
    repairForm.value.urgency = 2
  } else {
    repairForm.value.urgency = 1
  }
})

// 原有：提交报修单
const handleSubmit = async () => {
  try {
    await repairFormRef.value.validate()
    // 【修改】对接后端接口：/api/repairs/submit POST
    await request.post('/repairs/submit', repairForm.value)
    ElMessage.success('报修单提交成功！请等待管理员分配维修人员')
    resetForm()
  } catch (error) {
    console.error('提交失败：', error)
  }
}

// 原有：重置表单
const resetForm = () => {
  repairFormRef.value.resetFields()
  repairForm.value = {
    stuUserId: userId.value,
    deviceType: '',
    description: '',
    urgency: 1,
    phonenum: '',
    status: 0
  }
}
</script>

<style scoped>
.page-container {
  padding: 10px;
}
.card-header {
  font-weight: bold;
  font-size: 16px;
}
.urgency-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
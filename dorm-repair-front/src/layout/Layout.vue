<template>
  <div class="layout-container">
    <!-- 原有：顶部导航栏 -->
    <el-header class="layout-header">
      <div class="header-title">宿舍报修管理系统</div>
      <div class="header-user">
        <span>当前账号：{{ userId }} | 角色：{{ roleName }}</span>
        <el-button type="danger" text @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>

    <!-- 原有：主体内容 -->
    <div class="layout-main">
      <!-- 原有：左侧菜单栏 -->
      <el-aside width="220px" class="layout-aside">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <!-- 原有：学生菜单 -->
          <template v-if="userRole === 1">
            <el-menu-item index="/student/dorm-bind">
              <el-icon><Location /></el-icon>
              <span>绑定/修改宿舍</span>
            </el-menu-item>
            <el-menu-item index="/student/repair-submit">
              <el-icon><EditPen /></el-icon>
              <span>创建报修单</span>
            </el-menu-item>
            <el-menu-item index="/student/repair-list">
              <el-icon><Document /></el-icon>
              <span>我的报修记录</span>
            </el-menu-item>
            <el-menu-item index="/student/repair-cancel">
              <el-icon><Close /></el-icon>
              <span>取消报修单</span>
            </el-menu-item>
            <el-menu-item index="/student/repair-score">
              <el-icon><Star /></el-icon>
              <span>评分维修人员</span>
            </el-menu-item>
            <el-menu-item index="/student/change-pwd">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
            </el-menu-item>
          </template>

          <!-- 原有：维修人员菜单 -->
          <template v-else-if="userRole === 2">
            <el-menu-item index="/repair/task-current">
              <el-icon><Tools /></el-icon>
              <span>我的当前维修任务</span>
            </el-menu-item>
            <el-menu-item index="/repair/task-update">
              <el-icon><CircleCheck /></el-icon>
              <span>更新报修状态</span>
            </el-menu-item>
            <el-menu-item index="/repair/task-all">
              <el-icon><List /></el-icon>
              <span>我的全部维修任务</span>
            </el-menu-item>
            <el-menu-item index="/repair/change-pwd">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
            </el-menu-item>
          </template>

          <!-- 原有：管理员菜单 -->
          <template v-else-if="userRole === 3">
            <el-menu-item index="/admin/repair-all">
              <el-icon><Document /></el-icon>
              <span>查看所有报修单</span>
            </el-menu-item>
            <el-menu-item index="/admin/repair-delete">
              <el-icon><Delete /></el-icon>
              <span>删除报修单</span>
            </el-menu-item>
            <el-menu-item index="/admin/repair-assign">
              <el-icon><UserFilled /></el-icon>
              <span>分配报修任务</span>
            </el-menu-item>
            <el-menu-item index="/admin/score-stat">
              <el-icon><Star /></el-icon>
              <span>维修人员评分情况</span>
            </el-menu-item>
            <el-menu-item index="/admin/repairman-list">
              <el-icon><User /></el-icon>
              <span>查看所有维修人员</span>
            </el-menu-item>
            <!-- 修复：把 UserDelete 改成 Delete -->
            <el-menu-item index="/admin/repairman-delete">
              <el-icon><Delete /></el-icon>
              <span>删除维修人员</span>
            </el-menu-item>
            <el-menu-item index="/admin/change-pwd">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <!-- 原有：右侧内容区（页面在这里渲染） -->
      <el-main class="layout-content">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Location, EditPen, Document, Close, Star, Lock,
  Tools, CircleCheck, List, Delete, UserFilled, User
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 原有：获取登录用户信息
const userId = ref(localStorage.getItem('userId') || '')
const userRole = ref(Number(localStorage.getItem('userRole')) || 0)

// 原有：激活的菜单
const activeMenu = ref(route.path)

// 原有：角色名称
const roleName = computed(() => {
  const roleMap = { 1: '学生', 2: '维修人员', 3: '管理员' }
  return roleMap[userRole.value] || '未知角色'
})

// 修复：正确的路由监听写法
watch(
  () => route.path,
  (newPath) => {
    activeMenu.value = newPath
  }
)

// 原有：退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 原有：清空本地存储
    localStorage.clear()
    ElMessage.success('退出成功')
    router.push('/login')
  }).catch(() => {})
}

// 原有：页面加载校验登录状态
onMounted(() => {
  if (!userId.value || !userRole.value) {
    router.push('/login')
  }
})
</script>

<style scoped>
.layout-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
.layout-header {
  background-color: #242f42;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.header-title {
  font-size: 20px;
  font-weight: bold;
}
.header-user {
  display: flex;
  align-items: center;
  gap: 15px;
}
.layout-main {
  display: flex;
  height: calc(100vh - 60px);
}
.layout-aside {
  height: 100%;
}
.layout-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
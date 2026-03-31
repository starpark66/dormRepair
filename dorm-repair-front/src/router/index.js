import { createRouter, createWebHashHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 原有：页面组件导入
import Login from '../views/Login.vue'
import Layout from '../layout/Layout.vue'

// ========== 学生页面导入 ==========
import StudentDormBind from '../views/student/DormBind.vue'
import StudentRepairSubmit from '../views/student/RepairSubmit.vue'
import StudentRepairList from '../views/student/RepairList.vue'
import StudentRepairCancel from '../views/student/RepairCancel.vue'
import StudentRepairScore from '../views/student/RepairScore.vue'
import StudentChangePwd from '../views/student/ChangePwd.vue'

// ========== 维修人员页面导入 ==========
import RepairTaskCurrent from '../views/repair/TaskCurrent.vue'
import RepairTaskUpdate from '../views/repair/TaskUpdate.vue'
import RepairTaskAll from '../views/repair/TaskAll.vue'
import RepairChangePwd from '../views/repair/ChangePwd.vue'

// ========== 管理员页面导入 ==========
import AdminRepairAll from '../views/admin/RepairAll.vue'
import AdminRepairDetail from '../views/admin/RepairDetail.vue'
import AdminRepairDelete from '../views/admin/RepairDelete.vue'
import AdminRepairAssign from '../views/admin/RepairAssign.vue'
import AdminScoreStat from '../views/admin/ScoreStat.vue'
import AdminRepairmanList from '../views/admin/RepairmanList.vue'
import AdminRepairmanDelete from '../views/admin/RepairmanDelete.vue'
import AdminChangePwd from '../views/admin/ChangePwd.vue'

// 原有：路由守卫：未登录拦截+角色权限校验
const authGuard = (to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')

  // 原有：未登录，跳登录页
  if (!token) {
    ElMessage.error('请先登录')
    return next('/login')
  }

  // 【修改】对接后端角色权限：校验页面访问权限
  if (to.meta.allowRole && !to.meta.allowRole.includes(Number(userRole))) {
    ElMessage.error('无权限访问该页面')
    return next(from.path || '/login')
  }

  next()
}

// 原有：路由规则
const routes = [
  // 原有：登录页
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录',noAuth:true}
  },
  // 原有：主布局（登录后所有页面都嵌套在这个布局里）
  {
    path: '/',
    component: Layout,
    redirect: '/login',
    children: [
      // ========== 学生角色路由（role=1） ==========
      {
        path: 'student/dorm-bind',
        name: 'StudentDormBind',
        component: StudentDormBind,
        meta: { title: '绑定/修改宿舍', allowRole: [1] },
        beforeEnter: authGuard
      },
      {
        path: 'student/repair-submit',
        name: 'StudentRepairSubmit',
        component: StudentRepairSubmit,
        meta: { title: '创建报修单', allowRole: [1] },
        beforeEnter: authGuard
      },
      {
        path: 'student/repair-list',
        name: 'StudentRepairList',
        component: StudentRepairList,
        meta: { title: '我的报修记录', allowRole: [1] },
        beforeEnter: authGuard
      },
      {
        path: 'student/repair-cancel',
        name: 'StudentRepairCancel',
        component: StudentRepairCancel,
        meta: { title: '取消报修单', allowRole: [1] },
        beforeEnter: authGuard
      },
      {
        path: 'student/repair-score',
        name: 'StudentRepairScore',
        component: StudentRepairScore,
        meta: { title: '评分维修人员', allowRole: [1] },
        beforeEnter: authGuard
      },
      {
        path: 'student/change-pwd',
        name: 'StudentChangePwd',
        component: StudentChangePwd,
        meta: { title: '修改密码', allowRole: [1] },
        beforeEnter: authGuard
      },

      // ========== 维修人员角色路由（role=2） ==========
      {
        path: 'repair/task-current',
        name: 'RepairTaskCurrent',
        component: RepairTaskCurrent,
        meta: { title: '我的当前维修任务', allowRole: [2] },
        beforeEnter: authGuard
      },
      {
        path: 'repair/task-update',
        name: 'RepairTaskUpdate',
        component: RepairTaskUpdate,
        meta: { title: '更新报修状态', allowRole: [2] },
        beforeEnter: authGuard
      },
      {
        path: 'repair/task-all',
        name: 'RepairTaskAll',
        component: RepairTaskAll,
        meta: { title: '我的全部维修任务', allowRole: [2] },
        beforeEnter: authGuard
      },
      {
        path: 'repair/change-pwd',
        name: 'RepairChangePwd',
        component: RepairChangePwd,
        meta: { title: '修改密码', allowRole: [2] },
        beforeEnter: authGuard
      },

      // ========== 管理员角色路由（role=3） ==========
      {
        path: 'admin/repair-all',
        name: 'AdminRepairAll',
        component: AdminRepairAll,
        meta: { title: '所有报修单', allowRole: [3] },
        beforeEnter: authGuard
      },
      {
        path: 'admin/repair-detail/:id',
        name: 'AdminRepairDetail',
        component: AdminRepairDetail,
        meta: { title: '报修单详情', allowRole: [3] },
        beforeEnter: authGuard
      },
      {
        path: 'admin/repair-delete',
        name: 'AdminRepairDelete',
        component: AdminRepairDelete,
        meta: { title: '删除报修单', allowRole: [3] },
        beforeEnter: authGuard
      },
      {
        path: 'admin/repair-assign',
        name: 'AdminRepairAssign',
        component: AdminRepairAssign,
        meta: { title: '分配报修任务', allowRole: [3] },
        beforeEnter: authGuard
      },
      {
        path: 'admin/score-stat',
        name: 'AdminScoreStat',
        component: AdminScoreStat,
        meta: { title: '维修人员评分情况', allowRole: [3] },
        beforeEnter: authGuard
      },
      {
        path: 'admin/repairman-list',
        name: 'AdminRepairmanList',
        component: AdminRepairmanList,
        meta: { title: '所有维修人员', allowRole: [3] },
        beforeEnter: authGuard
      },
      {
        path: 'admin/repairman-delete',
        name: 'AdminRepairmanDelete',
        component: AdminRepairmanDelete,
        meta: { title: '删除维修人员', allowRole: [3] },
        beforeEnter: authGuard
      },
      {
        path: 'admin/change-pwd',
        name: 'AdminChangePwd',
        component: AdminChangePwd,
        meta: { title: '修改密码', allowRole: [3] },
        beforeEnter: authGuard
      }
    ]
  }
]

// 原有：创建路由实例
const router = createRouter({
  history: createWebHashHistory(), // 原有：哈希模式，无需后端配置
  routes
})

// 原有：全局路由守卫
router.beforeEach((to, from, next) => {
  // 1. 登录页直接放行
  if (to.path === '/login') {
    document.title = to.meta.title ? `${to.meta.title} - 宿舍报修系统` : '宿舍报修系统'
    next()
    return
  }

  // 2. 其他页面：判断是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录')
    return next('/login')
  }

  // 3. 设置标题
  document.title = to.meta.title ? `${to.meta.title} - 宿舍报修系统` : '宿舍报修系统'
  next()
})

export default router
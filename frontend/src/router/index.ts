import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/DashboardHome.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'projects',
        name: 'Projects',
        component: () => import('@/views/Projects.vue'),
        meta: { title: '项目管理' }
      },
      {
        path: 'tests',
        name: 'Tests',
        component: () => import('@/views/TestFlows.vue'),
        meta: { title: '测试管理' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/Settings.vue'),
        meta: { title: '系统设置' }
      },
      {
        path: 'executions',
        name: 'Executions',
        component: () => import('@/views/Executions.vue'),
        meta: { title: '执行历史' }
      },
      {
        path: 'scheduled-tasks',
        name: 'ScheduledTasks',
        component: () => import('@/views/ScheduledTasks.vue'),
        meta: { title: '定时任务' }
      },
      {
        path: 'reports/:reportId',
        name: 'ReportDetail',
        component: () => import('@/views/ReportDetail.vue'),
        meta: { title: '报告详情' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn

  console.log('[Router Guard] 从:', from.path, '到:', to.path)
  console.log('[Router Guard] isLoggedIn:', isLoggedIn)
  console.log('[Router Guard] token:', userStore.token)

  // 设置页面标题
  document.title = `${to.meta.title || '首页'} - TestFlowAI`

  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!isLoggedIn) {
      console.log('[Router Guard] 未登录，重定向到登录页')
      next({ name: 'Login', query: { redirect: to.fullPath } })
    } else {
      console.log('[Router Guard] 已登录，允许访问')
      next()
    }
  } else {
    // 已登录用户访问登录页面，重定向到首页
    if (to.name === 'Login' && isLoggedIn) {
      console.log('[Router Guard] 已登录访问登录页，重定向到首页')
      next({ name: 'Dashboard' })
    } else {
      console.log('[Router Guard] 允许访问登录页')
      next()
    }
  }
})

export default router

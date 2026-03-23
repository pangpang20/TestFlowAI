<template>
  <div class="dashboard-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h2>TestFlowAI</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">
            <el-icon><DataLine /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="projects">
            <el-icon><Folder /></el-icon>
            <span>项目管理</span>
          </el-menu-item>
          <el-menu-item index="tests">
            <el-icon><Connection /></el-icon>
            <span>测试管理</span>
          </el-menu-item>
          <el-menu-item index="settings">
            <el-icon><Setting /></el-icon>
            <span>系统设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <h3>{{ currentTitle }}</h3>
          </div>
          <div class="header-right">
            <span class="username">{{ userStore.username }}</span>
            <el-dropdown @command="handleCommand">
              <el-avatar :size="36" class="avatar">
                {{ userStore.username.charAt(0).toUpperCase() }}
              </el-avatar>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="password">修改密码</el-dropdown-item>
                  <el-dropdown-item command="avatar">修改头像</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="main-content">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-item">
                  <div class="stat-icon projects">
                    <el-icon><Folder /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">12</div>
                    <div class="stat-label">项目总数</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-item">
                  <div class="stat-icon tests">
                    <el-icon><Connection /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">56</div>
                    <div class="stat-label">测试用例</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-item">
                  <div class="stat-icon passed">
                    <el-icon><CircleCheck /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">89%</div>
                    <div class="stat-label">通过率</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="stat-card">
                <div class="stat-item">
                  <div class="stat-icon tasks">
                    <el-icon><List /></el-icon>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">8</div>
                    <div class="stat-label">待处理任务</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" class="mt-5">
            <el-col :span="12">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>最近项目</span>
                  </div>
                </template>
                <el-table :data="recentProjects" style="width: 100%">
                  <el-table-column prop="name" label="项目名称" />
                  <el-table-column prop="status" label="状态">
                    <template #default="{ row }">
                      <el-tag :type="row.status === 'running' ? 'warning' : 'success'" size="small">
                        {{ row.status === 'running' ? '进行中' : '已完成' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="progress" label="进度" />
                </el-table>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>系统通知</span>
                  </div>
                </template>
                <el-timeline>
                  <el-timeline-item timestamp="2024-01-15" placement="top">
                    <el-card>
                      <h4>测试报告已生成</h4>
                      <p>项目 A 的测试报告已自动生成</p>
                    </el-card>
                  </el-timeline-item>
                  <el-timeline-item timestamp="2024-01-14" placement="top">
                    <el-card>
                      <h4>新版本发布</h4>
                      <p>TestFlowAI v2.0.0 已发布</p>
                    </el-card>
                  </el-timeline-item>
                  <el-timeline-item timestamp="2024-01-13" placement="top">
                    <el-card>
                      <h4>系统维护通知</h4>
                      <p>本周末将进行系统维护</p>
                    </el-card>
                  </el-timeline-item>
                </el-timeline>
              </el-card>
            </el-col>
          </el-row>
        </el-main>
      </el-container>
    </el-container>

    <!-- 修改头像对话框 -->
    <el-dialog
      v-model="avatarDialogVisible"
      title="修改头像"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="avatar-upload">
        <div class="avatar-preview" v-if="avatarPreview">
          <img :src="avatarPreview" alt="头像预览" />
        </div>
        <div class="avatar-preview" v-else>
          <el-icon><Plus /></el-icon>
        </div>
        <input
          ref="avatarInput"
          type="file"
          accept="image/*"
          @change="handleAvatarChange"
          style="display: none"
        />
      </div>
      <template #footer>
        <el-button @click="avatarDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="avatarInput?.click()">选择图片</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form :model="passwordForm" label-width="80px" label-position="left">
        <el-form-item label="旧密码">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入旧密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, ElDialog, ElButton } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { uploadAvatarApi, changePasswordApi } from '@/api/auth'
import {
  DataLine,
  Folder,
  Connection,
  Setting,
  CircleCheck,
  List,
  Plus
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 头像上传对话框
const avatarDialogVisible = ref(false)
const uploading = ref(false)
const avatarPreview = ref<string>('')

// 头像文件输入
const avatarInput = ref<HTMLInputElement | null>(null)

// 处理头像上传
const handleAvatarChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return
  }

  // 验证文件大小（限制 5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 5MB')
    return
  }

  // 预览头像
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target?.result as string
  }
  reader.readAsDataURL(file)

  // 上传头像
  uploading.value = true
  try {
    const res = await uploadAvatarApi(file)
    const avatarUrl = res.data?.avatarUrl || res.avatarUrl

    // 更新用户信息中的头像
    if (userStore.userInfo) {
      userStore.userInfo.avatar = avatarUrl
    }

    ElMessage.success('头像上传成功')
    avatarDialogVisible.value = false
  } catch (error) {
    ElMessage.error('上传失败，请重试')
  } finally {
    uploading.value = false
    // 清空 input，允许重复上传同一文件
    if (avatarInput.value) {
      avatarInput.value.value = ''
    }
  }
}

// 打开头像选择对话框
const showAvatarDialog = () => {
  avatarDialogVisible.value = true
  avatarPreview.value = ''
}

// 当前激活的菜单
const activeMenu = computed(() => {
  const pathToIndex: Record<string, string> = {
    '/': 'dashboard',
    '/projects': 'projects',
    '/tests': 'tests',
    '/settings': 'settings'
  }
  return pathToIndex[route.path] || 'dashboard'
})

// 当前页面标题
const currentTitle = computed(() => {
  const titles: Record<string, string> = {
    '/': '仪表盘',
    '/projects': '项目管理',
    '/tests': '测试管理',
    '/settings': '系统设置'
  }
  return titles[route.path] || '仪表盘'
})

// 菜单路径映射
const menuRoutes: Record<string, string> = {
  dashboard: '/',
  projects: '/projects',
  tests: '/tests',
  settings: '/settings'
}

// 处理菜单点击
const handleMenuSelect = (index: string) => {
  const routePath = menuRoutes[index]
  if (routePath) {
    router.push(routePath)
  }
}

const recentProjects = [
  { name: '项目 A', status: 'running', progress: '75%' },
  { name: '项目 B', status: 'completed', progress: '100%' },
  { name: '项目 C', status: 'running', progress: '30%' },
  { name: '项目 D', status: 'completed', progress: '100%' }
]

// 用户头像下拉菜单处理
const handleCommand = async (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  } else if (command === 'password') {
    showPasswordDialog()
  } else if (command === 'avatar') {
    showAvatarDialog()
  }
}

// 修改密码对话框
const passwordDialogVisible = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const showPasswordDialog = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  passwordDialogVisible.value = true
}

const handleChangePassword = async () => {
  // 验证新密码格式
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9]).{8,20}$/
  if (!passwordRegex.test(passwordForm.value.newPassword)) {
    ElMessage.error('密码格式不符：需包含大小写字母、数字和特殊字符，长度 8-20 位')
    return
  }

  // 验证两次输入的密码是否一致
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  try {
    await changePasswordApi(passwordForm.value.oldPassword, passwordForm.value.newPassword)
    ElMessage.success('密码修改成功，请重新登录')
    passwordDialogVisible.value = false
    userStore.logout()
    router.push('/login')
  } catch (error: any) {
    ElMessage.error(error.message || '密码修改失败')
  }
}
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
  overflow: hidden;
}

.dashboard-container .el-container {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4b;
}

.logo h2 {
  color: #fff;
  font-size: 20px;
  margin: 0;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-left h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  color: #666;
  font-size: 14px;
}

.avatar {
  cursor: pointer;
  background-color: #409EFF;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.stat-icon.projects {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-icon.tests {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-icon.passed {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.stat-icon.tasks {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-5 {
  margin-top: 20px;
}

.avatar-upload {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.avatar-preview {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 2px dashed #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-preview .el-icon {
  font-size: 50px;
  color: #8c939d;
}
</style>

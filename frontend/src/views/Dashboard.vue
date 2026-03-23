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
                <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar" alt="avatar" style="width: 100%; height: 100%; object-fit: cover;" />
                <span v-else>{{ userStore.username.charAt(0).toUpperCase() }}</span>
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

        <!-- 内容区域 - 路由视图 -->
        <el-main class="main-content">
          <router-view />
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
        <div class="avatar-preview" v-if="avatarPreview || selectedFile">
          <img :src="avatarPreview || selectedFile" alt="头像预览" />
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
        <el-button
          type="success"
          @click="confirmAvatarUpload"
          :disabled="!selectedFile || uploading"
        >
          确认上传
        </el-button>
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
import { computed, ref, provide } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
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
const selectedFile = ref<File | null>(null)

// 头像文件输入
const avatarInput = ref<HTMLInputElement | null>(null)

// 处理头像选择（只预览，不上传）
const handleAvatarChange = (event: Event) => {
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

  // 保存选中的文件
  selectedFile.value = file

  // 预览头像
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target?.result as string
  }
  reader.readAsDataURL(file)
}

// 确认上传头像
const confirmAvatarUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择图片')
    return
  }

  uploading.value = true
  try {
    const res = await uploadAvatarApi(selectedFile.value)
    const avatarUrl = res.data?.avatarUrl || res.avatarUrl

    // 更新用户信息中的头像
    if (userStore.userInfo) {
      userStore.userInfo.avatar = avatarUrl
    }

    ElMessage.success('头像上传成功')
    avatarDialogVisible.value = false
    selectedFile.value = null
    avatarPreview.value = ''
  } catch (error: any) {
    ElMessage.error(error.message || '上传失败，请重试')
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
  selectedFile.value = null
}

// 当前激活的菜单
const activeMenu = computed(() => {
  const routeName = route.name as string
  const nameToIndex: Record<string, string> = {
    'Dashboard': 'dashboard',
    'Projects': 'projects',
    'Tests': 'tests',
    'Settings': 'settings'
  }
  return nameToIndex[routeName] || 'dashboard'
})

// 当前页面标题
const currentTitle = computed(() => {
  return (route.meta.title as string) || '仪表盘'
})

// 处理菜单点击
const handleMenuSelect = (index: string) => {
  const nameToPath: Record<string, string> = {
    'dashboard': '/',
    'projects': '/projects',
    'tests': '/tests',
    'settings': '/settings'
  }
  const path = nameToPath[index]
  if (path) {
    router.push(path)
  }
}

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
  overflow-y: auto;
}
</style>

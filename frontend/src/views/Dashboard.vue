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
            <h3>仪表盘</h3>
          </div>
          <div class="header-right">
            <span class="username">{{ userStore.username }}</span>
            <el-dropdown @command="handleCommand">
              <el-avatar :size="36" class="avatar">
                {{ userStore.username.charAt(0).toUpperCase() }}
              </el-avatar>
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
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import {
  DataLine,
  Folder,
  Connection,
  Setting,
  CircleCheck,
  List
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => 'dashboard')

const recentProjects = [
  { name: '项目 A', status: 'running', progress: '75%' },
  { name: '项目 B', status: 'completed', progress: '100%' },
  { name: '项目 C', status: 'running', progress: '30%' },
  { name: '项目 D', status: 'completed', progress: '100%' }
]

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }
}
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
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
</style>

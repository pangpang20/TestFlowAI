<template>
  <div class="dashboard-home">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon projects">
              <el-icon><Folder /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.projectCount }}</div>
              <div class="stat-label">项目总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon tests">
              <el-icon><Connection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.testFlowCount }}</div>
              <div class="stat-label">测试用例</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon passed">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.passRate }}%</div>
              <div class="stat-label">通过率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-item">
            <div class="stat-icon tasks">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.activeTasks }}</div>
              <div class="stat-label">定时任务</div>
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
              <span>最近执行</span>
              <el-button link type="primary" @click="$router.push('/executions')">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentExecutions" style="width: 100%" v-loading="loadingExecutions">
            <el-table-column prop="executionId" label="执行 ID" width="200" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTagType(row.status)" size="small">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="passedSteps" label="进度" width="120">
              <template #default="{ row }">
                <span>{{ row.passedSteps }}/{{ row.totalSteps }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间">
              <template #default="{ row }">
                {{ formatDateTime(row.startTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>执行统计</span>
            </div>
          </template>
          <div class="execution-stats">
            <div
              v-for="item in executionStatsData"
              :key="item.status"
              class="stat-bar-item"
            >
              <div class="stat-bar-label">{{ item.label }}</div>
              <div class="stat-bar-container">
                <div
                  class="stat-bar"
                  :class="item.status"
                  :style="{ width: item.percentage + '%' }"
                ></div>
              </div>
              <div class="stat-bar-value">{{ item.count }}</div>
            </div>
            <div class="stats-summary">
              <div class="summary-item">
                <span class="summary-label">总执行数:</span>
                <span class="summary-value">{{ totalExecutions }}</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">平均通过率:</span>
                <span class="summary-value">{{ stats.passRate }}%</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-5">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速入口</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/testflows')">
              <el-icon><Plus /></el-icon>
              新建测试
            </el-button>
            <el-button type="success" @click="$router.push('/executions')">
              <el-icon><VideoPlay /></el-icon>
              执行测试
            </el-button>
            <el-button type="warning" @click="$router.push('/scheduled-tasks')">
              <el-icon><Clock /></el-icon>
              定时任务
            </el-button>
            <el-button type="info" @click="$router.push('/reports')">
              <el-icon><Document /></el-icon>
              查看报告
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Folder,
  Connection,
  CircleCheck,
  List,
  Plus,
  VideoPlay,
  Clock,
  Document
} from '@element-plus/icons-vue'
import { getOverviewStatsApi, getExecutionStatusStatsApi } from '@/api/stats'
import type { OverviewStats, Execution } from '@/types/stats'

const loading = ref(false)
const loadingExecutions = ref(false)

const stats = reactive<OverviewStats>({
  projectCount: 0,
  testFlowCount: 0,
  passRate: 0,
  activeTasks: 0,
  executionStatus: {},
  recentExecutions: []
})

const executionStatsData = ref<Array<{
  status: string
  label: string
  count: number
  percentage: number
}>>([])

const totalExecutions = computed(() => {
  return Object.values(stats.executionStatus).reduce((sum, count) => sum + count, 0)
})

const recentExecutions = ref<Execution[]>([])

// 获取统计数据
const loadStats = async () => {
  loading.value = true
  try {
    const res = await getOverviewStatsApi()
    if (res.data) {
      stats.projectCount = res.data.projectCount || 0
      stats.testFlowCount = res.data.testFlowCount || 0
      stats.passRate = res.data.passRate || 0
      stats.activeTasks = res.data.activeTasks || 0
      stats.executionStatus = res.data.executionStatus || {}
      stats.recentExecutions = res.data.recentExecutions || []
      recentExecutions.value = res.data.recentExecutions || []

      // 计算执行统计图表数据
      calculateExecutionStats()
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载统计数据失败')
  } finally {
    loading.value = false
  }
}

// 计算执行统计图表数据
const calculateExecutionStats = () => {
  const statusLabels: Record<string, string> = {
    pending: '等待中',
    running: '运行中',
    passed: '已通过',
    failed: '已失败',
    stopped: '已停止'
  }

  const total = totalExecutions.value || 1

  executionStatsData.value = Object.entries(stats.executionStatus).map(([status, count]) => ({
    status,
    label: statusLabels[status] || status,
    count: count as number,
    percentage: Math.round((count as number / total) * 100)
  }))
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    pending: 'info',
    running: 'warning',
    passed: 'success',
    failed: 'danger',
    stopped: 'info'
  }
  return types[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    pending: '等待中',
    running: '运行中',
    passed: '已通过',
    failed: '已失败',
    stopped: '已停止'
  }
  return labels[status] || status
}

// 格式化日期时间
const formatDateTime = (dateStr: string | null) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard-home {
  height: 100%;
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
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
  font-size: 28px;
  font-weight: 700;
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

/* 执行统计样式 */
.execution-stats {
  padding: 10px;
}

.stat-bar-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  gap: 12px;
}

.stat-bar-label {
  width: 70px;
  font-size: 13px;
  color: #606266;
}

.stat-bar-container {
  flex: 1;
  height: 20px;
  background-color: #f0f2f5;
  border-radius: 10px;
  overflow: hidden;
}

.stat-bar {
  height: 100%;
  border-radius: 10px;
  transition: width 0.5s ease;
}

.stat-bar.passed {
  background: linear-gradient(90deg, #67c23a, #95d466);
}

.stat-bar.failed {
  background: linear-gradient(90deg, #f56c6c, #f89898);
}

.stat-bar.pending {
  background: linear-gradient(90deg, #909399, #c8c9cc);
}

.stat-bar.running {
  background: linear-gradient(90deg, #e6a23c, #ebb563);
}

.stat-bar.stopped {
  background: linear-gradient(90deg, #606266, #909399);
}

.stat-bar-value {
  width: 40px;
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.stats-summary {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-around;
}

.summary-item {
  text-align: center;
}

.summary-label {
  font-size: 13px;
  color: #909399;
  margin-right: 8px;
}

.summary-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

/* 快速入口样式 */
.quick-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.quick-actions .el-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 24px;
  font-size: 14px;
}
</style>

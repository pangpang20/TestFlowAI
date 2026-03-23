<template>
  <div class="executions-container">
    <el-card class="executions-card">
      <template #header>
        <div class="card-header">
          <span>执行历史</span>
          <el-button type="primary" @click="showRunDialog">
            <el-icon><VideoPlay /></el-icon>
            执行测试
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="filter-bar">
        <el-select
          v-model="filterStatus"
          placeholder="执行状态"
          clearable
          style="width: 150px"
          @change="loadExecutions"
        >
          <el-option label="等待中" value="pending" />
          <el-option label="运行中" value="running" />
          <el-option label="已通过" value="passed" />
          <el-option label="已失败" value="failed" />
          <el-option label="已停止" value="stopped" />
        </el-select>

        <el-select
          v-model="filterMode"
          placeholder="执行模式"
          clearable
          style="width: 120px"
          @change="loadExecutions"
        >
          <el-option label="手动" value="manual" />
          <el-option label="自动" value="auto" />
          <el-option label="定时" value="scheduled" />
        </el-select>

        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 240px"
          @change="loadExecutions"
        />
      </div>

      <!-- 执行列表 -->
      <el-table :data="executions" style="width: 100%" v-loading="loading">
        <el-table-column prop="executionId" label="执行 ID" width="280" />
        <el-table-column prop="testId" label="测试流 ID" width="280" />
        <el-table-column prop="mode" label="模式" width="100">
          <template #default="{ row }">
            <el-tag :type="getModeTagType(row.mode)" size="small">
              {{ getModeLabel(row.mode) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalSteps" label="步骤" width="100">
          <template #default="{ row }">
            <span style="color: #667eea">
              {{ row.passedSteps }}/{{ row.totalSteps }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'running'"
              link
              type="warning"
              @click="stopExecution(row)"
            >
              停止
            </el-button>
            <el-button link type="primary" @click="viewReport(row)">
              报告
            </el-button>
            <el-button link type="primary" @click="viewStepResults(row)">
              步骤
            </el-button>
            <el-button link type="danger" @click="deleteExecution(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="loadExecutions"
        @current-change="loadExecutions"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 运行测试对话框 -->
    <el-dialog
      v-model="runDialogVisible"
      title="执行测试"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="runFormRef" :model="runForm" label-width="100px">
        <el-form-item label="测试流 ID" required>
          <el-input v-model="runForm.testId" placeholder="请输入测试流 ID" />
        </el-form-item>
        <el-form-item label="执行模式" required>
          <el-select v-model="runForm.mode" style="width: 100%">
            <el-option label="手动执行" value="manual" />
            <el-option label="自动执行" value="auto" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="runDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRun" :loading="running">
          开始执行
        </el-button>
      </template>
    </el-dialog>

    <!-- 步骤结果对话框 -->
    <el-dialog
      v-model="stepDialogVisible"
      title="步骤执行结果"
      width="900px"
    >
      <el-table :data="currentStepResults" style="width: 100%">
        <el-table-column prop="stepId" label="步骤 ID" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="错误信息" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.errorMessage" style="color: #f56c6c">
              {{ row.errorMessage }}
            </span>
            <span v-else style="color: #67c23a">成功</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 实时日志对话框 -->
    <el-dialog
      v-model="logDialogVisible"
      :title="`执行日志 - ${currentExecutionId}`"
      width="1000px"
      :close-on-click-modal="false"
      @close="disconnectWebSocket"
    >
      <div class="log-container">
        <div class="log-header">
          <el-tag :type="getStatusTagType(executionStatus)" size="small">
            {{ getStatusLabel(executionStatus) }}
          </el-tag>
          <span class="log-progress">
            步骤：{{ passedSteps }}/{{ totalSteps }}
          </span>
          <el-button
            v-if="executionStatus === 'running'"
            type="warning"
            size="small"
            @click="stopCurrentExecution"
          >
            停止执行
          </el-button>
        </div>
        <div ref="logContentRef" class="log-content">
          <div
            v-for="(log, index) in executionLogs"
            :key="index"
            :class="['log-item', log.type]"
          >
            <span class="log-time">{{ formatLogTime(log.timestamp) }}</span>
            <span class="log-icon">{{ getLogIcon(log.type) }}</span>
            <span class="log-message">{{ log.message }}</span>
          </div>
          <div v-if="executionLogs.length === 0" class="log-empty">
            等待执行日志...
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="logDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { VideoPlay } from '@element-plus/icons-vue'
import {
  getExecutionsApi,
  runTestFlowApi,
  stopExecutionApi,
  deleteExecutionApi,
  getStepResultsApi,
  generateReportApi,
  getReportByExecutionIdApi
} from '@/api/execution'
import type { Execution, StepResult } from '@/types/execution'
import { useRouter } from 'vue-router'
import { wsClient } from '@/utils/websocket'

const router = useRouter()

const loading = ref(false)
const running = ref(false)
const executions = ref<Execution[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filterStatus = ref('')
const filterMode = ref('')
const dateRange = ref<[Date, Date] | null>(null)

const runDialogVisible = ref(false)
const runFormRef = ref<FormInstance>()
const runForm = reactive({
  testId: '',
  mode: 'manual'
})

const stepDialogVisible = ref(false)
const currentStepResults = ref<StepResult[]>([])
const currentExecutionId = ref('')

// 实时日志相关
const logDialogVisible = ref(false)
const logContentRef = ref<HTMLElement | null>(null)
const executionLogs = ref<Array<{
  type: string
  message: string
  timestamp: number
}>>([])
const executionStatus = ref('running')
const totalSteps = ref(0)
const passedSteps = ref(0)

// 获取执行列表
const loadExecutions = async () => {
  loading.value = true
  try {
    const res = await getExecutionsApi(currentPage.value, pageSize.value)
    executions.value = res.data?.list || res.list || []
    total.value = res.data?.total || res.total || 0
  } catch (error) {
    ElMessage.error('加载执行列表失败')
  } finally {
    loading.value = false
  }
}

// 显示运行对话框
const showRunDialog = () => {
  runForm.testId = ''
  runForm.mode = 'manual'
  runDialogVisible.value = true
}

// 执行测试
const handleRun = async () => {
  if (!runForm.testId) {
    ElMessage.warning('请输入测试流 ID')
    return
  }

  running.value = true
  try {
    // 创建执行记录
    const res = await runTestFlowApi(runForm.testId)
    ElMessage.success('执行已完成')
    runDialogVisible.value = false
    loadExecutions()

    // 如果执行成功，打开实时日志对话框
    if (res.data?.executionId) {
      openExecutionLog(res.data.executionId)
    }
  } catch (error: any) {
    ElMessage.error(error.message || '执行失败')
  } finally {
    running.value = false
  }
}

// 停止执行
const stopExecution = async (row: Execution) => {
  try {
    await ElMessageBox.confirm('确定要停止当前执行吗？', '停止确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await stopExecutionApi(row.executionId)
    ElMessage.success('执行已停止')
    loadExecutions()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('停止执行失败')
    }
  }
}

// 查看报告
const viewReport = async (row: Execution) => {
  try {
    // 尝试获取现有报告
    const reportRes = await getReportByExecutionIdApi(row.executionId)
    if (reportRes.data) {
      router.push(`/reports/${reportRes.data.reportId}`)
    } else {
      // 如果没有报告，生成新报告
      const genRes = await generateReportApi(row.executionId)
      if (genRes.data) {
        router.push(`/reports/${genRes.data.reportId}`)
      }
    }
  } catch (error) {
    ElMessage.error('获取报告失败')
  }
}

// 查看步骤结果
const viewStepResults = async (row: Execution) => {
  currentExecutionId.value = row.executionId
  try {
    const res = await getStepResultsApi(row.executionId)
    currentStepResults.value = res.data || res || []
    stepDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载步骤结果失败')
  }
}

// 删除执行记录
const deleteExecution = async (row: Execution) => {
  try {
    await ElMessageBox.confirm(`确定要删除执行记录 "${row.executionId}" 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteExecutionApi(row.executionId)
    ElMessage.success('删除成功')
    loadExecutions()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
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

// 获取模式标签类型
const getModeTagType = (mode: string) => {
  const types: Record<string, any> = {
    manual: '',
    auto: 'success',
    scheduled: 'warning'
  }
  return types[mode] || ''
}

// 获取模式标签
const getModeLabel = (mode: string) => {
  const labels: Record<string, string> = {
    manual: '手动',
    auto: '自动',
    scheduled: '定时'
  }
  return labels[mode] || mode
}

// 格式化日期
const formatDate = (dateStr: string | null) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 格式化日期时间
const formatDateTime = (dateStr: string | null) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

// ==================== WebSocket 实时日志相关 ====================

// 打开执行日志对话框
const openExecutionLog = (executionId: string) => {
  currentExecutionId.value = executionId
  executionLogs.value = []
  executionStatus.value = 'running'
  totalSteps.value = 0
  passedSteps.value = 0
  logDialogVisible.value = true

  // 连接 WebSocket
  connectWebSocket(executionId)
}

// 连接 WebSocket
const connectWebSocket = (executionId: string) => {
  console.log('[Executions] 准备连接 WebSocket，executionId:', executionId)

  wsClient.connect(
    executionId,
    (message) => {
      handleWebSocketMessage(message)
    },
    () => {
      console.log('[Executions] WebSocket 连接成功')
      addLog('log', '已连接到实时日志服务')
    },
    () => {
      console.log('[Executions] WebSocket 断开连接')
    }
  )
}

// 断开 WebSocket
const disconnectWebSocket = () => {
  wsClient.disconnect()
  console.log('[Executions] 已断开 WebSocket 连接')
}

// 处理 WebSocket 消息
const handleWebSocketMessage = (message: any) => {
  console.log('[Executions] 收到 WebSocket 消息:', message)

  switch (message.type) {
    case 'step_start':
      addLog('step_start', `开始执行步骤 ${message.stepId}: ${message.description}`)
      break

    case 'step_end':
      const statusLabel = getStatusLabel(message.status)
      const icon = message.status === 'passed' ? '✓' : '✗'
      addLog('step_end', `${icon} 步骤 ${message.stepId} ${statusLabel}`)
      if (message.status === 'passed') {
        passedSteps.value++
      }
      break

    case 'log':
      addLog('log', message.content)
      break

    case 'error':
      addLog('error', `错误：${message.errorMessage}`)
      break

    case 'complete':
      executionStatus.value = message.status
      totalSteps.value = message.totalSteps || 0
      passedSteps.value = message.passedSteps || 0
      addLog('complete', `执行完成！状态：${getStatusLabel(message.status)}, 总计：${message.totalSteps} 步骤，通过：${message.passedSteps} 步骤`)
      // 执行完成后刷新列表
      setTimeout(() => {
        loadExecutions()
      }, 1000)
      break

    case 'screenshot':
      addLog('screenshot', `步骤 ${message.stepId} 已截图：${message.screenshotPath}`)
      break

    default:
      console.log('[Executions] 未知消息类型:', message.type)
  }

  // 滚动到底部
  nextTick(() => {
    scrollToBottom()
  })
}

// 添加日志
const addLog = (type: string, message: string) => {
  executionLogs.value.push({
    type,
    message,
    timestamp: Date.now()
  })
}

// 滚动到底部
const scrollToBottom = () => {
  if (logContentRef.value) {
    logContentRef.value.scrollTop = logContentRef.value.scrollHeight
  }
}

// 格式化日志时间
const formatLogTime = (timestamp: number) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { hour12: false })
}

// 获取日志图标
const getLogIcon = (type: string) => {
  const icons: Record<string, string> = {
    step_start: '▶',
    step_end: '✓',
    log: 'ℹ',
    error: '✗',
    complete: '✓',
    screenshot: '📷'
  }
  return icons[type] || '•'
}

// 停止当前执行
const stopCurrentExecution = async () => {
  try {
    await ElMessageBox.confirm('确定要停止当前执行吗？', '停止确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await stopExecutionApi(currentExecutionId.value)
    ElMessage.success('执行已停止')
    disconnectWebSocket()
    logDialogVisible.value = false
    loadExecutions()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('停止执行失败')
    }
  }
}

onMounted(() => {
  loadExecutions()
})
</script>

<style scoped>
.executions-container {
  padding: 0;
}

.executions-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

/* 实时日志样式 */
.log-container {
  display: flex;
  flex-direction: column;
  height: 500px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.log-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  border-radius: 4px 4px 0 0;
}

.log-progress {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.log-content {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
}

.log-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 6px 8px;
  margin-bottom: 4px;
  border-radius: 4px;
  animation: slideIn 0.2s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.log-item.step_start {
  background-color: #ecf5ff;
  color: #409eff;
}

.log-item.step_end {
  background-color: #f0f9ff;
  color: #67c23a;
}

.log-item.log {
  background-color: #f5f7fa;
  color: #606266;
}

.log-item.error {
  background-color: #fef0f0;
  color: #f56c6c;
}

.log-item.complete {
  background-color: #f0f9ff;
  color: #67c23a;
  font-weight: 500;
}

.log-item.screenshot {
  background-color: #ecf5ff;
  color: #909399;
}

.log-time {
  flex-shrink: 0;
  font-size: 12px;
  color: #909399;
  min-width: 70px;
}

.log-icon {
  flex-shrink: 0;
  font-size: 14px;
  width: 16px;
  text-align: center;
}

.log-message {
  flex: 1;
  word-break: break-word;
}

.log-empty {
  text-align: center;
  color: #909399;
  padding: 40px;
  font-style: italic;
}
</style>

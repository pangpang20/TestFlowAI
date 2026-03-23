<template>
  <div class="report-detail-container">
    <el-card class="report-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <div class="title-section">
            <el-button type="primary" link @click="$router.back()">
              <el-icon><ArrowLeft /></el-icon>
            </el-button>
            <span class="report-title">测试报告</span>
          </div>
          <div class="action-section">
            <el-button type="success" @click="downloadReport">
              <el-icon><Download /></el-icon>
              下载报告
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="reportData" class="report-content">
        <!-- 报告摘要 -->
        <el-card class="summary-card" shadow="hover">
          <template #header>
            <span class="section-title">执行摘要</span>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="测试名称">
              {{ reportData.summary?.testName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="执行 ID">
              {{ reportData.summary?.executionId || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="执行模式">
              <el-tag :type="getModeTagType(reportData.summary?.mode)" size="small">
                {{ getModeLabel(reportData.summary?.mode) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="执行状态">
              <el-tag :type="getStatusTagType(reportData.summary?.status)" size="small">
                {{ getStatusLabel(reportData.summary?.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="开始时间">
              {{ formatDateTime(reportData.summary?.startTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="结束时间">
              {{ formatDateTime(reportData.summary?.endTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="持续时间">
              {{ reportData.summary?.duration || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="总步骤数">
              {{ reportData.summary?.totalSteps || 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="通过步骤">
              <span style="color: #67c23a">{{ reportData.summary?.passedSteps || 0 }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="失败步骤">
              <span style="color: #f56c6c">{{ reportData.summary?.failedSteps || 0 }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="通过率">
              <el-progress
                :percentage="getPassRate(reportData.summary)"
                :color="getPassRateColor(reportData.summary)"
              />
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 测试信息 -->
        <el-card class="info-card" shadow="hover" style="margin-top: 20px">
          <template #header>
            <span class="section-title">测试流信息</span>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="测试流标题" :span="3">
              {{ reportData.details?.testInfo?.title || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="版本号">
              {{ reportData.details?.testInfo?.version || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="应用 URL" :span="2">
              <el-link :href="reportData.details?.testInfo?.appUrl" target="_blank" type="primary">
                {{ reportData.details?.testInfo?.appUrl || '-' }}
              </el-link>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 步骤详情 -->
        <el-card class="steps-card" shadow="hover" style="margin-top: 20px">
          <template #header>
            <span class="section-title">步骤执行详情</span>
          </template>
          <el-table :data="stepDetails" style="width: 100%" :default-sort="{prop: 'stepId', order: 'ascending'}">
            <el-table-column prop="stepId" label="步骤 ID" width="80" sortable />
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
            <el-table-column prop="duration" label="执行时长" width="100">
              <template #default="{ row }">
                {{ row.duration || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="errorMessage" label="结果/错误信息" show-overflow-tooltip min-width="300">
              <template #default="{ row }">
                <div v-if="row.status === 'passed'" style="color: #67c23a">
                  <el-icon><CircleCheck /></el-icon> 执行成功
                </div>
                <div v-else-if="row.status === 'failed'" style="color: #f56c6c">
                  <el-icon><CircleClose /></el-icon>
                  {{ row.errorMessage || '执行失败' }}
                </div>
                <div v-else style="color: #909399">
                  {{ getStatusLabel(row.status) }}
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>

      <el-empty v-else description="暂无报告数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Download, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { getReportDataApi, downloadReportApi } from '@/api/execution'
import type { ReportData } from '@/types/execution'

const route = useRoute()
const loading = ref(false)
const reportData = ref<ReportData | null>(null)

// 步骤详情
const stepDetails = computed(() => {
  return reportData.value?.details?.stepDetails || []
})

// 获取报告数据
const loadReportData = async () => {
  const reportId = route.params.reportId as string
  if (!reportId) {
    ElMessage.error('报告 ID 不能为空')
    return
  }

  loading.value = true
  try {
    const res = await getReportDataApi(reportId)
    reportData.value = res.data || res
  } catch (error) {
    ElMessage.error('加载报告数据失败')
  } finally {
    loading.value = false
  }
}

// 下载报告
const downloadReport = async () => {
  const reportId = route.params.reportId as string
  try {
    const blob = await downloadReportApi(reportId)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `report-${reportId}.json`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (error) {
    ElMessage.error('下载报告失败')
  }
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    pending: 'info',
    running: 'warning',
    passed: 'success',
    failed: 'danger',
    stopped: 'info',
    skipped: 'info'
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
    stopped: '已停止',
    skipped: '已跳过'
  }
  return labels[status] || status
}

// 获取模式标签类型
const getModeTagType = (mode?: string) => {
  if (!mode) return ''
  const types: Record<string, any> = {
    manual: '',
    auto: 'success',
    scheduled: 'warning'
  }
  return types[mode] || ''
}

// 获取模式标签
const getModeLabel = (mode?: string) => {
  if (!mode) return '-'
  const labels: Record<string, string> = {
    manual: '手动',
    auto: '自动',
    scheduled: '定时'
  }
  return labels[mode] || mode
}

// 计算通过率
const getPassRate = (summary?: any) => {
  if (!summary || !summary.totalSteps || summary.totalSteps === 0) return 0
  return Math.round((summary.passedSteps / summary.totalSteps) * 100)
}

// 获取通过率颜色
const getPassRateColor = (summary?: any) => {
  const rate = getPassRate(summary)
  if (rate >= 90) return '#67c23a'
  if (rate >= 70) return '#e6a23c'
  return '#f56c6c'
}

// 格式化日期时间
const formatDateTime = (dateStr?: string | null) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  loadReportData()
})
</script>

<style scoped>
.report-detail-container {
  padding: 0;
  height: 100%;
}

.report-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.report-title {
  font-size: 18px;
  font-weight: bold;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.summary-card,
.info-card,
.steps-card {
  margin-bottom: 20px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  width: 120px;
}
</style>

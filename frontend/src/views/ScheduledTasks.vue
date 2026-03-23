<template>
  <div class="scheduled-tasks-container">
    <el-card class="tasks-card">
      <template #header>
        <div class="card-header">
          <span>定时任务</span>
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            新建任务
          </el-button>
        </div>
      </template>

      <!-- 任务列表 -->
      <el-table :data="tasks" style="width: 100%" v-loading="loading">
        <el-table-column prop="taskName" label="任务名称" min-width="150" />
        <el-table-column prop="title" label="任务标题" min-width="150" />
        <el-table-column prop="testId" label="测试流 ID" min-width="200" show-overflow-tooltip />
        <el-table-column prop="cronExpression" label="Cron 表达式" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
              {{ row.status === 'active' ? '运行中' : '已停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastExecutedAt" label="上次执行" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.lastExecutedAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="nextExecutionAt" label="下次执行" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.nextExecutionAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'active'"
              link
              type="warning"
              @click="toggleStatus(row, 'inactive')"
            >
              停用
            </el-button>
            <el-button
              v-else
              link
              type="success"
              @click="toggleStatus(row, 'active')"
            >
              激活
            </el-button>
            <el-button link type="primary" @click="triggerTask(row)">
              执行
            </el-button>
            <el-button link type="danger" @click="deleteTask(row)">
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
        @size-change="loadTasks"
        @current-change="loadTasks"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 创建/编辑任务对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑任务' : '新建任务'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="formData.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入任务标题（可选）" />
        </el-form-item>
        <el-form-item label="测试流 ID" prop="testId">
          <el-input v-model="formData.testId" placeholder="请输入测试流 ID" />
        </el-form-item>
        <el-form-item label="Cron 表达式" prop="cronExpression">
          <el-input v-model="formData.cronExpression" placeholder="例如：0 0 12 * * ? 每天中午 12 点">
            <template #append>
              <el-button @click="showCronHelp">
                <el-icon><QuestionFilled /></el-icon>
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="任务描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述（可选）"
          />
        </el-form-item>
        <el-form-item>
          <el-alert
            title="Cron 表达式示例"
            type="info"
            :closable="false"
            style="font-size: 12px;"
          >
            <div>• <code>0 0 12 * * ?</code> - 每天中午 12 点执行</div>
            <div>• <code>0 0/5 14 * * ?</code> - 每天下午 2 点到 2:55，每 5 分钟执行</div>
            <div>• <code>0 0 1 ? * MON</code> - 每周一凌晨 1 点执行</div>
            <div>• <code>0 0 1 1 * ?</code> - 每月 1 号凌晨 1 点执行</div>
          </el-alert>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- Cron 帮助对话框 -->
    <el-dialog v-model="cronHelpVisible" title="Cron 表达式帮助" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="格式">
          分钟 小时 日期 月份 星期
        </el-descriptions-item>
        <el-descriptions-item label="特殊字符">
          <div><code>*</code> - 匹配任意值</div>
          <div><code>,</code> - 分隔多个值</div>
          <div><code>-</code> - 指定范围</div>
          <div><code>/</code> - 指定步长</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="cronHelpVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, QuestionFilled } from '@element-plus/icons-vue'
import {
  getScheduledTasksApi,
  createScheduledTaskApi,
  updateScheduledTaskApi,
  toggleTaskStatusApi,
  triggerTaskApi,
  deleteScheduledTaskApi
} from '@/api/scheduled-task'
import type { ScheduledTask } from '@/types/scheduled-task'

const loading = ref(false)
const submitting = ref(false)
const tasks = ref<ScheduledTask[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const cronHelpVisible = ref(false)

const formData = reactive({
  taskId: '',
  taskName: '',
  testId: '',
  cronExpression: '',
  title: '',
  description: ''
})

const formRules: FormRules = {
  taskName: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  testId: [
    { required: true, message: '请输入测试流 ID', trigger: 'blur' }
  ],
  cronExpression: [
    { required: true, message: '请输入 Cron 表达式', trigger: 'blur' },
    {
      pattern: /^\S+\s+\S+\s+\S+\s+\S+\s+\S+$/,
      message: 'Cron 表达式格式不正确，应为 5 个字段',
      trigger: 'blur'
    }
  ]
}

// 获取任务列表
const loadTasks = async () => {
  loading.value = true
  try {
    const res = await getScheduledTasksApi(currentPage.value, pageSize.value)
    tasks.value = res.data?.list || res.list || []
    total.value = res.data?.total || res.total || 0
  } catch (error) {
    ElMessage.error('加载任务列表失败')
  } finally {
    loading.value = false
  }
}

// 显示创建对话框
const showCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.taskId = ''
  formData.taskName = ''
  formData.testId = ''
  formData.cronExpression = ''
  formData.title = ''
  formData.description = ''
  formRef.value?.clearValidate()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      if (isEdit.value) {
        await updateScheduledTaskApi(formData.taskId, formData)
        ElMessage.success('更新成功')
      } else {
        await createScheduledTaskApi(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadTasks()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    } finally {
      submitting.value = false
    }
  })
}

// 切换任务状态
const toggleStatus = async (row: ScheduledTask, status: string) => {
  try {
    await toggleTaskStatusApi(row.taskId, status)
    ElMessage.success('状态更新成功')
    loadTasks()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

// 手动触发任务
const triggerTask = async (row: ScheduledTask) => {
  try {
    await ElMessageBox.confirm('确定要手动执行此任务吗？', '执行确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await triggerTaskApi(row.taskId)
    ElMessage.success('任务已触发，执行结果将在后台运行')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('触发任务失败')
    }
  }
}

// 删除任务
const deleteTask = async (row: ScheduledTask) => {
  try {
    await ElMessageBox.confirm(`确定要删除任务 "${row.taskName}" 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteScheduledTaskApi(row.taskId)
    ElMessage.success('删除成功')
    loadTasks()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 显示 Cron 帮助
const showCronHelp = () => {
  cronHelpVisible.value = true
}

// 格式化日期时间
const formatDateTime = (dateStr: string | null) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  loadTasks()
})
</script>

<style scoped>
.scheduled-tasks-container {
  padding: 0;
}

.tasks-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-alert) {
  padding: 8px 12px;
}

:deep(.el-alert code) {
  background-color: #f4f4f5;
  padding: 2px 6px;
  border-radius: 4px;
  color: #d93025;
  font-family: 'Courier New', monospace;
}
</style>

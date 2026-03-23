<template>
  <div class="testflows-container">
    <el-card class="testflows-card">
      <template #header>
        <div class="card-header">
          <span>测试管理</span>
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            新建测试流
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="filter-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索测试流标题..."
          style="width: 300px"
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <!-- 测试流列表 -->
      <el-table :data="filteredTestFlows" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="测试流名称" min-width="250" />
        <el-table-column prop="version" label="版本" width="100" />
        <el-table-column prop="appUrl" label="应用 URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="tags" label="标签" width="150">
          <template #default="{ row }">
            <el-tag v-for="(tag, index) in parseTags(row.tags)" :key="index" size="small" style="margin-right: 4px;">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showEditDialog(row)">编辑</el-button>
            <el-button link type="primary" @click="handleDuplicate(row)">复制</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑测试流对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑测试流' : '新建测试流'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="测试流名称" prop="title">
          <el-input v-model="formData.title" placeholder="请输入测试流名称" />
        </el-form-item>
        <el-form-item label="版本号" prop="version">
          <el-input v-model="formData.version" placeholder="例如：1.0.0" />
        </el-form-item>
        <el-form-item label="应用 URL" prop="appUrl">
          <el-input v-model="formData.appUrl" placeholder="例如：http://localhost:3000" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="tagsInput" placeholder="请输入标签，用逗号分隔" />
        </el-form-item>
        <el-form-item label="测试步骤" prop="steps">
          <el-input
            v-model="formData.steps"
            type="textarea"
            :rows="8"
            placeholder='请输入测试步骤 JSON 数组，例如：[{"type":"click","selector":"#btn","description":"点击按钮"}]'
          />
        </el-form-item>
        <el-form-item label="变量定义" prop="variables">
          <el-input
            v-model="formData.variables"
            type="textarea"
            :rows="4"
            placeholder='请输入变量定义 JSON 对象，例如：{"username":"test","password":"123456"}'
          />
        </el-form-item>
        <el-form-item label="预期报告" prop="expectedReport">
          <el-input
            v-model="formData.expectedReport"
            type="textarea"
            :rows="4"
            placeholder='请输入预期报告 JSON 对象，例如：{"status":"passed","totalSteps":10}'
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import {
  getTestFlowsApi,
  createTestFlowApi,
  updateTestFlowApi,
  deleteTestFlowApi,
  duplicateTestFlowApi,
  searchTestFlowsApi
} from '@/api/testflow'
import type { TestFlowDto } from '@/api/testflow'

const loading = ref(false)
const submitting = ref(false)
const testFlows = ref<TestFlowDto[]>([])
const searchKeyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const tagsInput = ref('')

const formData = reactive<TestFlowDto>({
  testId: '',
  title: '',
  version: '',
  appUrl: '',
  steps: '',
  variables: '',
  tags: '',
  expectedReport: '',
  projectId: '',
  createdAt: '',
  updatedAt: ''
})

// 自定义验证规则
const validateJson = (rule: any, value: string, callback: (error?: string | Error) => void) => {
  if (!value) {
    callback() // 空值允许通过（字段可选）
    return
  }
  try {
    JSON.parse(value)
    callback()
  } catch (e) {
    callback(new Error('请输入有效的 JSON 格式'))
  }
}

const validateHttpUrl = (rule: any, value: string, callback: (error?: string | Error) => void) => {
  if (!value) {
    callback() // 空值允许通过（字段可选）
    return
  }
  const urlPattern = /^https?:\/\/.+$/
  if (urlPattern.test(value)) {
    callback()
  } else {
    callback(new Error('请输入有效的 HTTP/HTTPS URL'))
  }
}

const formRules: FormRules = {
  title: [
    { required: true, message: '请输入测试流名称', trigger: 'blur' }
  ],
  version: [
    { required: true, message: '请输入版本号', trigger: 'blur' }
  ],
  appUrl: [
    { validator: validateHttpUrl, trigger: 'blur' }
  ],
  steps: [
    { validator: validateJson, trigger: 'blur' }
  ],
  variables: [
    { validator: validateJson, trigger: 'blur' }
  ],
  expectedReport: [
    { validator: validateJson, trigger: 'blur' }
  ]
}

// 筛选后的测试流列表
const filteredTestFlows = computed(() => {
  if (searchKeyword.value) {
    return testFlows.value.filter(tf =>
      tf.title.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  return testFlows.value
})

// 解析标签
const parseTags = (tagsStr: string): string[] => {
  if (!tagsStr) return []
  try {
    const parsed = JSON.parse(tagsStr)
    return Array.isArray(parsed) ? parsed : [parsed]
  } catch {
    return tagsStr.split(',').map(t => t.trim()).filter(t => t)
  }
}

// 格式化日期
const formatDate = (dateStr: string | null): string => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 获取测试流列表
const loadTestFlows = async () => {
  loading.value = true
  try {
    const res = await getTestFlowsApi()
    testFlows.value = res.data || res
  } catch (error) {
    ElMessage.error('加载测试流列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = async () => {
  if (!searchKeyword.value) {
    loadTestFlows()
    return
  }
  try {
    const res = await searchTestFlowsApi(searchKeyword.value)
    testFlows.value = res.data || res
  } catch (error) {
    ElMessage.error('搜索失败')
  }
}

// 显示创建对话框
const showCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row: TestFlowDto) => {
  isEdit.value = true
  resetForm()
  formData.testId = row.testId
  formData.title = row.title
  formData.version = row.version
  formData.appUrl = row.appUrl
  formData.steps = row.steps || ''
  formData.variables = row.variables || ''
  formData.tags = row.tags || ''
  formData.expectedReport = row.expectedReport || ''
  formData.projectId = row.projectId || ''
  tagsInput.value = parseTags(row.tags).join(', ')
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.testId = ''
  formData.title = ''
  formData.version = ''
  formData.appUrl = ''
  formData.steps = ''
  formData.variables = ''
  formData.tags = ''
  formData.expectedReport = ''
  formData.projectId = ''
  tagsInput.value = ''
  formRef.value?.clearValidate()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      // 将标签输入转换为 JSON 字符串
      if (tagsInput.value) {
        formData.tags = JSON.stringify(tagsInput.value.split(',').map(t => t.trim()).filter(t => t))
      }

      if (isEdit.value) {
        await updateTestFlowApi(formData.testId, formData)
        ElMessage.success('更新成功')
      } else {
        await createTestFlowApi(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadTestFlows()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    } finally {
      submitting.value = false
    }
  })
}

// 复制测试流
const handleDuplicate = async (row: TestFlowDto) => {
  try {
    await duplicateTestFlowApi(row.testId)
    ElMessage.success('复制成功')
    loadTestFlows()
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

// 删除测试流
const handleDelete = (row: TestFlowDto) => {
  ElMessageBox.confirm(
    `确定要删除测试流"${row.title}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteTestFlowApi(row.testId)
      ElMessage.success('删除成功')
      loadTestFlows()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消
  })
}

onMounted(() => {
  loadTestFlows()
})
</script>

<style scoped>
.testflows-container {
  padding: 0;
}

.testflows-card {
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
</style>

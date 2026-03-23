<template>
  <div class="projects-container">
    <el-card class="projects-card">
      <template #header>
        <div class="card-header">
          <span>项目管理</span>
          <el-button type="primary" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            新建项目
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="filter-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索项目名称..."
          style="width: 300px"
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="filterStatus" placeholder="项目状态" style="width: 150px" clearable @change="handleFilter">
          <el-option label="进行中" value="active" />
          <el-option label="已完成" value="completed" />
          <el-option label="已归档" value="archived" />
        </el-select>
      </div>

      <!-- 项目列表 -->
      <el-table :data="filteredProjects" style="width: 100%" v-loading="loading">
        <el-table-column prop="projectName" label="项目名称" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="owner" label="负责人" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="150">
          <template #default="{ row }">
            <el-progress :percentage="row.progress" :status="row.progress === 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showEditDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑项目对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑项目' : '新建项目'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="formData.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        <el-form-item label="负责人" prop="owner">
          <el-input v-model="formData.owner" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="进行中" value="active" />
            <el-option label="已完成" value="completed" />
            <el-option label="已归档" value="archived" />
          </el-select>
        </el-form-item>
        <el-form-item label="进度" prop="progress">
          <el-slider v-model="formData.progress" :max="100" show-input />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
            v-model="formData.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="formData.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
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
  getProjectsApi,
  createProjectApi,
  updateProjectApi,
  deleteProjectApi,
  searchProjectsApi
} from '@/api/project'
import type { ProjectDto } from '@/api/project'

const loading = ref(false)
const submitting = ref(false)
const projects = ref<ProjectDto[]>([])
const searchKeyword = ref('')
const filterStatus = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive<ProjectDto>({
  projectId: '',
  projectName: '',
  description: '',
  owner: '',
  status: 'active',
  startDate: null,
  endDate: null,
  progress: 0,
  createdAt: '',
  updatedAt: ''
})

const formRules: FormRules = {
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' }
  ],
  owner: [
    { required: true, message: '请输入负责人', trigger: 'blur' }
  ]
}

// 筛选后的项目列表
const filteredProjects = computed(() => {
  let result = projects.value
  if (filterStatus.value) {
    result = result.filter(p => p.status === filterStatus.value)
  }
  if (searchKeyword.value) {
    result = result.filter(p =>
      p.projectName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  return result
})

// 获取项目列表
const loadProjects = async () => {
  loading.value = true
  try {
    const res = await getProjectsApi()
    projects.value = res.data || res
  } catch (error) {
    ElMessage.error('加载项目列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  // 前端搜索已处理
}

// 筛选处理
const handleFilter = () => {
  // 前端筛选已处理
}

// 显示创建对话框
const showCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row: ProjectDto) => {
  isEdit.value = true
  resetForm()
  formData.projectId = row.projectId
  formData.projectName = row.projectName
  formData.description = row.description || ''
  formData.owner = row.owner || ''
  formData.status = row.status
  formData.progress = row.progress
  formData.startDate = row.startDate
  formData.endDate = row.endDate
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.projectId = ''
  formData.projectName = ''
  formData.description = ''
  formData.owner = ''
  formData.status = 'active'
  formData.progress = 0
  formData.startDate = null
  formData.endDate = null
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
        await updateProjectApi(formData.projectId, formData)
        ElMessage.success('更新成功')
      } else {
        await createProjectApi(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadProjects()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    } finally {
      submitting.value = false
    }
  })
}

// 删除项目
const handleDelete = (row: ProjectDto) => {
  ElMessageBox.confirm(
    `确定要删除项目"${row.projectName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteProjectApi(row.projectId)
      ElMessage.success('删除成功')
      loadProjects()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消
  })
}

// 获取状态类型
const getStatusType = (status: string) => {
  const types: Record<string, 'success' | 'warning' | 'info'> = {
    active: 'warning',
    completed: 'success',
    archived: 'info'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    active: '进行中',
    completed: '已完成',
    archived: '已归档'
  }
  return texts[status] || status
}

onMounted(() => {
  loadProjects()
})
</script>

<style scoped>
.projects-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.projects-card {
  max-width: 1400px;
  margin: 0 auto;
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

<template>
  <div class="permission-management">
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索权限名称或编码..."
        style="width: 300px"
        clearable
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        新建权限
      </el-button>
    </div>

    <!-- 权限列表 -->
    <el-table :data="filteredPermissions" style="width: 100%" v-loading="loading">
      <el-table-column prop="permissionName" label="权限名称" min-width="150" />
      <el-table-column prop="permissionCode" label="权限编码" min-width="180" />
      <el-table-column prop="resource" label="资源" width="120" />
      <el-table-column prop="action" label="操作" width="100">
        <template #default="{ row }">
          <el-tag :type="getActionType(row.action)">
            {{ getActionText(row.action) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showEditDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建/编辑权限对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑权限' : '新建权限'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="formData.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="formData.permissionCode" placeholder="例如：user:create" />
        </el-form-item>
        <el-form-item label="资源类型" prop="resource">
          <el-select v-model="formData.resource" placeholder="请选择资源类型" style="width: 100%">
            <el-option label="用户" value="user" />
            <el-option label="角色" value="role" />
            <el-option label="权限" value="permission" />
            <el-option label="项目" value="project" />
            <el-option label="测试流" value="testflow" />
            <el-option label="系统" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型" prop="action">
          <el-select v-model="formData.action" placeholder="请选择操作类型" style="width: 100%">
            <el-option label="查看" value="read" />
            <el-option label="创建" value="create" />
            <el-option label="编辑" value="update" />
            <el-option label="删除" value="delete" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入权限描述"
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
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { getPermissionsApi, createPermissionApi, updatePermissionApi, deletePermissionApi } from '@/api/permission'
import type { Permission, CreatePermissionDto } from '@/api/permission'

const loading = ref(false)
const submitting = ref(false)
const permissions = ref<Permission[]>([])
const searchKeyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const currentPermission = ref<Permission | null>(null)

const formData = reactive<CreatePermissionDto>({
  permissionCode: '',
  permissionName: '',
  resource: '',
  action: '',
  description: ''
})

const formRules: FormRules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' }
  ],
  resource: [
    { required: true, message: '请选择资源类型', trigger: 'change' }
  ],
  action: [
    { required: true, message: '请选择操作类型', trigger: 'change' }
  ]
}

// 筛选后的权限列表
const filteredPermissions = computed(() => {
  if (searchKeyword.value) {
    return permissions.value.filter(p =>
      p.permissionName.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      p.permissionCode?.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  return permissions.value
})

// 获取权限列表
const loadPermissions = async () => {
  loading.value = true
  try {
    const res = await getPermissionsApi()
    permissions.value = res.data || res || []
  } catch (error) {
    ElMessage.error('加载权限列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {}

// 显示创建对话框
const showCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (row: Permission) => {
  isEdit.value = true
  resetForm()
  currentPermission.value = row
  formData.permissionName = row.permissionName
  formData.permissionCode = row.permissionCode
  formData.resource = row.resource || ''
  formData.action = row.action || ''
  formData.description = row.description || ''
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.permissionCode = ''
  formData.permissionName = ''
  formData.resource = ''
  formData.action = ''
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
      if (isEdit.value && currentPermission.value) {
        await updatePermissionApi(currentPermission.value.permissionId, formData)
        ElMessage.success('更新成功')
      } else {
        await createPermissionApi(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadPermissions()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    } finally {
      submitting.value = false
    }
  })
}

// 删除权限
const handleDelete = (row: Permission) => {
  ElMessageBox.confirm(
    `确定要删除权限"${row.permissionName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deletePermissionApi(row.permissionId)
      ElMessage.success('删除成功')
      loadPermissions()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 获取操作类型
const getActionType = (action: string): '' | 'success' | 'warning' | 'danger' | 'info' => {
  const types: Record<string, any> = {
    read: 'info',
    create: 'success',
    update: 'warning',
    delete: 'danger'
  }
  return types[action] || 'info'
}

// 获取操作文本
const getActionText = (action: string): string => {
  const texts: Record<string, string> = {
    read: '查看',
    create: '创建',
    update: '编辑',
    delete: '删除'
  }
  return texts[action] || action
}

onMounted(() => {
  loadPermissions()
})
</script>

<style scoped>
.permission-management {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>

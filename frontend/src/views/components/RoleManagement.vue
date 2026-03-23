<template>
  <div class="role-management">
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索角色名称..."
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
        新建角色
      </el-button>
    </div>

    <!-- 角色列表 -->
    <el-table :data="filteredRoles" style="width: 100%" v-loading="loading">
      <el-table-column prop="roleName" label="角色名称" min-width="150" />
      <el-table-column prop="displayName" label="显示名称" min-width="150" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showEditDialog(row)">编辑</el-button>
          <el-button link type="primary" @click="showAssignPermissionDialog(row)">分配权限</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑角色' : '新建角色'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称（英文）" />
        </el-form-item>
        <el-form-item label="显示名称" prop="displayName">
          <el-input v-model="formData.displayName" placeholder="请输入显示名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
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

    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form label-width="80px">
        <el-form-item label="角色">
          <el-input :model-value="currentRole?.displayName" disabled />
        </el-form-item>
        <el-form-item label="选择权限">
          <el-tree
            ref="permissionTreeRef"
            :data="permissions"
            :props="{ children: 'children', label: 'permissionName', value: 'permissionId' }"
            show-checkbox
            node-key="permissionId"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignPermission" :loading="assigningPermission">
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
import { getRolesApi, createRoleApi, updateRoleApi, deleteRoleApi, assignPermissionsToRoleApi, getRolePermissionsApi } from '@/api/role'
import type { Role, CreateRoleDto } from '@/api/role'
import { getPermissionsApi } from '@/api/permission'
import type { Permission } from '@/api/permission'

const loading = ref(false)
const submitting = ref(false)
const assigningPermission = ref(false)
const roles = ref<Role[]>([])
const permissions = ref<Permission[]>([])
const searchKeyword = ref('')

const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const currentRole = ref<Role | null>(null)
const permissionTreeRef = ref<any>()

const formData = reactive<CreateRoleDto>({
  roleName: '',
  displayName: '',
  description: ''
})

const formRules: FormRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  displayName: [
    { required: true, message: '请输入显示名称', trigger: 'blur' }
  ]
}

// 筛选后的角色列表
const filteredRoles = computed(() => {
  if (searchKeyword.value) {
    return roles.value.filter(r =>
      r.roleName.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      r.displayName?.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  return roles.value
})

// 获取角色列表
const loadRoles = async () => {
  loading.value = true
  try {
    const res = await getRolesApi()
    roles.value = res.data || res || []
  } catch (error) {
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

// 获取权限列表
const loadPermissions = async () => {
  try {
    const res = await getPermissionsApi()
    permissions.value = res.data || res || []
  } catch (error) {
    console.error('加载权限列表失败', error)
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
const showEditDialog = (row: Role) => {
  isEdit.value = true
  resetForm()
  formData.roleName = row.roleName
  formData.displayName = row.displayName || ''
  formData.description = row.description || ''
  currentRole.value = row
  dialogVisible.value = true
}

// 显示分配权限对话框
const showAssignPermissionDialog = async (row: Role) => {
  currentRole.value = row
  await loadPermissions()
  // 获取角色已有权限
  try {
    const res = await getRolePermissionsApi(row.roleId)
    if (res.data && res.data.length > 0) {
      const permissionIds = res.data.map((p: Permission) => p.permissionId)
      // 等待 DOM 更新后设置选中的权限
      setTimeout(() => {
        permissionTreeRef.value?.setCheckedKeys(permissionIds)
      }, 100)
    }
  } catch (error) {
    console.error('获取角色权限失败', error)
  }
  permissionDialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.roleName = ''
  formData.displayName = ''
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
      if (isEdit.value && currentRole.value) {
        await updateRoleApi(currentRole.value.roleId, formData)
        ElMessage.success('更新成功')
      } else {
        await createRoleApi(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadRoles()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    } finally {
      submitting.value = false
    }
  })
}

// 分配权限
const handleAssignPermission = async () => {
  assigningPermission.value = true
  try {
    if (!currentRole.value) return
    const checkedKeys = permissionTreeRef.value?.getCheckedKeys()
    await assignPermissionsToRoleApi(currentRole.value.roleId, checkedKeys)
    ElMessage.success('分配成功')
    permissionDialogVisible.value = false
  } catch (error) {
    ElMessage.error('分配失败')
  } finally {
    assigningPermission.value = false
  }
}

// 删除角色
const handleDelete = (row: Role) => {
  ElMessageBox.confirm(
    `确定要删除角色"${row.displayName || row.roleName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteRoleApi(row.roleId)
      ElMessage.success('删除成功')
      loadRoles()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 格式化日期
const formatDate = (dateStr: string | null): string => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  loadRoles()
})
</script>

<style scoped>
.role-management {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>

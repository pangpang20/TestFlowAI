<template>
  <div class="user-management">
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名或邮箱..."
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
        新建用户
      </el-button>
    </div>

    <!-- 用户列表 -->
    <el-table :data="filteredUsers" style="width: 100%" v-loading="loading">
      <el-table-column prop="username" label="用户名" min-width="150" />
      <el-table-column prop="email" label="邮箱" min-width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
            {{ row.status === 'active' ? '正常' : '禁用' }}
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
          <el-button link type="primary" @click="showAssignRoleDialog(row)">分配角色</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新建用户'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="active">正常</el-radio>
            <el-radio label="disabled">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form label-width="80px">
        <el-form-item label="用户">
          <el-input :model-value="currentUser?.username" disabled />
        </el-form-item>
        <el-form-item label="选择角色">
          <el-select v-model="selectedRoles" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roles"
              :key="role.roleId"
              :label="role.displayName || role.roleName"
              :value="role.roleId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignRole" :loading="assigningRole">
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
import { getRolesApi } from '@/api/role'
import type { Role } from '@/api/role'
import {
  getUsersApi,
  createUserApi,
  updateUserApi,
  deleteUserApi,
  getUserRolesApi,
  assignRolesToUserApi
} from '@/api/user'
import type { User, CreateUpdateUserDto } from '@/api/user'

const loading = ref(false)
const submitting = ref(false)
const assigningRole = ref(false)
const users = ref<User[]>([])
const roles = ref<Role[]>([])
const searchKeyword = ref('')

const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const currentUser = ref<User | null>(null)
const selectedRoles = ref<string[]>([])

const formData = reactive<CreateUpdateUserDto>({
  username: '',
  password: '',
  email: '',
  status: 'active'
})

const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 32, message: '用户名长度必须在 3-32 之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度必须在 8-20 之间', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 筛选后的用户列表
const filteredUsers = computed(() => {
  if (searchKeyword.value) {
    return users.value.filter(u =>
      u.username.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      u.email?.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  return users.value
})

// 获取用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUsersApi()
    users.value = res.data || res || []
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 获取角色列表
const loadRoles = async () => {
  try {
    const res = await getRolesApi()
    roles.value = res.data || res || []
  } catch (error) {
    console.error('加载角色列表失败', error)
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
const showEditDialog = (row: User) => {
  isEdit.value = true
  resetForm()
  formData.username = row.username
  formData.email = row.email
  formData.status = row.status
  currentUser.value = row
  dialogVisible.value = true
}

// 显示分配角色对话框
const showAssignRoleDialog = async (row: User) => {
  currentUser.value = row
  selectedRoles.value = []
  // 获取用户已有角色
  try {
    const res = await getUserRolesApi(row.userId)
    if (res.data && res.data.length > 0) {
      selectedRoles.value = res.data.map((r: Role) => r.roleId)
    }
  } catch (error) {
    console.error('获取用户角色失败', error)
  }
  await loadRoles()
  roleDialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.username = ''
  formData.password = ''
  formData.email = ''
  formData.status = 'active'
  formRef.value?.clearValidate()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      if (isEdit.value && currentUser.value) {
        await updateUserApi(currentUser.value.userId, formData)
        ElMessage.success('更新成功')
      } else {
        await createUserApi(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadUsers()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    } finally {
      submitting.value = false
    }
  })
}

// 分配角色
const handleAssignRole = async () => {
  assigningRole.value = true
  try {
    if (!currentUser.value) return
    await assignRolesToUserApi(currentUser.value.userId, selectedRoles.value)
    ElMessage.success('分配成功')
    roleDialogVisible.value = false
  } catch (error) {
    ElMessage.error('分配失败')
  } finally {
    assigningRole.value = false
  }
}

// 删除用户
const handleDelete = (row: User) => {
  ElMessageBox.confirm(
    `确定要删除用户"${row.username}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteUserApi(row.userId)
      ElMessage.success('删除成功')
      loadUsers()
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
  loadUsers()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>

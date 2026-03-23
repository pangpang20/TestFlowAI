<template>
  <div class="settings-container">
    <el-tabs v-model="activeTab" class="settings-tabs">
      <!-- 用户管理 -->
      <el-tab-pane label="用户管理" name="users">
        <UserManagement />
      </el-tab-pane>
      <!-- 角色管理 -->
      <el-tab-pane label="角色管理" name="roles">
        <RoleManagement />
      </el-tab-pane>
      <!-- 权限管理 -->
      <el-tab-pane label="权限管理" name="permissions">
        <PermissionManagement />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import UserManagement from './components/UserManagement.vue'
import RoleManagement from './components/RoleManagement.vue'
import PermissionManagement from './components/PermissionManagement.vue'

const activeTab = ref('users')
</script>

<style scoped>
.settings-container {
  height: 100%;
}

.settings-tabs {
  height: 100%;
}

.settings-tabs :deep(.el-tabs__content) {
  height: calc(100% - 55px);
  overflow: auto;
}
</style>

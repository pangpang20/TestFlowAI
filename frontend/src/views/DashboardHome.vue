<template>
  <div class="dashboard-home">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon projects">
              <el-icon><Folder /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">12</div>
              <div class="stat-label">项目总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon tests">
              <el-icon><Connection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">56</div>
              <div class="stat-label">测试用例</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon passed">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">89%</div>
              <div class="stat-label">通过率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon tasks">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">8</div>
              <div class="stat-label">待处理任务</div>
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
              <span>最近项目</span>
            </div>
          </template>
          <el-table :data="recentProjects" style="width: 100%">
            <el-table-column prop="name" label="项目名称" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.status === 'running' ? 'warning' : 'success'" size="small">
                  {{ row.status === 'running' ? '进行中' : '已完成' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="progress" label="进度" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统通知</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item timestamp="2024-01-15" placement="top">
              <el-card>
                <h4>测试报告已生成</h4>
                <p>项目 A 的测试报告已自动生成</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2024-01-14" placement="top">
              <el-card>
                <h4>新版本发布</h4>
                <p>TestFlowAI v2.0.0 已发布</p>
              </el-card>
            </el-timeline-item>
            <el-timeline-item timestamp="2024-01-13" placement="top">
              <el-card>
                <h4>系统维护通知</h4>
                <p>本周末将进行系统维护</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import {
  Folder,
  Connection,
  CircleCheck,
  List
} from '@element-plus/icons-vue'

const recentProjects = [
  { name: '项目 A', status: 'running', progress: '75%' },
  { name: '项目 B', status: 'completed', progress: '100%' },
  { name: '项目 C', status: 'running', progress: '30%' },
  { name: '项目 D', status: 'completed', progress: '100%' }
]
</script>

<style scoped>
.dashboard-home {
  height: 100%;
}

.stat-card {
  margin-bottom: 20px;
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
  font-size: 24px;
  font-weight: 600;
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
</style>

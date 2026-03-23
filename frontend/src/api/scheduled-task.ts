import request from '@/utils/request'
import type { ScheduledTask } from '@/types/scheduled-task'

/**
 * 定时任务 API
 */

/**
 * 创建定时任务
 */
export function createScheduledTaskApi(data: {
  taskName: string
  testId: string
  cronExpression: string
  title?: string
  description?: string
}) {
  return request<ScheduledTask>({
    url: '/api/scheduled-tasks',
    method: 'POST',
    data
  })
}

/**
 * 更新定时任务
 */
export function updateScheduledTaskApi(taskId: string, data: {
  taskName?: string
  cronExpression?: string
  title?: string
  description?: string
}) {
  return request<ScheduledTask>({
    url: `/api/scheduled-tasks/${taskId}`,
    method: 'PUT',
    data
  })
}

/**
 * 切换任务状态
 */
export function toggleTaskStatusApi(taskId: string, status: string) {
  return request({
    url: `/api/scheduled-tasks/${taskId}/status`,
    method: 'PATCH',
    data: { status }
  })
}

/**
 * 手动触发任务
 */
export function triggerTaskApi(taskId: string) {
  return request({
    url: `/api/scheduled-tasks/${taskId}/trigger`,
    method: 'POST'
  })
}

/**
 * 获取任务详情
 */
export function getScheduledTaskApi(taskId: string) {
  return request<ScheduledTask>({
    url: `/api/scheduled-tasks/${taskId}`,
    method: 'GET'
  })
}

/**
 * 获取任务列表
 */
export function getScheduledTasksApi(page: number = 1, size: number = 10) {
  return request<{ list: ScheduledTask[]; total: number; page: number; size: number }>({
    url: '/api/scheduled-tasks',
    method: 'GET',
    params: { page, size }
  })
}

/**
 * 根据测试流 ID 获取任务列表
 */
export function getTasksByTestIdApi(testId: string) {
  return request<ScheduledTask[]>({
    url: `/api/scheduled-tasks/test/${testId}`,
    method: 'GET'
  })
}

/**
 * 删除定时任务
 */
export function deleteScheduledTaskApi(taskId: string) {
  return request({
    url: `/api/scheduled-tasks/${taskId}`,
    method: 'DELETE'
  })
}

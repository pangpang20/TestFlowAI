/**
 * 定时任务类型定义
 */
export interface ScheduledTask {
  taskId: string
  taskName: string
  testId: string
  title: string
  description: string
  cronExpression: string
  status: string
  lastExecutedAt: string | null
  nextExecutionAt: string | null
  createdAt: string | null
  updatedAt: string | null
  deletedAt?: string | null
  deleted?: number
  createdBy?: string
  updatedBy?: string
  deletedBy?: string
}

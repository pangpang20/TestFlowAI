/**
 * 仪表盘统计类型定义
 */
export interface OverviewStats {
  projectCount: number
  testFlowCount: number
  passRate: number
  activeTasks: number
  executionStatus: {
    pending?: number
    running?: number
    passed?: number
    failed?: number
    stopped?: number
  }
  recentExecutions: Execution[]
}

export interface Execution {
  executionId: string
  testId: string
  status: string
  startTime: string
  totalSteps: number
  passedSteps: number
}

export interface Project {
  projectId: string
  name: string
  description: string
  status: string
}

export interface TestFlow {
  testId: string
  title: string
  projectId: string
  version: string
}

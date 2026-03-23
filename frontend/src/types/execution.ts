/**
 * 执行记录类型定义
 */
export interface Execution {
  executionId: string
  testId: string
  mode: string
  status: string
  startTime: string | null
  endTime: string | null
  totalSteps: number
  passedSteps: number
  failedSteps: number
  input?: string
  output?: string
  stepResults?: string
  screenshots?: string
  loopContext?: string
  createdAt: string | null
  updatedAt: string | null
  deletedAt?: string | null
  deleted?: number
  createdBy?: string
  updatedBy?: string
  deletedBy?: string
}

/**
 * 步骤结果类型定义
 */
export interface StepResult {
  resultId: string
  executionId: string
  stepId: number
  status: string
  startTime: string | null
  endTime: string | null
  errorMessage?: string
  screenshot?: string
  log?: string
  createdAt: string | null
  updatedAt: string | null
  deletedAt?: string | null
  deleted?: number
  createdBy?: string
  updatedBy?: string
  deletedBy?: string
}

/**
 * 测试报告类型定义
 */
export interface Report {
  reportId: string
  executionId: string
  testId: string
  generatedAt: string
  summary: string
  details: string
  comparisons?: string
  format: string
  filePath?: string
  createdAt: string | null
  updatedAt: string | null
  deletedAt?: string | null
  deleted?: number
  createdBy?: string
  updatedBy?: string
  deletedBy?: string
}

/**
 * 报告数据结构
 */
export interface ReportData {
  summary: {
    executionId: string
    testId: string
    status: string
    mode: string
    startTime: string
    endTime: string
    duration: string
    totalSteps: number
    passedSteps: number
    failedSteps: number
    passRate: string
  }
  details: {
    testInfo: {
      title: string
      version: string
      appUrl: string
    }
    executionInfo: {
      executionId: string
      mode: string
      status: string
    }
    stepDetails: Array<{
      stepId: number
      status: string
      startTime: string
      endTime: string
      duration: string
      errorMessage?: string
      screenshot?: string
      log?: string
    }>
  }
}

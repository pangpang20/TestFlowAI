import request from '@/utils/request'
import type { Execution, Report } from '@/types/execution'

/**
 * 执行记录 API
 */

/**
 * 创建执行记录
 */
export function createExecutionApi(data: { testId: string; mode?: string }) {
  return request<Execution>({
    url: '/api/executions',
    method: 'POST',
    data
  })
}

/**
 * 开始执行
 */
export function startExecutionApi(executionId: string) {
  return request({
    url: `/api/executions/${executionId}/start`,
    method: 'POST'
  })
}

/**
 * 执行测试流
 */
export function runTestFlowApi(executionId: string) {
  return request<Execution>({
    url: `/api/executions/${executionId}/run`,
    method: 'POST'
  })
}

/**
 * 停止执行
 */
export function stopExecutionApi(executionId: string) {
  return request({
    url: `/api/executions/${executionId}/stop`,
    method: 'POST'
  })
}

/**
 * 获取执行记录详情
 */
export function getExecutionApi(executionId: string) {
  return request<Execution>({
    url: `/api/executions/${executionId}`,
    method: 'GET'
  })
}

/**
 * 获取执行历史（按测试流）
 */
export function getExecutionHistoryApi(testId: string) {
  return request<Execution[]>({
    url: `/api/executions/test/${testId}`,
    method: 'GET'
  })
}

/**
 * 获取执行列表（分页）
 */
export function getExecutionsApi(page: number = 1, size: number = 10) {
  return request<{ list: Execution[]; total: number; page: number; size: number }>({
    url: '/api/executions',
    method: 'GET',
    params: { page, size }
  })
}

/**
 * 获取状态统计
 */
export function getStatusCountApi() {
  return request<Record<string, number>>({
    url: '/api/executions/stats/status',
    method: 'GET'
  })
}

/**
 * 获取最近的执行记录
 */
export function getRecentExecutionsApi(limit: number = 10) {
  return request<Execution[]>({
    url: '/api/executions/recent',
    method: 'GET',
    params: { limit }
  })
}

/**
 * 获取步骤结果
 */
export function getStepResultsApi(executionId: string) {
  return request<any[]>({
    url: `/api/executions/${executionId}/steps`,
    method: 'GET'
  })
}

/**
 * 删除执行记录
 */
export function deleteExecutionApi(executionId: string) {
  return request({
    url: `/api/executions/${executionId}`,
    method: 'DELETE'
  })
}

/**
 * 测试报告 API
 */

/**
 * 生成报告
 */
export function generateReportApi(executionId: string) {
  return request<Report>({
    url: `/api/reports/generate/${executionId}`,
    method: 'POST'
  })
}

/**
 * 获取报告详情
 */
export function getReportApi(reportId: string) {
  return request<Report>({
    url: `/api/reports/${reportId}`,
    method: 'GET'
  })
}

/**
 * 根据执行 ID 获取报告
 */
export function getReportByExecutionIdApi(executionId: string) {
  return request<Report>({
    url: `/api/reports/execution/${executionId}`,
    method: 'GET'
  })
}

/**
 * 获取测试流的报告列表
 */
export function getReportsByTestIdApi(testId: string) {
  return request<Report[]>({
    url: `/api/reports/test/${testId}`,
    method: 'GET'
  })
}

/**
 * 获取报告列表（分页）
 */
export function getReportsApi(page: number = 1, size: number = 10) {
  return request<{ list: Report[]; total: number; page: number; size: number }>({
    url: '/api/reports',
    method: 'GET',
    params: { page, size }
  })
}

/**
 * 获取报告 JSON 数据
 */
export function getReportDataApi(reportId: string) {
  return request<Record<string, any>>({
    url: `/api/reports/${reportId}/data`,
    method: 'GET'
  })
}

/**
 * 下载报告为 JSON 文件
 */
export function downloadReportApi(reportId: string) {
  return request({
    url: `/api/reports/${reportId}/download`,
    method: 'GET',
    responseType: 'blob'
  })
}

/**
 * 删除报告
 */
export function deleteReportApi(reportId: string) {
  return request({
    url: `/api/reports/${reportId}`,
    method: 'DELETE'
  })
}

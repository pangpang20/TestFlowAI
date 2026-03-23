import request from '@/utils/request'

/**
 * 获取仪表盘概述统计数据
 */
export function getOverviewStatsApi() {
  return request({
    url: '/api/stats/overview',
    method: 'get'
  })
}

/**
 * 获取执行状态统计
 */
export function getExecutionStatusStatsApi() {
  return request({
    url: '/api/stats/execution/status',
    method: 'get'
  })
}

/**
 * 获取最近执行记录
 */
export function getRecentExecutionsApi(limit: number = 10) {
  return request({
    url: `/api/stats/execution/recent?limit=${limit}`,
    method: 'get'
  })
}

/**
 * 获取项目统计
 */
export function getProjectStatsApi() {
  return request({
    url: '/api/stats/projects',
    method: 'get'
  })
}

/**
 * 获取测试用例统计
 */
export function getTestFlowStatsApi() {
  return request({
    url: '/api/stats/testflows',
    method: 'get'
  })
}

/**
 * 获取定时任务统计
 */
export function getScheduledTaskStatsApi() {
  return request({
    url: '/api/stats/scheduled-tasks',
    method: 'get'
  })
}

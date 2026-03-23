import request from '@/utils/request'
import type { TestFlow } from '@/types/testflow'

export interface TestFlowDto {
  testId: string
  title: string
  version: string
  appUrl: string
  steps: string
  variables: string
  tags: string
  expectedReport: string
  projectId: string
  createdAt: string
  updatedAt: string
}

// 获取测试流列表
export function getTestFlowsApi() {
  return request<TestFlowDto[]>({
    url: '/api/testflows',
    method: 'get'
  })
}

// 获取测试流详情
export function getTestFlowByIdApi(id: string) {
  return request<TestFlowDto>({
    url: `/api/testflows/${id}`,
    method: 'get'
  })
}

// 根据项目 ID 获取测试流
export function getTestFlowsByProjectApi(projectId: string) {
  return request<TestFlowDto[]>({
    url: `/api/testflows/project/${projectId}`,
    method: 'get'
  })
}

// 创建测试流
export function createTestFlowApi(data: Partial<TestFlowDto>) {
  return request({
    url: '/api/testflows',
    method: 'post',
    data
  })
}

// 更新测试流
export function updateTestFlowApi(id: string, data: Partial<TestFlowDto>) {
  return request({
    url: `/api/testflows/${id}`,
    method: 'put',
    data
  })
}

// 删除测试流
export function deleteTestFlowApi(id: string) {
  return request({
    url: `/api/testflows/${id}`,
    method: 'delete'
  })
}

// 复制测试流
export function duplicateTestFlowApi(id: string) {
  return request({
    url: `/api/testflows/${id}/duplicate`,
    method: 'post'
  })
}

// 导出测试流
export function exportTestFlowApi(id: string) {
  return request({
    url: `/api/testflows/${id}/export`,
    method: 'get'
  })
}

// 导入测试流
export function importTestFlowApi(data: Partial<TestFlowDto>) {
  return request({
    url: '/api/testflows/import',
    method: 'post',
    data
  })
}

// 搜索测试流
export function searchTestFlowsApi(keyword: string) {
  return request<TestFlowDto[]>({
    url: '/api/testflows/search',
    method: 'get',
    params: { keyword }
  })
}

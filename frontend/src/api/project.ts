import request from '@/utils/request'
import type { Project } from '@/types/project'

export interface ProjectDto {
  projectId: string
  projectName: string
  description: string
  owner: string
  status: string
  startDate: string | null
  endDate: string | null
  progress: number
  createdAt: string
  updatedAt: string
}

// 获取项目列表
export function getProjectsApi() {
  return request<ProjectDto[]>({
    url: '/api/projects',
    method: 'get'
  })
}

// 获取项目详情
export function getProjectByIdApi(id: string) {
  return request<ProjectDto>({
    url: `/api/projects/${id}`,
    method: 'get'
  })
}

// 创建项目
export function createProjectApi(data: Partial<ProjectDto>) {
  return request({
    url: '/api/projects',
    method: 'post',
    data
  })
}

// 更新项目
export function updateProjectApi(id: string, data: Partial<ProjectDto>) {
  return request({
    url: `/api/projects/${id}`,
    method: 'put',
    data
  })
}

// 删除项目
export function deleteProjectApi(id: string) {
  return request({
    url: `/api/projects/${id}`,
    method: 'delete'
  })
}

// 更新项目进度
export function updateProjectProgressApi(id: string, progress: number) {
  return request({
    url: `/api/projects/${id}/progress`,
    method: 'patch',
    data: { progress }
  })
}

// 更新项目状态
export function updateProjectStatusApi(id: string, status: string) {
  return request({
    url: `/api/projects/${id}/status`,
    method: 'patch',
    data: { status }
  })
}

// 搜索项目
export function searchProjectsApi(keyword: string) {
  return request<ProjectDto[]>({
    url: '/api/projects/search',
    method: 'get',
    params: { keyword }
  })
}

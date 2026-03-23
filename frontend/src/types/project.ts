export interface Project {
  projectId: string
  projectName: string
  description: string
  owner: string
  status: 'active' | 'completed' | 'archived'
  startDate: string | null
  endDate: string | null
  progress: number
  createdAt: string
  updatedAt: string
}

export interface ProjectForm {
  projectName: string
  description: string
  owner: string
  status: string
  progress: number
  startDate: string | null
  endDate: string | null
}

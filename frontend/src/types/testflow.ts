export interface TestFlow {
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

export interface TestFlowStep {
  id: number
  type: string
  description: string
  selector?: string
  value?: string
  expected?: string
  screenshot?: boolean
}

export interface TestFlowForm {
  title: string
  version: string
  appUrl: string
  steps: string
  variables: string
  tags: string
  expectedReport: string
  projectId: string
}

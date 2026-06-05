import type { PortalProjectDetail, PortalProjectQuery, ProjectCard } from '@/types/project'

interface ApiResult<T> {
  code: number
  data: T
  message: string
}

async function request<T>(url: string, init?: RequestInit) {
  const response = await fetch(url, init)
  const result = (await response.json()) as ApiResult<T>
  if (result.code !== 0) throw new Error(result.message || '请求失败')
  return result.data
}

/**
 * 获取授权项目卡片
 */
export function getPortalProjects(params: PortalProjectQuery) {
  const search = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value) search.set(key, String(value))
  })
  return request<ProjectCard[]>(`/api/portal/projects?${search.toString()}`)
}

/**
 * 获取项目详情
 */
export function getPortalProjectDetail(projectId: number) {
  return request<PortalProjectDetail>(`/api/portal/project-detail?id=${projectId}`)
}

/**
 * 记录门户敏感操作
 */
export function recordPortalAction(projectId: number, action: string, target: string) {
  return request<boolean>('/api/portal/action-log', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ projectId, action, target }),
  })
}

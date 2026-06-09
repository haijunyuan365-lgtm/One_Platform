import type { PortalProjectDetail, PortalProjectQuery, ProjectCard } from '@/types/project'

interface ApiResult<T> {
  code: number
  data: T
  message: string
}

async function request<T>(url: string, init?: RequestInit) {
  const response = await fetch(url, init)
  const result = (await response.json()) as ApiResult<T>
  if (result.code !== 200) throw new Error(result.message || '请求失败')
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
  return request<ProjectCard[]>(`/app/portal/projects?${search.toString()}`)
}

/**
 * 获取项目详情
 */
export function getPortalProjectDetail(projectId: number) {
  return request<PortalProjectDetail>(`/app/portal/projects/${projectId}`)
}

/**
 * 记录门户敏感操作
 */
export function recordPortalAction(projectId: number, action: string, target: string) {
  return request<boolean>('/app/portal/action-log', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ projectId, action, target }),
  })
}

/**
 * 查看门户凭据密码
 */
export function revealPortalPassword(credentialId: number) {
  return request<string>(`/app/portal/credentials/${credentialId}/reveal`, {
    method: 'POST',
  })
}

/**
 * 复制门户凭据账号或密码
 */
export function copyPortalCredential(credentialId: number, field: 'username' | 'password') {
  return request<string>(`/app/portal/credentials/${credentialId}/copy`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ field }),
  })
}

import request from '@/utils/http'
import type {
  OperationLog,
  OperationLogQueryParams,
  PortalProjectQueryParams,
  ProjectOption,
  ProjectAddress,
  ProjectCard,
  ProjectCredential,
  ProjectInstruction,
  ProjectPermission,
  ProjectQrcode,
  ProjectQueryParams,
  StatusConfig,
  StatusRecord,
  StatusRecordQueryParams
} from '@/types/project'
import {
  addProjectMock,
  copyCredentialMock,
  deleteProjectCredentialMock,
  deleteProjectMock,
  deleteProjectQrcodeMock,
  getPortalProjectDetailMock,
  getPortalProjectsMock,
  getProjectCredentialsMock,
  getProjectDetailContentMock,
  getProjectListMock,
  getProjectOptionsMock,
  getProjectQrcodesMock,
  revealProjectPasswordMock,
  saveProjectAddressesMock,
  saveProjectCredentialMock,
  saveProjectInstructionMock,
  saveProjectQrcodeMock,
  updateProjectCredentialStatusMock,
  updateProjectEnabledMock,
  updateProjectMock,
  updateProjectQrcodeStatusMock
} from '@/mock/project'
import {
  getStatusConfigListMock,
  getStatusRecordListMock,
  runStatusCheckMock,
  setManualStatusMock,
  updateStatusConfigMock
} from '@/mock/status'
import {
  deleteProjectPermissionMock,
  exportOperationLogMock,
  getOperationLogListMock,
  getProjectPermissionListMock,
  saveProjectPermissionMock
} from '@/mock/audit'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

type PageResult<T> = { list: T[]; total: number }

function mockResponse<T>(executor: () => T, message = 'success') {
  return new Promise<{ code: number; message: string; data: T }>((resolve, reject) => {
    setTimeout(() => {
      try {
        resolve({ code: 200, message, data: executor() })
      } catch (error: any) {
        reject(new Error(error.message || '操作失败'))
      }
    }, 300)
  })
}

/**
 * 获取项目下拉选项
 */
export function getProjectOptions() {
  if (USE_MOCK) return mockResponse(() => getProjectOptionsMock(), '获取项目选项成功')
  return request.get<ProjectOption[]>({ url: '/admin/project/options' })
}

/**
 * 分页查询项目卡片
 */
export function getProjectList(params: ProjectQueryParams) {
  if (USE_MOCK) return mockResponse(() => getProjectListMock(params), '获取项目列表成功')
  return request.get<PageResult<ProjectCard>>({ url: '/admin/project/list', params })
}

/**
 * 新增项目卡片
 */
export function addProject(data: Partial<ProjectCard>) {
  if (USE_MOCK) return mockResponse(() => addProjectMock(data), '新增项目成功')
  return request.post<ProjectCard>({ url: '/admin/project', data })
}

/**
 * 更新项目卡片
 */
export function updateProject(id: number, data: Partial<ProjectCard>) {
  if (USE_MOCK) return mockResponse(() => updateProjectMock(id, data), '更新项目成功')
  return request.put<ProjectCard>({ url: `/admin/project/${id}`, data })
}

/**
 * 删除项目卡片
 */
export function deleteProject(id: number) {
  if (USE_MOCK) return mockResponse(() => deleteProjectMock(id), '删除项目成功')
  return request.del<boolean>({ url: `/admin/project/${id}` })
}

/**
 * 更新项目启用状态
 */
export function updateProjectEnabled(id: number, enabled: 0 | 1) {
  if (USE_MOCK) return mockResponse(() => updateProjectEnabledMock(id, enabled), '更新启用状态成功')
  return request.put<boolean>({ url: `/admin/project/${id}/enabled`, data: { enabled } })
}

/**
 * 查询项目详情内容
 */
export function getProjectDetailContent(projectId: number) {
  if (USE_MOCK) return mockResponse(() => getProjectDetailContentMock(projectId), '获取详情内容成功')
  return request.get<ReturnType<typeof getProjectDetailContentMock>>({ url: `/admin/project/${projectId}/detail-content` })
}

/**
 * 保存项目访问地址
 */
export function saveProjectAddresses(projectId: number, list: ProjectAddress[]) {
  if (USE_MOCK) return mockResponse(() => saveProjectAddressesMock(projectId, list), '保存访问地址成功')
  return request.put<ProjectAddress[]>({ url: `/admin/project/${projectId}/addresses`, data: { list } })
}

/**
 * 保存项目访问说明
 */
export function saveProjectInstruction(projectId: number, data: ProjectInstruction) {
  if (USE_MOCK) return mockResponse(() => saveProjectInstructionMock(projectId, data), '保存访问说明成功')
  return request.put<ProjectInstruction>({ url: `/admin/project/${projectId}/instruction`, data })
}

/**
 * 查询项目账号凭据
 */
export function getProjectCredentials(params: { projectId?: number | ''; environment?: string; status?: string | number }) {
  if (USE_MOCK) return mockResponse(() => getProjectCredentialsMock(params), '获取凭据成功')
  return request.get<Array<ProjectCredential & { projectName?: string }>>({ url: '/admin/project/credential/list', params })
}

/**
 * 保存项目账号凭据
 */
export function saveProjectCredential(data: Partial<ProjectCredential> & { password?: string }) {
  if (USE_MOCK) return mockResponse(() => saveProjectCredentialMock(data), '保存凭据成功')
  return request.post<ProjectCredential>({ url: '/admin/project/credential', data })
}

/**
 * 删除项目账号凭据
 */
export function deleteProjectCredential(id: number) {
  if (USE_MOCK) return mockResponse(() => deleteProjectCredentialMock(id), '删除凭据成功')
  return request.del<boolean>({ url: `/admin/project/credential/${id}` })
}

/**
 * 更新项目账号凭据状态
 */
export function updateProjectCredentialStatus(id: number, status: 0 | 1) {
  if (USE_MOCK) return mockResponse(() => updateProjectCredentialStatusMock(id, status), '更新凭据状态成功')
  return request.put<boolean>({ url: `/admin/project/credential/${id}/status`, data: { status } })
}

/**
 * 查看项目账号密码
 */
export function revealProjectPassword(id: number) {
  if (USE_MOCK) return mockResponse(() => revealProjectPasswordMock(id), '查看密码成功')
  return request.post<string>({ url: `/admin/project/credential/${id}/reveal` })
}

/**
 * 复制项目账号或密码
 */
export function copyCredential(id: number, field: 'username' | 'password') {
  if (USE_MOCK) return mockResponse(() => copyCredentialMock(id, field), '复制成功')
  return request.post<string>({ url: `/admin/project/credential/${id}/copy`, data: { field } })
}

/**
 * 查询项目二维码
 */
export function getProjectQrcodes(params: { projectId?: number | ''; status?: string | number }) {
  if (USE_MOCK) return mockResponse(() => getProjectQrcodesMock(params), '获取二维码成功')
  return request.get<Array<ProjectQrcode & { projectName?: string }>>({ url: '/admin/project/qrcode/list', params })
}

/**
 * 保存项目二维码
 */
export function saveProjectQrcode(data: Partial<ProjectQrcode>) {
  if (USE_MOCK) return mockResponse(() => saveProjectQrcodeMock(data), '保存二维码成功')
  return request.post<ProjectQrcode>({ url: '/admin/project/qrcode', data })
}

/**
 * 删除项目二维码
 */
export function deleteProjectQrcode(id: number) {
  if (USE_MOCK) return mockResponse(() => deleteProjectQrcodeMock(id), '删除二维码成功')
  return request.del<boolean>({ url: `/admin/project/qrcode/${id}` })
}

/**
 * 更新项目二维码状态
 */
export function updateProjectQrcodeStatus(id: number, status: 0 | 1) {
  if (USE_MOCK) return mockResponse(() => updateProjectQrcodeStatusMock(id, status), '更新二维码状态成功')
  return request.put<boolean>({ url: `/admin/project/qrcode/${id}/status`, data: { status } })
}

/**
 * 查询状态检测配置
 */
export function getStatusConfigList(params: { projectId?: number | ''; status?: string }) {
  if (USE_MOCK) return mockResponse(() => getStatusConfigListMock(params), '获取检测配置成功')
  return request.get<StatusConfig[]>({ url: '/admin/status/config/list', params })
}

/**
 * 更新状态检测配置
 */
export function updateStatusConfig(id: number, data: Partial<StatusConfig>) {
  if (USE_MOCK) return mockResponse(() => updateStatusConfigMock(id, data), '更新检测配置成功')
  return request.put<StatusConfig>({ url: `/admin/status/config/${id}`, data })
}

/**
 * 立即触发一次状态检测
 */
export function runStatusCheck(id: number) {
  if (USE_MOCK) return mockResponse(() => runStatusCheckMock(id), '立即检测成功')
  return request.post<StatusRecord>({ url: `/admin/status/config/${id}/run` })
}

/**
 * 设置人工覆盖状态
 */
export function setManualStatus(id: number, manualStatus: StatusConfig['manualStatus'], reason: string) {
  if (USE_MOCK) return mockResponse(() => setManualStatusMock(id, manualStatus, reason), '人工状态保存成功')
  return request.put<StatusConfig>({ url: `/admin/status/config/${id}/manual-status`, data: { manualStatus, reason } })
}

/**
 * 查询状态检测记录
 */
export function getStatusRecordList(params: StatusRecordQueryParams) {
  if (USE_MOCK) return mockResponse(() => getStatusRecordListMock(params), '获取检测记录成功')
  return request.get<PageResult<StatusRecord>>({ url: '/admin/status/record/list', params })
}

/**
 * 查询项目授权配置
 */
export function getProjectPermissionList(projectId?: number | '') {
  if (USE_MOCK) return mockResponse(() => getProjectPermissionListMock(projectId), '获取项目授权成功')
  return request.get<Array<ProjectPermission & { projectName?: string }>>({ url: '/admin/audit/project-permission/list', params: { projectId } })
}

/**
 * 保存项目授权配置
 */
export function saveProjectPermission(data: Partial<ProjectPermission>) {
  if (USE_MOCK) return mockResponse(() => saveProjectPermissionMock(data), '保存授权成功')
  return request.post<ProjectPermission>({ url: '/admin/audit/project-permission', data })
}

/**
 * 删除项目授权配置
 */
export function deleteProjectPermission(id: number) {
  if (USE_MOCK) return mockResponse(() => deleteProjectPermissionMock(id), '删除授权成功')
  return request.del<boolean>({ url: `/admin/audit/project-permission/${id}` })
}

/**
 * 查询操作日志
 */
export function getOperationLogList(params: OperationLogQueryParams) {
  if (USE_MOCK) return mockResponse(() => getOperationLogListMock(params), '获取操作日志成功')
  return request.get<PageResult<OperationLog>>({ url: '/admin/audit/operation-log/list', params })
}

/**
 * 导出操作日志
 */
export function exportOperationLog(params: OperationLogQueryParams) {
  if (USE_MOCK) return mockResponse(() => exportOperationLogMock(params), '导出操作日志成功')
  return request.get<OperationLog[]>({ url: '/admin/audit/operation-log/export', params })
}

/**
 * 查询前台门户项目卡片
 */
export function getPortalProjects(params: PortalProjectQueryParams & { favoriteIds?: number[] }) {
  if (USE_MOCK) return mockResponse(() => getPortalProjectsMock(params), '获取门户项目成功')
  return request.get<ProjectCard[]>({ url: '/app/portal/projects', params })
}

/**
 * 查询前台门户项目详情
 */
export function getPortalProjectDetail(projectId: number, canViewPassword = true) {
  if (USE_MOCK) return mockResponse(() => getPortalProjectDetailMock(projectId, canViewPassword), '获取项目详情成功')
  return request.get<ReturnType<typeof getPortalProjectDetailMock>>({ url: `/app/portal/projects/${projectId}` })
}

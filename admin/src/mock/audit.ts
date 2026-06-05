import type { OperationLogQueryParams, ProjectPermission } from '@/types/project'
import { idSeed, nowText, operationLogs, permissions, projects } from './projectData'

function paginate<T>(list: T[], page = 1, pageSize = 20) {
  const start = (Number(page) - 1) * Number(pageSize)
  return { list: list.slice(start, start + Number(pageSize)), total: list.length }
}

function addLog(projectName: string, remark: string) {
  operationLogs.unshift({
    id: idSeed.log++,
    operateTime: nowText(),
    operator: '管理员',
    account: 'admin',
    department: '总公司',
    projectName,
    operationType: '修改权限',
    target: '项目授权',
    result: '成功',
    ip: '10.10.1.21',
    device: 'Chrome / Windows',
    remark
  })
}

export function getProjectPermissionListMock(projectId?: number | '') {
  const list = projectId ? permissions.filter((item) => item.projectId === Number(projectId)) : permissions
  return list.map((item) => ({
    ...item,
    projectName: projects.find((project) => project.id === item.projectId)?.name || '-'
  }))
}

export function saveProjectPermissionMock(data: Partial<ProjectPermission>) {
  if (!data.projectId) throw new Error('请选择项目')
  if (!data.targetType || !data.targetId || !data.targetName) throw new Error('请选择授权对象')
  const projectName = projects.find((item) => item.id === data.projectId)?.name || '-'
  if (data.id) {
    const index = permissions.findIndex((item) => item.id === data.id)
    if (index < 0) throw new Error('授权记录不存在')
    permissions[index] = { ...permissions[index], ...data, updateTime: nowText() }
    addLog(projectName, `更新${data.targetType}${data.targetName}授权`)
    return permissions[index]
  }
  const permission: ProjectPermission = {
    id: idSeed.permission++,
    projectId: data.projectId,
    targetType: data.targetType,
    targetId: data.targetId,
    targetName: data.targetName,
    visible: Boolean(data.visible),
    passwordView: Boolean(data.passwordView),
    passwordCopy: Boolean(data.passwordCopy),
    qrcodeView: Boolean(data.qrcodeView),
    status: data.status ?? 1,
    updateTime: nowText()
  }
  permissions.push(permission)
  addLog(projectName, `新增${permission.targetType}${permission.targetName}授权`)
  return permission
}

export function deleteProjectPermissionMock(id: number) {
  const index = permissions.findIndex((item) => item.id === id)
  if (index < 0) throw new Error('授权记录不存在')
  const [permission] = permissions.splice(index, 1)
  const projectName = projects.find((item) => item.id === permission.projectId)?.name || '-'
  addLog(projectName, `删除${permission.targetType}${permission.targetName}授权`)
  return true
}

export function getOperationLogListMock(params: OperationLogQueryParams) {
  let list = [...operationLogs]
  if (params.operator) {
    list = list.filter((item) => item.operator.includes(params.operator!) || item.account.includes(params.operator!))
  }
  if (params.projectName) list = list.filter((item) => item.projectName.includes(params.projectName!))
  if (params.operationType) list = list.filter((item) => item.operationType === params.operationType)
  if (params.result) list = list.filter((item) => item.result === params.result)
  if (params.dateRange?.length === 2) {
    const [start, end] = params.dateRange
    list = list.filter((item) => item.operateTime >= start && item.operateTime <= `${end} 23:59:59`)
  }
  return paginate(list, params.page, params.pageSize)
}

export function exportOperationLogMock(params: OperationLogQueryParams) {
  const { list } = getOperationLogListMock({ ...params, page: 1, pageSize: 1000 })
  if (list.length > 500) throw new Error('导出数据量过大，请缩小筛选范围')
  return list.map(({ id, operateTime, operator, account, department, projectName, operationType, target, result, ip, device, remark }) => ({
    id,
    operateTime,
    operator,
    account,
    department,
    projectName,
    operationType,
    target,
    result,
    ip,
    device,
    remark
  }))
}

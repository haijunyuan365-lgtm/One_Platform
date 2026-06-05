import type { StatusConfig, StatusRecordQueryParams } from '@/types/project'
import { idSeed, nowText, operationLogs, projects, statusConfigs, statusRecords } from './projectData'

function paginate<T>(list: T[], page = 1, pageSize = 20) {
  const start = (Number(page) - 1) * Number(pageSize)
  return { list: list.slice(start, start + Number(pageSize)), total: list.length }
}

function addLog(projectName: string, operationType: string, target: string, remark: string) {
  operationLogs.unshift({
    id: idSeed.log++,
    operateTime: nowText(),
    operator: '管理员',
    account: 'admin',
    department: '总公司',
    projectName,
    operationType,
    target,
    result: '成功',
    ip: '10.10.1.21',
    device: 'Chrome / Windows',
    remark
  })
}

function syncProjectStatus(config: StatusConfig) {
  const project = projects.find((item) => item.id === config.projectId)
  if (!project) return
  project.status = config.currentStatus
  project.lastCheckTime = config.lastCheckTime
  project.responseTime = config.responseTime
  project.abnormalReason = config.abnormalReason
  project.updateTime = nowText()
}

export function getStatusConfigListMock(params: { projectId?: number | ''; status?: string }) {
  let list = [...statusConfigs]
  if (params.projectId) list = list.filter((item) => item.projectId === Number(params.projectId))
  if (params.status) list = list.filter((item) => item.currentStatus === params.status)
  return list
}

export function updateStatusConfigMock(id: number, data: Partial<StatusConfig>) {
  const index = statusConfigs.findIndex((item) => item.id === id)
  if (index < 0) throw new Error('检测配置不存在')
  const next = { ...statusConfigs[index], ...data }
  if (next.autoEnabled === 1 && !next.checkAddress) throw new Error('请配置检测地址')
  statusConfigs[index] = next
  addLog(next.projectName, '后台修改', '检测配置', '更新检测配置')
  return next
}

export function runStatusCheckMock(id: number) {
  const config = statusConfigs.find((item) => item.id === id)
  if (!config) throw new Error('检测配置不存在')
  const failed = config.checkAddress.includes('bi.example.test')
  const result = failed ? '异常' : '可用'
  const time = nowText()
  const record = {
    id: idSeed.statusRecord++,
    projectId: config.projectId,
    projectName: config.projectName,
    method: config.method,
    checkAddress: config.checkAddress || '手动维护',
    result: result as any,
    responseTime: failed ? 0 : 120 + (config.projectId % 5) * 36,
    errorReason: failed ? 'HTTP 502' : '',
    checkTime: time,
    operator: '管理员'
  }
  statusRecords.unshift(record)
  config.lastCheckTime = time
  config.responseTime = record.responseTime
  config.abnormalReason = record.errorReason
  if (config.manualStatus === '无覆盖') config.currentStatus = result as any
  syncProjectStatus(config)
  addLog(config.projectName, '立即检测', '检测配置', `${config.method} 检测${result}`)
  return record
}

export function setManualStatusMock(id: number, manualStatus: StatusConfig['manualStatus'], reason: string) {
  const config = statusConfigs.find((item) => item.id === id)
  if (!config) throw new Error('检测配置不存在')
  if (manualStatus !== '无覆盖' && !reason) throw new Error('请填写覆盖原因')
  config.manualStatus = manualStatus
  config.manualReason = reason
  config.currentStatus = manualStatus === '无覆盖' ? config.currentStatus : manualStatus
  config.lastCheckTime = nowText()
  config.abnormalReason = reason
  syncProjectStatus(config)
  addLog(config.projectName, '修改检测状态', '人工状态', `设置为${manualStatus}`)
  return config
}

export function getStatusRecordListMock(params: StatusRecordQueryParams) {
  let list = [...statusRecords]
  if (params.projectId) list = list.filter((item) => item.projectId === Number(params.projectId))
  if (params.result) list = list.filter((item) => item.result === params.result)
  if (params.dateRange?.length === 2) {
    const [start, end] = params.dateRange
    list = list.filter((item) => item.checkTime >= start && item.checkTime <= `${end} 23:59:59`)
  }
  return paginate(list, params.page, params.pageSize)
}

export type ProjectCategory = '内部系统' | '客户项目' | 'AI工具' | '数据平台' | '运维服务' | '小程序'
export type ProjectStatus = '可用' | '异常' | '维护中' | '未检测'
export type EnabledStatus = 1 | 0
export type AddressType = 'Web' | '文档' | '后台' | '其他'
export type CredentialEnv = '正式' | '测试' | '演示' | '其他'
export type CheckMethod = 'HTTP/HTTPS' | 'TCP' | 'ping' | '手动'
export type ManualStatus = ProjectStatus | '无覆盖'
export type OperationResult = '成功' | '失败'
export type PermissionTargetType = '部门' | '角色' | '用户'

export interface ProjectOption {
  id: number
  name: string
  shortName: string
}

export interface ProjectCard {
  id: number
  name: string
  shortName: string
  logo: string
  category: ProjectCategory
  tags: string[]
  description: string
  maintainerId?: number
  maintainer: string
  sort: number
  enabled: EnabledStatus
  status: ProjectStatus
  lastCheckTime?: string
  responseTime?: number
  abnormalReason?: string
  createTime: string
  updateTime: string
}

export interface ProjectQueryParams {
  keyword?: string
  category?: ProjectCategory | ''
  maintainer?: string
  enabled?: EnabledStatus | '' | string
  status?: ProjectStatus | ''
  page: number
  pageSize: number
}

export interface ProjectAddress {
  id: number
  projectId: number
  name: string
  type: AddressType
  url: string
  isDefault: EnabledStatus
  isDetection: EnabledStatus
  sort: number
  status: EnabledStatus
}

export interface ProjectInstruction {
  projectId: number
  browserRequirement: string
  vpnRequirement: string
  notes: string
  maintainer: string
  contactPhone: string
  updateTime: string
}

export interface ProjectCredential {
  id: number
  projectId: number
  name: string
  username: string
  passwordMasked: string
  environment: CredentialEnv
  description: string
  expireDate?: string
  status: EnabledStatus
  updateTime: string
}

export interface ProjectQrcode {
  id: number
  projectId: number
  name: string
  image: string
  audience: string
  description: string
  status: EnabledStatus
  updateTime: string
}

export interface StatusConfig {
  id: number
  projectId: number
  projectName: string
  autoEnabled: EnabledStatus
  method: CheckMethod
  checkAddress: string
  checkPort?: number
  timeoutSeconds: number
  frequencyMinutes: number
  expectedStatusCodes: string
  manualStatus: ManualStatus
  manualReason: string
  currentStatus: ProjectStatus
  lastCheckTime?: string
  responseTime?: number
  abnormalReason?: string
}

export interface StatusRecord {
  id: number
  projectId: number
  projectName: string
  method: CheckMethod
  checkAddress: string
  result: ProjectStatus
  responseTime: number
  errorReason: string
  checkTime: string
  operator: string
}

export interface StatusRecordQueryParams {
  projectId?: number | ''
  result?: ProjectStatus | ''
  dateRange?: [string, string] | []
  page: number
  pageSize: number
}

export interface ProjectPermission {
  id: number
  projectId: number
  targetType: PermissionTargetType
  targetId: number
  targetName: string
  visible: boolean
  passwordView: boolean
  passwordCopy: boolean
  qrcodeView: boolean
  status: EnabledStatus
  updateTime: string
}

export interface OperationLog {
  id: number
  operateTime: string
  operator: string
  account: string
  department: string
  projectName: string
  operationType: string
  target: string
  result: OperationResult
  ip: string
  device: string
  remark: string
}

export interface OperationLogQueryParams {
  operator?: string
  projectName?: string
  operationType?: string
  result?: OperationResult | ''
  dateRange?: [string, string] | []
  page: number
  pageSize: number
}

export interface PortalProjectQueryParams {
  keyword?: string
  category?: ProjectCategory | ''
  status?: ProjectStatus | ''
  favoriteOnly?: boolean
}

export interface PortalProjectDetail {
  project: ProjectCard
  addresses: ProjectAddress[]
  credentials: ProjectCredential[]
  qrcodes: ProjectQrcode[]
  instruction: ProjectInstruction
  canViewPassword: boolean
  canCopyPassword: boolean
  canViewQrcode: boolean
}

export interface SelectOption<T = string | number> {
  label: string
  value: T
}

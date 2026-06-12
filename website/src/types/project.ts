export type ProjectCategory =
  | '内部系统'
  | '客户项目'
  | 'AI工具'
  | '数据平台'
  | '运维服务'
  | '小程序'

export type ProjectStatus = '可用' | '异常' | '维护中' | '未检查'

export type CredentialEnv = '正式' | '测试' | '演示' | '其他'

export interface ProjectCard {
  id: number
  name: string
  shortName: string
  logo: string
  category: ProjectCategory
  tags: string[]
  description: string
  maintainer: string
  status: ProjectStatus
  lastCheckTime?: string
  responseTime?: number
  abnormalReason?: string
}

export interface ProjectAddress {
  id: number
  name: string
  type: string
  url: string
  isDefault: 0 | 1
}

export interface ProjectCredential {
  id: number
  addressId?: number
  name: string
  username: string
  password?: string
  passwordMasked: string
  environment: CredentialEnv
  description: string
}

export interface ProjectQrcode {
  id: number
  name: string
  image: string
  audience: string
  description: string
}

export interface ProjectInstruction {
  browserRequirement: string
  vpnRequirement: string
  notes: string
  maintainer: string
  contactPhone: string
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

export interface PortalProjectQuery {
  keyword?: string
  category?: ProjectCategory | ''
  status?: ProjectStatus | ''
}

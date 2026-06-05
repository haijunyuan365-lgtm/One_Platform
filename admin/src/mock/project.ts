import type {
  PortalProjectQueryParams,
  ProjectAddress,
  ProjectCard,
  ProjectCredential,
  ProjectInstruction,
  ProjectQrcode,
  ProjectQueryParams
} from '@/types/project'
import {
  addresses,
  buildAsset,
  credentialSecrets,
  credentials,
  idSeed,
  instructions,
  nowText,
  operationLogs,
  projects,
  qrcodes,
  statusConfigs,
  statusRecords
} from './projectData'

function paginate<T>(list: T[], page = 1, pageSize = 20) {
  const start = (Number(page) - 1) * Number(pageSize)
  return { list: list.slice(start, start + Number(pageSize)), total: list.length }
}

function toNumber(value: unknown) {
  return typeof value === 'string' ? Number(value) : value
}

function getProjectName(projectId: number) {
  return projects.find((project) => project.id === projectId)?.name || '-'
}

function touchProject(projectId: number) {
  const project = projects.find((item) => item.id === projectId)
  if (project) project.updateTime = nowText()
}

function addLog(projectName: string, operationType: string, target: string, remark: string, result = '成功') {
  operationLogs.unshift({
    id: idSeed.log++,
    operateTime: nowText(),
    operator: '管理员',
    account: 'admin',
    department: '总公司',
    projectName,
    operationType,
    target,
    result: result as any,
    ip: '10.10.1.21',
    device: 'Chrome / Windows',
    remark
  })
}

export function getProjectOptionsMock() {
  return projects.map((project) => ({ id: project.id, name: project.name, shortName: project.shortName }))
}

export function getProjectListMock(params: ProjectQueryParams) {
  const { keyword, category, maintainer, enabled, status, page, pageSize } = params
  let filtered = [...projects].sort((a, b) => a.sort - b.sort)

  if (keyword) {
    filtered = filtered.filter((item) =>
      [item.name, item.shortName, item.description, item.tags.join(',')].some((text) =>
        text.includes(keyword)
      )
    )
  }
  if (category) filtered = filtered.filter((item) => item.category === category)
  if (maintainer) filtered = filtered.filter((item) => item.maintainer.includes(maintainer))
  if (enabled !== '' && enabled !== undefined && enabled !== null) {
    filtered = filtered.filter((item) => item.enabled === toNumber(enabled))
  }
  if (status) filtered = filtered.filter((item) => item.status === status)

  return paginate(filtered, page, pageSize)
}

export function addProjectMock(data: Partial<ProjectCard>) {
  if (projects.some((item) => item.name === data.name)) throw new Error('项目名称已存在，请更换')
  const time = nowText()
  const project: ProjectCard = {
    id: idSeed.project++,
    name: data.name || '',
    shortName: data.shortName || '',
    logo: data.logo || buildAsset((data.shortName || data.name || '项').slice(0, 2), '#2563eb'),
    category: data.category || '内部系统',
    tags: data.tags || [],
    description: data.description || '',
    maintainerId: data.maintainerId,
    maintainer: data.maintainer || '',
    sort: Number(data.sort || 100),
    enabled: data.enabled ?? 1,
    status: data.status || '未检测',
    createTime: time,
    updateTime: time
  }
  projects.push(project)
  instructions.push({
    projectId: project.id,
    browserRequirement: '',
    vpnRequirement: '',
    notes: '',
    maintainer: project.maintainer,
    contactPhone: '',
    updateTime: time
  })
  statusConfigs.push({
    id: idSeed.statusConfig++,
    projectId: project.id,
    projectName: project.name,
    autoEnabled: 0,
    method: '手动',
    checkAddress: '',
    timeoutSeconds: 5,
    frequencyMinutes: 5,
    expectedStatusCodes: '200,302,401,403',
    manualStatus: '无覆盖',
    manualReason: '',
    currentStatus: '未检测'
  })
  addLog(project.name, '后台修改', '项目卡片', '新增项目卡片')
  return project
}

export function updateProjectMock(id: number, data: Partial<ProjectCard>) {
  const index = projects.findIndex((item) => item.id === id)
  if (index < 0) throw new Error('项目不存在')
  if (data.name && projects.some((item) => item.name === data.name && item.id !== id)) {
    throw new Error('项目名称已存在，请更换')
  }
  projects[index] = { ...projects[index], ...data, updateTime: nowText() }
  const config = statusConfigs.find((item) => item.projectId === id)
  if (config) config.projectName = projects[index].name
  addLog(projects[index].name, '后台修改', '项目卡片', '编辑项目卡片')
  return projects[index]
}

export function deleteProjectMock(id: number) {
  const project = projects.find((item) => item.id === id)
  if (!project) throw new Error('项目不存在')
  const related =
    addresses.some((item) => item.projectId === id) ||
    credentials.some((item) => item.projectId === id) ||
    qrcodes.some((item) => item.projectId === id) ||
    statusRecords.some((item) => item.projectId === id)
  if (related) throw new Error('该项目存在关联数据，不可删除')
  projects.splice(projects.indexOf(project), 1)
  addLog(project.name, '后台修改', '项目卡片', '删除项目卡片')
  return true
}

export function updateProjectEnabledMock(id: number, enabled: 0 | 1) {
  const project = projects.find((item) => item.id === id)
  if (!project) throw new Error('项目不存在')
  project.enabled = enabled
  project.updateTime = nowText()
  addLog(project.name, '后台修改', '项目卡片', enabled ? '启用项目' : '停用项目')
  return true
}

export function getProjectDetailContentMock(projectId: number) {
  return {
    project: projects.find((item) => item.id === projectId),
    addresses: addresses
      .filter((item) => item.projectId === projectId)
      .sort((a, b) => a.sort - b.sort),
    instruction: instructions.find((item) => item.projectId === projectId)
  }
}

export function saveProjectAddressesMock(projectId: number, list: ProjectAddress[]) {
  for (let index = addresses.length - 1; index >= 0; index--) {
    if (addresses[index].projectId === projectId) addresses.splice(index, 1)
  }
  const normalized = list.map((item) => ({
    ...item,
    id: item.id || idSeed.address++,
    projectId,
    isDefault: (item.isDefault ? 1 : 0) as 0 | 1,
    isDetection: (item.isDetection ? 1 : 0) as 0 | 1,
    status: (item.status ? 1 : 0) as 0 | 1
  }))
  const defaultItems = normalized.filter((item) => item.isDefault === 1)
  if (defaultItems.length > 1) throw new Error('每个项目最多一个默认打开地址')
  addresses.push(...normalized)
  touchProject(projectId)
  addLog(getProjectName(projectId), '后台修改', '访问地址', '保存访问地址')
  return normalized
}

export function saveProjectInstructionMock(projectId: number, data: ProjectInstruction) {
  const index = instructions.findIndex((item) => item.projectId === projectId)
  const next = { ...data, projectId, updateTime: nowText() }
  if (index >= 0) instructions[index] = next
  else instructions.push(next)
  touchProject(projectId)
  addLog(getProjectName(projectId), '后台修改', '访问说明', '保存访问说明')
  return next
}

export function getProjectCredentialsMock(params: { projectId?: number | ''; environment?: string; status?: string | number }) {
  let list = [...credentials]
  if (params.projectId) list = list.filter((item) => item.projectId === Number(params.projectId))
  if (params.environment) list = list.filter((item) => item.environment === params.environment)
  if (params.status !== '' && params.status !== undefined) {
    list = list.filter((item) => item.status === toNumber(params.status))
  }
  return list.map((item) => ({ ...item, projectName: getProjectName(item.projectId) }))
}

export function saveProjectCredentialMock(data: Partial<ProjectCredential> & { password?: string }) {
  if (!data.projectId) throw new Error('请选择项目')
  const time = nowText()
  if (data.id) {
    const index = credentials.findIndex((item) => item.id === data.id)
    if (index < 0) throw new Error('凭据不存在')
    credentials[index] = { ...credentials[index], ...data, passwordMasked: '************', updateTime: time }
    if (data.password) credentialSecrets[data.id] = data.password
    addLog(getProjectName(credentials[index].projectId), '后台修改', '账号凭据', '编辑账号凭据')
    return credentials[index]
  }
  const id = idSeed.credential++
  credentialSecrets[id] = data.password || 'DemoPassword2026'
  const credential: ProjectCredential = {
    id,
    projectId: data.projectId,
    name: data.name || '',
    username: data.username || '',
    passwordMasked: '************',
    environment: data.environment || '测试',
    description: data.description || '',
    expireDate: data.expireDate,
    status: data.status ?? 1,
    updateTime: time
  }
  credentials.push(credential)
  addLog(getProjectName(credential.projectId), '后台修改', '账号凭据', '新增账号凭据')
  return credential
}

export function deleteProjectCredentialMock(id: number) {
  const index = credentials.findIndex((item) => item.id === id)
  if (index < 0) throw new Error('凭据不存在')
  const [credential] = credentials.splice(index, 1)
  delete credentialSecrets[id]
  addLog(getProjectName(credential.projectId), '后台修改', '账号凭据', '删除账号凭据')
  return true
}

export function updateProjectCredentialStatusMock(id: number, status: 0 | 1) {
  const credential = credentials.find((item) => item.id === id)
  if (!credential) throw new Error('凭据不存在')
  credential.status = status
  credential.updateTime = nowText()
  addLog(getProjectName(credential.projectId), '后台修改', '账号凭据', status ? '启用凭据' : '停用凭据')
  return true
}

export function revealProjectPasswordMock(id: number) {
  const credential = credentials.find((item) => item.id === id)
  if (!credential) throw new Error('凭据不存在')
  addLog(getProjectName(credential.projectId), '查看密码', credential.name, '权限校验通过')
  return credentialSecrets[id] || ''
}

export function copyCredentialMock(id: number, field: 'username' | 'password') {
  const credential = credentials.find((item) => item.id === id)
  if (!credential) throw new Error('凭据不存在')
  addLog(getProjectName(credential.projectId), field === 'password' ? '复制密码' : '复制账号', credential.name, '复制成功')
  return field === 'password' ? credentialSecrets[id] || '' : credential.username
}

export function getProjectQrcodesMock(params: { projectId?: number | ''; status?: string | number }) {
  let list = [...qrcodes]
  if (params.projectId) list = list.filter((item) => item.projectId === Number(params.projectId))
  if (params.status !== '' && params.status !== undefined) {
    list = list.filter((item) => item.status === toNumber(params.status))
  }
  return list.map((item) => ({ ...item, projectName: getProjectName(item.projectId) }))
}

export function saveProjectQrcodeMock(data: Partial<ProjectQrcode>) {
  if (!data.projectId) throw new Error('请选择项目')
  const time = nowText()
  if (data.id) {
    const index = qrcodes.findIndex((item) => item.id === data.id)
    if (index < 0) throw new Error('二维码不存在')
    qrcodes[index] = { ...qrcodes[index], ...data, updateTime: time }
    addLog(getProjectName(qrcodes[index].projectId), '后台修改', '二维码', '编辑二维码')
    return qrcodes[index]
  }
  const qrcode: ProjectQrcode = {
    id: idSeed.qrcode++,
    projectId: data.projectId,
    name: data.name || '',
    image: data.image || buildAsset('QR', '#2563eb'),
    audience: data.audience || '',
    description: data.description || '',
    status: data.status ?? 1,
    updateTime: time
  }
  qrcodes.push(qrcode)
  addLog(getProjectName(qrcode.projectId), '后台修改', '二维码', '新增二维码')
  return qrcode
}

export function deleteProjectQrcodeMock(id: number) {
  const index = qrcodes.findIndex((item) => item.id === id)
  if (index < 0) throw new Error('二维码不存在')
  const [qrcode] = qrcodes.splice(index, 1)
  addLog(getProjectName(qrcode.projectId), '后台修改', '二维码', '删除二维码')
  return true
}

export function updateProjectQrcodeStatusMock(id: number, status: 0 | 1) {
  const qrcode = qrcodes.find((item) => item.id === id)
  if (!qrcode) throw new Error('二维码不存在')
  qrcode.status = status
  qrcode.updateTime = nowText()
  addLog(getProjectName(qrcode.projectId), '后台修改', '二维码', status ? '启用二维码' : '停用二维码')
  return true
}

export function getPortalProjectsMock(params: PortalProjectQueryParams & { favoriteIds?: number[] }) {
  let list = projects.filter((item) => item.enabled === 1)
  if (params.keyword) {
    list = list.filter((item) =>
      [item.name, item.shortName, item.tags.join(','), item.description].some((text) =>
        text.includes(params.keyword!)
      )
    )
  }
  if (params.category) list = list.filter((item) => item.category === params.category)
  if (params.status) list = list.filter((item) => item.status === params.status)
  if (params.favoriteOnly) list = list.filter((item) => params.favoriteIds?.includes(item.id))
  return list.sort((a, b) => a.sort - b.sort)
}

export function getPortalProjectDetailMock(projectId: number, canViewPassword = true) {
  const project = projects.find((item) => item.id === projectId && item.enabled === 1)
  if (!project) throw new Error('项目不存在或已停用')
  addLog(project.name, '打开项目详情', '项目详情', '查看项目详情')
  return {
    project,
    addresses: addresses.filter((item) => item.projectId === projectId && item.status === 1),
    credentials: canViewPassword
      ? credentials.filter((item) => item.projectId === projectId && item.status === 1)
      : [],
    qrcodes: qrcodes.filter((item) => item.projectId === projectId && item.status === 1),
    instruction: instructions.find((item) => item.projectId === projectId),
    canViewPassword,
    canCopyPassword: canViewPassword,
    canViewQrcode: true
  }
}

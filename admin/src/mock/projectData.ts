import type {
  OperationLog,
  ProjectAddress,
  ProjectCard,
  ProjectCredential,
  ProjectInstruction,
  ProjectPermission,
  ProjectQrcode,
  StatusConfig,
  StatusRecord
} from '@/types/project'

export const idSeed = {
  project: 7,
  address: 13,
  credential: 11,
  qrcode: 8,
  statusConfig: 7,
  statusRecord: 8,
  permission: 9,
  log: 12
}

export function nowText() {
  return new Date().toLocaleString('zh-CN', { hour12: false })
}

export function buildAsset(label: string, bg: string, fg = '#ffffff') {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="160" height="160"><rect width="160" height="160" rx="28" fill="${bg}"/><text x="50%" y="56%" text-anchor="middle" font-size="54" font-family="Arial" fill="${fg}" font-weight="700">${label}</text></svg>`
  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}

export const projects: ProjectCard[] = [
  {
    id: 1,
    name: '公司统一门户',
    shortName: '统一门户',
    logo: buildAsset('门户', '#2563eb'),
    category: '内部系统',
    tags: ['正式', '内网', 'SSO'],
    description: '公司内部应用入口，统一展示项目地址、账号资料和运行状态。',
    maintainerId: 1,
    maintainer: '管理员',
    sort: 10,
    enabled: 1,
    status: '可用',
    lastCheckTime: '2026-06-05 09:10:00',
    responseTime: 126,
    createTime: '2026-05-20 10:00:00',
    updateTime: '2026-06-05 09:10:00'
  },
  {
    id: 2,
    name: '客户项目交付看板',
    shortName: '交付看板',
    logo: buildAsset('交付', '#059669'),
    category: '客户项目',
    tags: ['正式', '外网'],
    description: '展示客户项目里程碑、风险、问题和验收资料的交付看板。',
    maintainerId: 4,
    maintainer: '王五',
    sort: 20,
    enabled: 1,
    status: '维护中',
    lastCheckTime: '2026-06-05 08:50:00',
    responseTime: 243,
    abnormalReason: '周五例行发布窗口',
    createTime: '2026-05-22 09:30:00',
    updateTime: '2026-06-05 08:50:00'
  },
  {
    id: 3,
    name: 'AI 原型生成平台',
    shortName: 'AI 原型',
    logo: buildAsset('AI', '#7c3aed'),
    category: 'AI工具',
    tags: ['测试', '内网', 'AI'],
    description: '为产品经理提供需求文档、原型页面和测试用例的智能生成工具。',
    maintainerId: 5,
    maintainer: '赵六',
    sort: 30,
    enabled: 1,
    status: '可用',
    lastCheckTime: '2026-06-05 09:12:00',
    responseTime: 98,
    createTime: '2026-05-24 15:00:00',
    updateTime: '2026-06-05 09:12:00'
  },
  {
    id: 4,
    name: '经营数据分析平台',
    shortName: '数据分析',
    logo: buildAsset('BI', '#dc2626'),
    category: '数据平台',
    tags: ['正式', '内网', '报表'],
    description: '集中展示销售、交付、财务和运维指标，支持经营例会复盘。',
    maintainerId: 7,
    maintainer: '周八',
    sort: 40,
    enabled: 1,
    status: '异常',
    lastCheckTime: '2026-06-05 08:58:00',
    responseTime: 0,
    abnormalReason: 'HTTP 502',
    createTime: '2026-05-28 11:20:00',
    updateTime: '2026-06-05 08:58:00'
  },
  {
    id: 5,
    name: '运维监控中心',
    shortName: '监控中心',
    logo: buildAsset('Ops', '#ea580c'),
    category: '运维服务',
    tags: ['正式', '内网', '监控'],
    description: '统一查看服务健康度、告警、CI/CD 和服务器资源。',
    maintainerId: 3,
    maintainer: '李四',
    sort: 50,
    enabled: 1,
    status: '可用',
    lastCheckTime: '2026-06-05 09:08:00',
    responseTime: 164,
    createTime: '2026-05-29 14:40:00',
    updateTime: '2026-06-05 09:08:00'
  },
  {
    id: 6,
    name: '售后服务小程序',
    shortName: '售后小程序',
    logo: buildAsset('小程', '#0891b2'),
    category: '小程序',
    tags: ['演示', '小程序'],
    description: '面向客户提交服务工单、查看进度和反馈满意度的小程序。',
    maintainerId: 8,
    maintainer: '吴九',
    sort: 60,
    enabled: 0,
    status: '未检测',
    createTime: '2026-06-01 16:00:00',
    updateTime: '2026-06-03 10:00:00'
  }
]

export const addresses: ProjectAddress[] = [
  { id: 1, projectId: 1, name: '正式地址', type: 'Web', url: 'https://portal.example.test', isDefault: 1, isDetection: 1, sort: 1, status: 1 },
  { id: 2, projectId: 1, name: '后台地址', type: '后台', url: 'https://portal.example.test/admin', isDefault: 0, isDetection: 0, sort: 2, status: 1 },
  { id: 3, projectId: 2, name: '正式地址', type: 'Web', url: 'https://delivery.example.test', isDefault: 1, isDetection: 1, sort: 1, status: 1 },
  { id: 4, projectId: 2, name: '项目文档', type: '文档', url: 'https://docs.example.test/delivery', isDefault: 0, isDetection: 0, sort: 2, status: 1 },
  { id: 5, projectId: 3, name: '测试地址', type: 'Web', url: 'https://prototype.example.test', isDefault: 1, isDetection: 1, sort: 1, status: 1 },
  { id: 6, projectId: 3, name: '接口文档', type: '文档', url: 'https://docs.example.test/ai-prototype', isDefault: 0, isDetection: 0, sort: 2, status: 1 },
  { id: 7, projectId: 4, name: '正式地址', type: 'Web', url: 'https://bi.example.test', isDefault: 1, isDetection: 1, sort: 1, status: 1 },
  { id: 8, projectId: 4, name: '后台地址', type: '后台', url: 'https://bi.example.test/admin', isDefault: 0, isDetection: 0, sort: 2, status: 1 },
  { id: 9, projectId: 5, name: '监控首页', type: 'Web', url: 'https://ops.example.test', isDefault: 1, isDetection: 1, sort: 1, status: 1 },
  { id: 10, projectId: 5, name: 'CI/CD', type: '其他', url: 'https://ci.example.test', isDefault: 0, isDetection: 0, sort: 2, status: 1 },
  { id: 11, projectId: 6, name: '体验地址', type: 'Web', url: 'https://mini.example.test', isDefault: 1, isDetection: 0, sort: 1, status: 0 },
  { id: 12, projectId: 6, name: '说明文档', type: '文档', url: 'https://docs.example.test/mini-service', isDefault: 0, isDetection: 0, sort: 2, status: 1 }
]

export const instructions: ProjectInstruction[] = projects.map((project) => ({
  projectId: project.id,
  browserRequirement: 'Chrome 120+ 或 Edge 120+',
  vpnRequirement: project.tags.includes('内网') ? '需连接公司 VPN 后访问' : '外网可访问',
  notes: `${project.name} 的访问资料由 ${project.maintainer} 维护，信息变更后请同步更新。`,
  maintainer: project.maintainer,
  contactPhone: '13800138000',
  updateTime: project.updateTime
}))

export const credentialSecrets: Record<number, string> = {
  1: 'DemoPortal2026',
  2: 'AdminPortal2026',
  3: 'DeliveryDemo2026',
  4: 'PrototypeDemo2026',
  5: 'PrototypeAdmin2026',
  6: 'BiViewer2026',
  7: 'BiAdmin2026',
  8: 'OpsViewer2026',
  9: 'OpsAdmin2026',
  10: 'MiniDemo2026'
}

export const credentials: ProjectCredential[] = [
  { id: 1, projectId: 1, name: '演示账号', username: 'portal_demo', passwordMasked: '************', environment: '演示', description: '门户演示账号，仅用于内部培训。', expireDate: '2026-12-31', status: 1, updateTime: '2026-06-01 10:00:00' },
  { id: 2, projectId: 1, name: '管理员账号', username: 'portal_admin', passwordMasked: '************', environment: '正式', description: '超级管理员仅限平台管理员使用。', expireDate: '2026-09-30', status: 1, updateTime: '2026-06-01 10:00:00' },
  { id: 3, projectId: 2, name: '客户演示账号', username: 'delivery_demo', passwordMasked: '************', environment: '演示', description: '客户验收演示账号。', expireDate: '2026-08-31', status: 1, updateTime: '2026-06-02 10:00:00' },
  { id: 4, projectId: 3, name: '测试账号', username: 'prototype_demo', passwordMasked: '************', environment: '测试', description: '供产品经理体验 AI 原型生成。', status: 1, updateTime: '2026-06-02 11:00:00' },
  { id: 5, projectId: 3, name: '管理账号', username: 'prototype_admin', passwordMasked: '************', environment: '测试', description: '仅维护模板与模型配置。', status: 1, updateTime: '2026-06-02 11:20:00' },
  { id: 6, projectId: 4, name: '经营查看账号', username: 'bi_viewer', passwordMasked: '************', environment: '正式', description: '经营层查看账号。', status: 1, updateTime: '2026-06-03 09:00:00' },
  { id: 7, projectId: 4, name: '报表管理员', username: 'bi_admin', passwordMasked: '************', environment: '正式', description: '数据集与权限维护。', status: 0, updateTime: '2026-06-03 09:20:00' },
  { id: 8, projectId: 5, name: '监控查看账号', username: 'ops_viewer', passwordMasked: '************', environment: '正式', description: '查看服务状态与告警。', status: 1, updateTime: '2026-06-03 13:00:00' },
  { id: 9, projectId: 5, name: '运维管理员', username: 'ops_admin', passwordMasked: '************', environment: '正式', description: '仅限运维负责人使用。', status: 1, updateTime: '2026-06-03 13:10:00' },
  { id: 10, projectId: 6, name: '小程序演示账号', username: 'mini_demo', passwordMasked: '************', environment: '演示', description: '小程序体验账号。', status: 1, updateTime: '2026-06-04 10:00:00' }
]

export const qrcodes: ProjectQrcode[] = [
  { id: 1, projectId: 1, name: '门户移动入口', image: buildAsset('QR', '#1d4ed8'), audience: '全员', description: '移动端快速访问入口。', status: 1, updateTime: '2026-06-01 10:00:00' },
  { id: 2, projectId: 2, name: '客户验收入口', image: buildAsset('验收', '#047857'), audience: '项目干系人', description: '客户项目验收用。', status: 1, updateTime: '2026-06-02 10:00:00' },
  { id: 3, projectId: 3, name: 'AI工具入口', image: buildAsset('AI', '#6d28d9'), audience: '产品与研发', description: '扫码进入 AI 原型工具。', status: 1, updateTime: '2026-06-02 11:00:00' },
  { id: 4, projectId: 4, name: '经营日报入口', image: buildAsset('日报', '#b91c1c'), audience: '经营层', description: '移动端查看日报摘要。', status: 1, updateTime: '2026-06-03 09:00:00' },
  { id: 5, projectId: 5, name: '告警订阅', image: buildAsset('告警', '#c2410c'), audience: '运维值班', description: '订阅告警通知。', status: 1, updateTime: '2026-06-03 13:00:00' },
  { id: 6, projectId: 6, name: '售后小程序', image: buildAsset('售后', '#0e7490'), audience: '客户与售后', description: '售后服务入口。', status: 1, updateTime: '2026-06-04 10:00:00' },
  { id: 7, projectId: 6, name: '旧版入口', image: buildAsset('旧', '#64748b'), audience: '历史用户', description: '旧版二维码，已停用。', status: 0, updateTime: '2026-05-20 10:00:00' }
]

export const statusConfigs: StatusConfig[] = projects.map((project) => ({
  id: project.id,
  projectId: project.id,
  projectName: project.name,
  autoEnabled: project.id === 6 ? 0 : 1,
  method: project.id === 5 ? 'TCP' : project.id === 6 ? '手动' : 'HTTP/HTTPS',
  checkAddress: addresses.find((item) => item.projectId === project.id && item.isDetection === 1)?.url || '',
  checkPort: project.id === 5 ? 443 : undefined,
  timeoutSeconds: 5,
  frequencyMinutes: 5,
  expectedStatusCodes: '200,302,401,403',
  manualStatus: project.status === '维护中' ? '维护中' : '无覆盖',
  manualReason: project.status === '维护中' ? '例行发布维护' : '',
  currentStatus: project.status,
  lastCheckTime: project.lastCheckTime,
  responseTime: project.responseTime,
  abnormalReason: project.abnormalReason
}))

export const statusRecords: StatusRecord[] = [
  { id: 1, projectId: 1, projectName: '公司统一门户', method: 'HTTP/HTTPS', checkAddress: 'https://portal.example.test', result: '可用', responseTime: 126, errorReason: '', checkTime: '2026-06-05 09:10:00', operator: '系统任务' },
  { id: 2, projectId: 2, projectName: '客户项目交付看板', method: 'HTTP/HTTPS', checkAddress: 'https://delivery.example.test', result: '可用', responseTime: 243, errorReason: '', checkTime: '2026-06-05 08:50:00', operator: '系统任务' },
  { id: 3, projectId: 3, projectName: 'AI 原型生成平台', method: 'HTTP/HTTPS', checkAddress: 'https://prototype.example.test', result: '可用', responseTime: 98, errorReason: '', checkTime: '2026-06-05 09:12:00', operator: '系统任务' },
  { id: 4, projectId: 4, projectName: '经营数据分析平台', method: 'HTTP/HTTPS', checkAddress: 'https://bi.example.test', result: '异常', responseTime: 0, errorReason: 'HTTP 502', checkTime: '2026-06-05 08:58:00', operator: '系统任务' },
  { id: 5, projectId: 5, projectName: '运维监控中心', method: 'TCP', checkAddress: 'ops.example.test', result: '可用', responseTime: 164, errorReason: '', checkTime: '2026-06-05 09:08:00', operator: '系统任务' },
  { id: 6, projectId: 2, projectName: '客户项目交付看板', method: '手动', checkAddress: 'https://delivery.example.test', result: '维护中', responseTime: 0, errorReason: '周五例行发布窗口', checkTime: '2026-06-05 09:00:00', operator: '管理员' },
  { id: 7, projectId: 4, projectName: '经营数据分析平台', method: 'HTTP/HTTPS', checkAddress: 'https://bi.example.test', result: '异常', responseTime: 0, errorReason: 'HTTP 502', checkTime: '2026-06-05 08:53:00', operator: '系统任务' }
]

export const permissions: ProjectPermission[] = [
  { id: 1, projectId: 1, targetType: '角色', targetId: 1, targetName: '超级管理员', visible: true, passwordView: true, passwordCopy: true, qrcodeView: true, status: 1, updateTime: '2026-06-01 10:00:00' },
  { id: 2, projectId: 1, targetType: '部门', targetId: 21, targetName: '产品研发中心/研发部', visible: true, passwordView: true, passwordCopy: false, qrcodeView: true, status: 1, updateTime: '2026-06-01 10:00:00' },
  { id: 3, projectId: 2, targetType: '用户', targetId: 4, targetName: '王五', visible: true, passwordView: true, passwordCopy: true, qrcodeView: true, status: 1, updateTime: '2026-06-02 10:00:00' },
  { id: 4, projectId: 3, targetType: '角色', targetId: 3, targetName: '普通用户', visible: true, passwordView: false, passwordCopy: false, qrcodeView: true, status: 1, updateTime: '2026-06-02 11:00:00' },
  { id: 5, projectId: 4, targetType: '部门', targetId: 31, targetName: '交付与运营中心/交付部', visible: true, passwordView: true, passwordCopy: false, qrcodeView: true, status: 1, updateTime: '2026-06-03 09:00:00' },
  { id: 6, projectId: 5, targetType: '用户', targetId: 3, targetName: '李四', visible: true, passwordView: true, passwordCopy: true, qrcodeView: true, status: 1, updateTime: '2026-06-03 13:00:00' },
  { id: 7, projectId: 6, targetType: '用户', targetId: 8, targetName: '吴九', visible: true, passwordView: false, passwordCopy: false, qrcodeView: true, status: 1, updateTime: '2026-06-04 10:00:00' },
  { id: 8, projectId: 4, targetType: '角色', targetId: 2, targetName: '管理员', visible: true, passwordView: true, passwordCopy: true, qrcodeView: true, status: 1, updateTime: '2026-06-03 09:00:00' }
]

export const operationLogs: OperationLog[] = [
  { id: 1, operateTime: '2026-06-05 09:12:00', operator: '管理员', account: 'admin', department: '总公司', projectName: 'AI 原型生成平台', operationType: '打开项目详情', target: '项目详情', result: '成功', ip: '10.10.1.21', device: 'Chrome / Windows', remark: '查看项目详情' },
  { id: 2, operateTime: '2026-06-05 09:10:10', operator: '管理员', account: 'admin', department: '总公司', projectName: '公司统一门户', operationType: '查看密码', target: '演示账号', result: '成功', ip: '10.10.1.21', device: 'Chrome / Windows', remark: '权限校验通过' },
  { id: 3, operateTime: '2026-06-05 09:08:20', operator: '李四', account: 'liuzhy002', department: '产品研发中心', projectName: '运维监控中心', operationType: '立即检测', target: '检测配置', result: '成功', ip: '10.10.1.32', device: 'Edge / Windows', remark: 'TCP 检测可用' },
  { id: 4, operateTime: '2026-06-05 09:00:00', operator: '管理员', account: 'admin', department: '总公司', projectName: '客户项目交付看板', operationType: '修改检测状态', target: '人工状态', result: '成功', ip: '10.10.1.21', device: 'Chrome / Windows', remark: '设置为维护中' },
  { id: 5, operateTime: '2026-06-05 08:59:30', operator: '周八', account: 'zhouba006', department: '交付与运营中心/交付部', projectName: '经营数据分析平台', operationType: '打开项目详情', target: '项目详情', result: '成功', ip: '10.10.2.17', device: 'Chrome / macOS', remark: '查看异常说明' },
  { id: 6, operateTime: '2026-06-04 18:20:00', operator: '管理员', account: 'admin', department: '总公司', projectName: '经营数据分析平台', operationType: '修改权限', target: '项目授权', result: '成功', ip: '10.10.1.21', device: 'Chrome / Windows', remark: '调整财务部密码查看权限' },
  { id: 7, operateTime: '2026-06-04 16:40:00', operator: '赵六', account: 'zhaoliu004', department: '产品研发中心/产品部', projectName: 'AI 原型生成平台', operationType: '复制密码', target: '测试账号', result: '失败', ip: '10.10.1.44', device: 'Edge / Windows', remark: '无复制密码权限' },
  { id: 8, operateTime: '2026-06-04 15:00:00', operator: '管理员', account: 'admin', department: '总公司', projectName: '售后服务小程序', operationType: '后台修改', target: '项目卡片', result: '成功', ip: '10.10.1.21', device: 'Chrome / Windows', remark: '停用项目卡片' },
  { id: 9, operateTime: '2026-06-04 10:00:00', operator: '吴九', account: 'wujiu007', department: '交付与运营中心/运维部', projectName: '售后服务小程序', operationType: '后台修改', target: '二维码', result: '成功', ip: '10.10.2.31', device: 'Chrome / Windows', remark: '替换售后小程序二维码' },
  { id: 10, operateTime: '2026-06-03 13:10:00', operator: '李四', account: 'liuzhy002', department: '产品研发中心', projectName: '运维监控中心', operationType: '查看密码', target: '运维管理员', result: '成功', ip: '10.10.1.32', device: 'Edge / Windows', remark: '权限校验通过' },
  { id: 11, operateTime: '2026-06-03 09:20:00', operator: '管理员', account: 'admin', department: '总公司', projectName: '经营数据分析平台', operationType: '后台修改', target: '账号凭据', result: '成功', ip: '10.10.1.21', device: 'Chrome / Windows', remark: '停用报表管理员凭据' }
]

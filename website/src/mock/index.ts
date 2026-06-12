import type {
  PortalProjectDetail,
  ProjectAddress,
  ProjectCard,
  ProjectCredential,
  ProjectInstruction,
  ProjectQrcode,
} from '@/types/project'

export const MOCK_ENABLED = true

type MockHandler = (body: unknown, params: URLSearchParams) => unknown

function buildAsset(label: string, bg: string) {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="160" height="160"><rect width="160" height="160" rx="28" fill="${bg}"/><text x="50%" y="56%" text-anchor="middle" font-size="50" font-family="Arial" fill="#fff" font-weight="700">${label}</text></svg>`
  return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`
}

const projects: ProjectCard[] = [
  {
    id: 1,
    name: '公司统一门户',
    shortName: '统一门户',
    logo: buildAsset('门户', '#2563eb'),
    category: '内部系统',
    tags: ['正式', '内网', 'SSO'],
    description: '公司内部应用入口，统一展示项目地址、账号资料和运行状态。',
    maintainer: '管理员',
    status: '可用',
    lastCheckTime: '2026-06-05 09:10:00',
    responseTime: 126,
  },
  {
    id: 2,
    name: '客户项目交付看板',
    shortName: '交付看板',
    logo: buildAsset('交付', '#059669'),
    category: '客户项目',
    tags: ['正式', '外网'],
    description: '展示客户项目里程碑、风险、问题和验收资料的交付看板。',
    maintainer: '王五',
    status: '维护中',
    lastCheckTime: '2026-06-05 08:50:00',
    responseTime: 243,
    abnormalReason: '周五例行发布窗口',
  },
  {
    id: 3,
    name: 'AI 原型生成平台',
    shortName: 'AI 原型',
    logo: buildAsset('AI', '#7c3aed'),
    category: 'AI工具',
    tags: ['测试', '内网', 'AI'],
    description: '为产品经理提供需求文档、原型页面和测试用例的智能生成工具。',
    maintainer: '赵六',
    status: '可用',
    lastCheckTime: '2026-06-05 09:12:00',
    responseTime: 98,
  },
  {
    id: 4,
    name: '经营数据分析平台',
    shortName: '数据分析',
    logo: buildAsset('BI', '#dc2626'),
    category: '数据平台',
    tags: ['正式', '内网', '报表'],
    description: '集中展示销售、交付、财务和运维指标，支持经营例会复盘。',
    maintainer: '周八',
    status: '异常',
    lastCheckTime: '2026-06-05 08:58:00',
    responseTime: 0,
    abnormalReason: 'HTTP 502',
  },
  {
    id: 5,
    name: '运维监控中心',
    shortName: '监控中心',
    logo: buildAsset('Ops', '#ea580c'),
    category: '运维服务',
    tags: ['正式', '内网', '监控'],
    description: '统一查看服务健康度、告警、CI/CD 和服务器资源。',
    maintainer: '李四',
    status: '可用',
    lastCheckTime: '2026-06-05 09:08:00',
    responseTime: 164,
  },
]

const addresses: Record<number, ProjectAddress[]> = {
  1: [
    { id: 1, name: '正式地址', type: 'Web', url: 'https://portal.example.test', isDefault: 1 },
    { id: 2, name: '后台地址', type: '后台', url: 'https://portal.example.test/admin', isDefault: 0 },
  ],
  2: [
    { id: 3, name: '正式地址', type: 'Web', url: 'https://delivery.example.test', isDefault: 1 },
    { id: 4, name: '项目文档', type: '文档', url: 'https://docs.example.test/delivery', isDefault: 0 },
  ],
  3: [
    { id: 5, name: '测试地址', type: 'Web', url: 'https://prototype.example.test', isDefault: 1 },
    { id: 6, name: '接口文档', type: '文档', url: 'https://docs.example.test/ai-prototype', isDefault: 0 },
  ],
  4: [{ id: 7, name: '正式地址', type: 'Web', url: 'https://bi.example.test', isDefault: 1 }],
  5: [{ id: 8, name: '监控首页', type: 'Web', url: 'https://ops.example.test', isDefault: 1 }],
}

const credentials: Record<number, ProjectCredential[]> = {
  1: [
    {
      id: 1,
      addressId: 1,
      name: '演示账号',
      username: 'portal_demo',
      password: 'DemoPortal2026',
      passwordMasked: '************',
      environment: '演示',
      description: '门户演示账号，仅用于内部培训。',
    },
    {
      id: 2,
      addressId: 2,
      name: '管理员账号',
      username: 'portal_admin',
      password: 'AdminPortal2026',
      passwordMasked: '************',
      environment: '正式',
      description: '超级管理员仅限平台管理员使用。',
    },
  ],
  2: [
    {
      id: 3,
      addressId: 3,
      name: '客户演示账号',
      username: 'delivery_demo',
      password: 'DeliveryDemo2026',
      passwordMasked: '************',
      environment: '演示',
      description: '客户验收演示账号。',
    },
  ],
  3: [
    {
      id: 4,
      addressId: 5,
      name: '测试账号',
      username: 'prototype_demo',
      password: 'PrototypeDemo2026',
      passwordMasked: '************',
      environment: '测试',
      description: '供产品经理体验 AI 原型生成。',
    },
  ],
  4: [
    {
      id: 5,
      addressId: 7,
      name: '经营查看账号',
      username: 'bi_viewer',
      password: 'BiViewer2026',
      passwordMasked: '************',
      environment: '正式',
      description: '经营层查看账号。',
    },
  ],
  5: [
    {
      id: 6,
      addressId: 8,
      name: '监控查看账号',
      username: 'ops_viewer',
      password: 'OpsViewer2026',
      passwordMasked: '************',
      environment: '正式',
      description: '查看服务状态与告警。',
    },
  ],
}

const credentialSecrets: Record<number, string> = {
  1: 'DemoPortal2026',
  2: 'AdminPortal2026',
  3: 'DeliveryDemo2026',
  4: 'PrototypeDemo2026',
  5: 'BiViewer2026',
  6: 'OpsViewer2026',
}

const qrcodes: Record<number, ProjectQrcode[]> = {
  1: [{ id: 1, name: '门户移动入口', image: buildAsset('QR', '#1d4ed8'), audience: '全员', description: '移动端快速访问入口。' }],
  2: [{ id: 2, name: '客户验收入口', image: buildAsset('验收', '#047857'), audience: '项目干系人', description: '客户项目验收使用。' }],
  3: [{ id: 3, name: 'AI 工具入口', image: buildAsset('AI', '#6d28d9'), audience: '产品与研发', description: '扫码进入 AI 原型工具。' }],
  4: [{ id: 4, name: '经营日报入口', image: buildAsset('日报', '#b91c1c'), audience: '经营层', description: '移动端查看日报摘要。' }],
  5: [{ id: 5, name: '告警订阅', image: buildAsset('告警', '#c2410c'), audience: '运维值班', description: '订阅告警通知。' }],
}

function instruction(project: ProjectCard): ProjectInstruction {
  return {
    browserRequirement: 'Chrome 120+ 或 Edge 120+',
    vpnRequirement: project.tags.includes('内网') ? '需连接公司 VPN 后访问' : '外网可直接访问',
    notes: `${project.name} 的资料由 ${project.maintainer} 维护，如无法访问请优先联系维护人。`,
    maintainer: project.maintainer,
    contactPhone: '13800138000',
  }
}

function projectDetail(projectId: number): PortalProjectDetail {
  const project = projects.find((item) => item.id === projectId)
  if (!project) throw new Error('项目不存在')
  return {
    project,
    addresses: addresses[projectId] || [],
    credentials: credentials[projectId] || [],
    qrcodes: qrcodes[projectId] || [],
    instruction: instruction(project),
    canViewPassword: true,
    canCopyPassword: true,
    canViewQrcode: true,
  }
}

if (MOCK_ENABLED) {
  const mockRoutes: Record<string, Record<string, MockHandler>> = {
    POST: {
      '/api/auth/login': (body: unknown) => {
        const { username, password } = body as { username: string; password: string }
        if (username === 'admin' && password === '123456') {
          return {
            code: 200,
            data: {
              token: 'mock-token-one-platform-2026',
              user: { id: 1, name: '张明', email: 'admin@one-platform.local', avatar: '', role: 'admin' },
            },
            message: '登录成功',
          }
        }
        return { code: 401, data: null, message: '用户名或密码错误' }
      },
      '/api/auth/logout': () => ({ code: 200, data: true, message: '已退出登录' }),
      '/app/portal/action-log': () => ({ code: 200, data: true, message: '已记录' }),
    },
    GET: {
      '/api/auth/me': () => ({
        code: 200,
        data: { id: 1, name: '张明', email: 'admin@one-platform.local', avatar: '', role: 'admin' },
      }),
      '/app/portal/projects': (_, params) => {
        const keyword = params.get('keyword') || ''
        const category = params.get('category') || ''
        const status = params.get('status') || ''
        const list = projects.filter((project) => {
          const matchKeyword =
            !keyword ||
            [project.name, project.shortName, project.description, project.tags.join(',')].some((text) =>
              text.includes(keyword),
            )
          return matchKeyword && (!category || project.category === category) && (!status || project.status === status)
        })
        return { code: 200, data: list, message: '获取成功' }
      },
    },
  }

  const originalFetch = window.fetch.bind(window)

  window.fetch = async (input: RequestInfo | URL, init?: RequestInit) => {
    const rawUrl = typeof input === 'string' ? input : input instanceof URL ? input.href : (input as Request).url
    const parsedUrl = new URL(rawUrl, window.location.origin)
    const method = (init?.method || 'GET').toUpperCase()
    let handler = mockRoutes[method]?.[parsedUrl.pathname]
    if (!handler && method === 'GET') {
      const detailMatch = parsedUrl.pathname.match(/^\/app\/portal\/projects\/(\d+)$/)
      if (detailMatch) {
        handler = () => {
          try {
            return { code: 200, data: projectDetail(Number(detailMatch[1])), message: '获取成功' }
          } catch (error) {
            return { code: 404, data: null, message: error instanceof Error ? error.message : '项目不存在' }
          }
        }
      }
    }
    if (!handler && method === 'POST') {
      const revealMatch = parsedUrl.pathname.match(/^\/app\/portal\/credentials\/(\d+)\/reveal$/)
      const copyMatch = parsedUrl.pathname.match(/^\/app\/portal\/credentials\/(\d+)\/copy$/)
      if (revealMatch) {
        handler = () => ({ code: 200, data: credentialSecrets[Number(revealMatch[1])] || '', message: '获取成功' })
      }
      if (copyMatch) {
        handler = (body) => {
          const field = (body as { field?: string })?.field
          const credential = Object.values(credentials)
            .flat()
            .find((item) => item.id === Number(copyMatch[1]))
          const value = field === 'password' ? credentialSecrets[Number(copyMatch[1])] || '' : credential?.username || ''
          return { code: 200, data: value, message: '复制成功' }
        }
      }
    }

    if (handler) {
      await new Promise((resolve) => setTimeout(resolve, 220))
      let body: unknown
      if (init?.body) {
        try {
          body = JSON.parse(init.body as string)
        } catch {
          body = init.body
        }
      }
      const data = handler(body, parsedUrl.searchParams)
      return new Response(JSON.stringify(data), {
        status: 200,
        headers: { 'Content-Type': 'application/json' },
      })
    }

    return originalFetch(input, init)
  }
}

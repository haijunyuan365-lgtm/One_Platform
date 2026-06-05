/**
 * 快速入口配置
 * 包含：应用列表、快速链接等配置
 */
import type { FastEnterConfig } from '@/types/config'

const fastEnterConfig: FastEnterConfig = {
  // 显示条件（屏幕宽度）
  minWidth: 1200,
  // 应用列表
  applications: [
    {
      name: '项目卡片',
      description: '维护项目基础信息与展示状态',
      icon: '&#xe721;',
      iconColor: '#377dff',
      enabled: true,
      order: 1,
      routeName: 'ProjectCard'
    },
    {
      name: '详情内容',
      description: '维护访问地址、说明和联系人',
      icon: '&#xe812;',
      iconColor: '#ff3b30',
      enabled: true,
      order: 2,
      routeName: 'ProjectDetailContent'
    },
    {
      name: '账号凭据',
      description: '维护项目账号和凭据资料',
      icon: '&#xe7ed;',
      iconColor: '#7A7FFF',
      enabled: true,
      order: 3,
      routeName: 'ProjectCredential'
    },
    {
      name: '状态检测',
      description: '配置项目可用性检测规则',
      icon: '&#xe70a;',
      iconColor: '#13DEB9',
      enabled: true,
      order: 4,
      routeName: 'StatusConfig'
    },
    {
      name: '操作日志',
      description: '查询敏感操作与后台维护日志',
      icon: '&#xe788;',
      iconColor: '#ffb100',
      enabled: true,
      order: 5,
      routeName: 'OperationLog'
    }
  ],
  // 快速链接
  quickLinks: [
    {
      name: '登录',
      enabled: true,
      order: 1,
      routeName: 'Login'
    },
    {
      name: '忘记密码',
      enabled: true,
      order: 2,
      routeName: 'ForgetPassword'
    }
  ]
}

export default Object.freeze(fastEnterConfig)

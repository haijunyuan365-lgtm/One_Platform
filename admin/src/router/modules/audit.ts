import { AppRouteRecord } from '@/types/router'

export const auditRoutes: AppRouteRecord = {
  path: '/audit',
  name: 'AuditManagement',
  component: () => import('@/views/index/index.vue'),
  meta: {
    title: 'menus.audit.title',
    icon: 'Operation',
    isFirstLevel: true
  },
  children: [
    {
      path: '',
      name: 'AuditManagementIndex',
      component: () => import('@/views/audit/project-permission/index.vue'),
      meta: {
        title: 'menus.audit.title',
        keepAlive: true,
        isHide: true
      }
    },
    {
      path: 'project-permission',
      name: 'ProjectPermission',
      component: () => import('@/views/audit/project-permission/index.vue'),
      meta: {
        title: 'menus.audit.projectPermission',
        keepAlive: true
      }
    },
    {
      path: 'operation-log',
      name: 'OperationLog',
      component: () => import('@/views/audit/operation-log/index.vue'),
      meta: {
        title: 'menus.audit.operationLog',
        keepAlive: true
      }
    }
  ]
}

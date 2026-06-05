import { AppRouteRecord } from '@/types/router'

export const statusRoutes: AppRouteRecord = {
  path: '/status',
  name: 'StatusManagement',
  component: () => import('@/views/index/index.vue'),
  meta: {
    title: 'menus.status.title',
    icon: 'Monitor',
    isFirstLevel: true
  },
  children: [
    {
      path: '',
      name: 'StatusManagementIndex',
      component: () => import('@/views/status/config/index.vue'),
      meta: {
        title: 'menus.status.title',
        keepAlive: true,
        isHide: true
      }
    },
    {
      path: 'config',
      name: 'StatusConfig',
      component: () => import('@/views/status/config/index.vue'),
      meta: {
        title: 'menus.status.config',
        keepAlive: true
      }
    },
    {
      path: 'record',
      name: 'StatusRecord',
      component: () => import('@/views/status/record/index.vue'),
      meta: {
        title: 'menus.status.record',
        keepAlive: true
      }
    }
  ]
}

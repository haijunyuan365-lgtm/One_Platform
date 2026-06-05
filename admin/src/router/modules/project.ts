import { AppRouteRecord } from '@/types/router'

export const projectRoutes: AppRouteRecord = {
  path: '/project',
  name: 'ProjectManagement',
  component: () => import('@/views/index/index.vue'),
  meta: {
    title: 'menus.project.title',
    icon: 'FolderOpened',
    isFirstLevel: true
  },
  children: [
    {
      path: '',
      name: 'ProjectManagementIndex',
      component: () => import('@/views/project/card/index.vue'),
      meta: {
        title: 'menus.project.title',
        keepAlive: true,
        isHide: true
      }
    },
    {
      path: 'card',
      name: 'ProjectCard',
      component: () => import('@/views/project/card/index.vue'),
      meta: {
        title: 'menus.project.card',
        keepAlive: true
      }
    },
    {
      path: 'detail-content',
      name: 'ProjectDetailContent',
      component: () => import('@/views/project/detail-content/index.vue'),
      meta: {
        title: 'menus.project.detailContent',
        keepAlive: true
      }
    },
    {
      path: 'credential',
      name: 'ProjectCredential',
      component: () => import('@/views/project/credential/index.vue'),
      meta: {
        title: 'menus.project.credential',
        keepAlive: true
      }
    },
    {
      path: 'qrcode',
      name: 'ProjectQrcode',
      component: () => import('@/views/project/qrcode/index.vue'),
      meta: {
        title: 'menus.project.qrcode',
        keepAlive: true
      }
    }
  ]
}

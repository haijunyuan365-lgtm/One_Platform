import { AppRouteRecord } from '@/types/router'
import { organizationTemplateRoutes } from './organization-template'
import { permissionTemplateRoutes } from './permission-template'
import { projectRoutes } from './project'
import { statusRoutes } from './status'
import { auditRoutes } from './audit'

export const routeModules: AppRouteRecord[] = [
  organizationTemplateRoutes,
  permissionTemplateRoutes,
  projectRoutes,
  statusRoutes,
  auditRoutes
]

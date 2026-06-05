/**
 * 部门管理 API — 桥接 organization.ts
 */

import {
  getDepartmentList,
  addDepartment,
  updateDepartment,
  deleteDepartment,
  updateDepartmentStatus
} from './organization'
import type { Department } from '@/types/api'

export type { Department } from '@/types/api'

export const departmentApi = {
  getList: getDepartmentList,
  add: addDepartment,
  update: (data: Partial<Department>) => updateDepartment(data.id!, data),
  delete: deleteDepartment,
  updateStatus: updateDepartmentStatus
}

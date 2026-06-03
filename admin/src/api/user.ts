/**
 * 用户管理 API — 桥接 organization.ts
 */

import {
  getUserList,
  addUser,
  updateUser,
  deleteUser,
  batchDeleteUsers,
  updateUserStatus,
  batchSetPosition,
  batchSetRole
} from './organization'
import type { AdminUser } from '@/types/api'

export type { AdminUser } from '@/types/api'

export const userApi = {
  getList: getUserList,
  add: (data: Partial<AdminUser>) => addUser(data),
  update: (data: Partial<AdminUser> & { id: number }) => updateUser(data.id, data),
  delete: deleteUser,
  batchDelete: batchDeleteUsers,
  updateStatus: updateUserStatus
}

/**
 * 批量设置岗位
 */
export { batchSetPosition, batchSetRole }

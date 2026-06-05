/**
 * 权限管理 Mock 数据
 */

import type { Role, Menu } from '@/types/api'

// 模拟角色数据
const mockRoles: Role[] = [
  {
    id: 1,
    code: 'super_admin',
    name: '超级管理员',
    description: '平台最高权限，维护用户、部门、角色、项目资料、状态检测和审计日志',
    menuIds: [1, 11, 12, 2, 21, 22, 3, 31, 32, 33, 34, 4, 41, 42, 5, 51, 52],
    userCount: 1,
    sort: 1,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-01-07 10:00:00'
  },
  {
    id: 2,
    code: 'project_admin',
    name: '项目管理员',
    description: '负责指定项目的卡片、访问资料、凭据、二维码和检测配置维护',
    menuIds: [3, 31, 32, 33, 34, 4, 41, 42, 5, 52],
    userCount: 6,
    sort: 2,
    status: 1,
    createTime: '2024-01-02 10:00:00',
    updateTime: '2025-01-06 15:30:00'
  },
  {
    id: 3,
    code: 'dept_admin',
    name: '部门管理员',
    description: '查看本部门用户和授权项目，协助维护部门范围内的项目访问资料',
    menuIds: [1, 11, 3, 31, 4, 42, 5, 52],
    userCount: 8,
    sort: 3,
    status: 1,
    createTime: '2024-01-03 10:00:00',
    updateTime: '2025-01-05 09:20:00'
  },
  {
    id: 4,
    code: 'employee',
    name: '普通员工',
    description: '使用前台项目门户查看授权项目入口和访问说明',
    menuIds: [],
    userCount: 80,
    sort: 4,
    status: 1,
    createTime: '2024-01-04 10:00:00',
    updateTime: '2025-01-04 14:10:00'
  },
  {
    id: 5,
    code: 'guest',
    name: '访客用户',
    description: '临时查看指定项目入口和基础说明，不授予后台维护权限',
    menuIds: [],
    userCount: 12,
    sort: 5,
    status: 1,
    createTime: '2024-01-05 10:00:00',
    updateTime: '2025-01-03 11:45:00'
  }
]

let nextRoleId = 6

// 模拟菜单数据（简化版，实际应该从菜单管理获取）
const mockMenus: Menu[] = [
  {
    id: 1,
    parentId: 0,
    name: 'organizationTemplate',
    title: '系统管理',
    type: 'directory',
    icon: 'OfficeBuilding',
    path: '/organization-template',
    sort: 1,
    status: 1,
    children: [
      {
        id: 11,
        parentId: 1,
        name: 'user',
        title: '用户管理',
        type: 'menu',
        path: '/organization-template/user',
        sort: 1,
        status: 1
      },
      {
        id: 12,
        parentId: 1,
        name: 'department',
        title: '部门管理',
        type: 'menu',
        path: '/organization-template/department',
        sort: 2,
        status: 1
      }
    ]
  },
  {
    id: 2,
    parentId: 0,
    name: 'permissionTemplate',
    title: '权限管理',
    type: 'directory',
    icon: 'Lock',
    path: '/permission-template',
    sort: 2,
    status: 1,
    children: [
      {
        id: 21,
        parentId: 2,
        name: 'role',
        title: '角色管理',
        type: 'menu',
        path: '/permission-template/role',
        sort: 1,
        status: 1
      },
      {
        id: 22,
        parentId: 2,
        name: 'menu',
        title: '菜单管理',
        type: 'menu',
        path: '/permission-template/menu',
        sort: 2,
        status: 1
      }
    ]
  },
  {
    id: 3,
    parentId: 0,
    name: 'project',
    title: '项目管理',
    type: 'directory',
    icon: 'FolderOpened',
    path: '/project',
    sort: 3,
    status: 1,
    children: [
      {
        id: 31,
        parentId: 3,
        name: 'card',
        title: '项目卡片管理',
        type: 'menu',
        path: '/project/card',
        sort: 1,
        status: 1
      },
      {
        id: 32,
        parentId: 3,
        name: 'detailContent',
        title: '详情内容管理',
        type: 'menu',
        path: '/project/detail-content',
        sort: 2,
        status: 1
      },
      {
        id: 33,
        parentId: 3,
        name: 'credential',
        title: '账号凭据管理',
        type: 'menu',
        path: '/project/credential',
        sort: 3,
        status: 1
      },
      {
        id: 34,
        parentId: 3,
        name: 'qrcode',
        title: '二维码管理',
        type: 'menu',
        path: '/project/qrcode',
        sort: 4,
        status: 1
      }
    ]
  },
  {
    id: 4,
    parentId: 0,
    name: 'status',
    title: '状态检测',
    type: 'directory',
    icon: 'Monitor',
    path: '/status',
    sort: 4,
    status: 1,
    children: [
      {
        id: 41,
        parentId: 4,
        name: 'config',
        title: '检测配置',
        type: 'menu',
        path: '/status/config',
        sort: 1,
        status: 1
      },
      {
        id: 42,
        parentId: 4,
        name: 'record',
        title: '检测记录',
        type: 'menu',
        path: '/status/record',
        sort: 2,
        status: 1
      }
    ]
  },
  {
    id: 5,
    parentId: 0,
    name: 'audit',
    title: '权限与审计',
    type: 'directory',
    icon: 'Operation',
    path: '/audit',
    sort: 5,
    status: 1,
    children: [
      {
        id: 51,
        parentId: 5,
        name: 'projectPermission',
        title: '项目权限配置',
        type: 'menu',
        path: '/audit/project-permission',
        sort: 1,
        status: 1
      },
      {
        id: 52,
        parentId: 5,
        name: 'operationLog',
        title: '操作日志',
        type: 'menu',
        path: '/audit/operation-log',
        sort: 2,
        status: 1
      }
    ]
  }
]

/**
 * 获取角色列表（支持分页）
 */
export function getRoleListMock(params?: {
  keyword?: string
  status?: number | string
  page?: number
  pageSize?: number
}) {
  let filteredRoles = [...mockRoles]

  // 关键词筛选
  if (params?.keyword) {
    const keyword = params.keyword.toLowerCase()
    filteredRoles = filteredRoles.filter(
      (role) =>
        role.name.toLowerCase().includes(keyword) || role.code.toLowerCase().includes(keyword)
    )
  }

  // 状态筛选
  if (params?.status !== undefined && params?.status !== '') {
    const status = Number(params.status)
    filteredRoles = filteredRoles.filter((role) => role.status === status)
  }

  const total = filteredRoles.length
  const page = params?.page || 1
  const pageSize = params?.pageSize || 10
  const start = (page - 1) * pageSize
  const end = start + pageSize

  return {
    list: filteredRoles.slice(start, end),
    total,
    page,
    pageSize
  }
}

/**
 * 新增角色
 */
export function addRoleMock(data: Partial<Role>) {
  const newRole: Role = {
    id: nextRoleId++,
    code: data.code || '',
    name: data.name || '',
    description: data.description,
    menuIds: data.menuIds || [],
    userCount: 0,
    sort: data.sort || 0,
    status: data.status ?? 1,
    createTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
    updateTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
  }

  mockRoles.unshift(newRole)
  return newRole
}

/**
 * 更新角色
 */
export function updateRoleMock(id: number, data: Partial<Role>) {
  const index = mockRoles.findIndex((role) => role.id === id)
  if (index === -1) {
    return false
  }

  mockRoles[index] = {
    ...mockRoles[index],
    ...data,
    updateTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
  }
  return true
}

/**
 * 删除角色
 */
export function deleteRoleMock(id: number) {
  const index = mockRoles.findIndex((role) => role.id === id)
  if (index === -1) {
    return false
  }

  mockRoles.splice(index, 1)
  return true
}

/**
 * 更新角色状态
 */
export function updateRoleStatusMock(id: number, status: number) {
  return updateRoleMock(id, { status })
}

/**
 * 获取角色的菜单权限
 */
export function getRoleMenusMock(id: number) {
  const role = mockRoles.find((role) => role.id === id)
  if (!role) {
    return []
  }
  return role.menuIds || []
}

/**
 * 更新角色的菜单权限
 */
export function updateRoleMenusMock(id: number, menuIds: number[]) {
  return updateRoleMock(id, { menuIds })
}

/**
 * 获取菜单列表（树形结构）
 */
export function getMenuListMock() {
  return mockMenus
}

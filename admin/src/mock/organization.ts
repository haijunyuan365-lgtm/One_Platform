/**
 * 组织管理 Mock 数据
 */

import type { Department, AdminUser, Role, Position, Menu } from '@/types/api'

// 模拟部门数据
const mockDepartments: Department[] = [
  {
    id: 1,
    name: '公司本部',
    parentId: null,
    code: 'HQ',
    type: '公司',
    leader: '张三',
    phone: '13800138001',
    sort: 1,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00',
    children: [
      {
        id: 2,
        name: '产品研发中心',
        parentId: 1,
        code: 'PRODUCT-RD',
        type: '中心',
        leader: '李四',
        phone: '13800138002',
        sort: 1,
        status: 1,
        createTime: '2024-01-02 10:00:00',
        updateTime: '2025-03-01 09:00:00',
        children: [
          {
            id: 21,
            name: '研发部',
            parentId: 2,
            code: 'RD',
            type: '部门',
            leader: '王五',
            phone: '13800138021',
            sort: 1,
            status: 1,
            createTime: '2024-01-03 10:00:00',
            updateTime: '2025-03-01 09:00:00'
          },
          {
            id: 22,
            name: '产品部',
            parentId: 2,
            code: 'PRODUCT',
            type: '部门',
            leader: '赵六',
            phone: '13800138022',
            sort: 2,
            status: 1,
            createTime: '2024-01-03 10:00:00',
            updateTime: '2025-03-01 09:00:00'
          }
        ]
      },
      {
        id: 3,
        name: '交付与运营中心',
        parentId: 1,
        code: 'DELIVERY-OPS',
        type: '中心',
        leader: '孙七',
        phone: '13800138003',
        sort: 2,
        status: 1,
        createTime: '2024-01-02 10:00:00',
        updateTime: '2025-03-01 09:00:00',
        children: [
          {
            id: 31,
            name: '交付部',
            parentId: 3,
            code: 'DELIVERY',
            type: '部门',
            leader: '周八',
            phone: '13800138031',
            sort: 1,
            status: 1,
            createTime: '2024-01-03 10:00:00',
            updateTime: '2025-03-01 09:00:00'
          },
          {
            id: 32,
            name: '运维部',
            parentId: 3,
            code: 'OPS',
            type: '部门',
            leader: '吴九',
            phone: '13800138032',
            sort: 2,
            status: 1,
            createTime: '2024-01-03 10:00:00',
            updateTime: '2025-03-01 09:00:00'
          }
        ]
      }
    ]
  }
]

let nextDepartmentId = 100

// 模拟用户数据
const mockUsers: AdminUser[] = [
  {
    id: 1,
    username: 'admin',
    realName: '管理员',
    phone: '13800138000',
    email: '1144837984@qq.com',
    departmentId: 1,
    departmentName: '公司本部',
    positionId: 1,
    positionName: '平台负责人',
    roles: [{ id: 1, name: '超级管理员', code: 'super_admin' }],
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 2,
    username: 'wangjg001',
    realName: '张三',
    phone: '13800138001',
    email: 'zhangsan@example.com',
    departmentId: 1,
    departmentName: '公司本部',
    positionId: 1,
    positionName: '平台负责人',
    roles: [{ id: 2, name: '项目管理员', code: 'project_admin' }],
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 3,
    username: 'liuzhy002',
    realName: '李四',
    phone: '13800138002',
    email: 'lisi@example.com',
    departmentId: 2,
    departmentName: '产品研发中心',
    positionId: 2,
    positionName: '中心负责人',
    roles: [{ id: 2, name: '项目管理员', code: 'project_admin' }],
    status: 1,
    createTime: '2024-01-02 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 4,
    username: 'wangwu003',
    realName: '王五',
    phone: '13800138021',
    email: 'wangwu@example.com',
    departmentId: 21,
    departmentName: '产品研发中心/研发部',
    positionId: 3,
    positionName: '部门经理',
    roles: [{ id: 3, name: '部门管理员', code: 'dept_admin' }],
    status: 1,
    createTime: '2024-01-03 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 5,
    username: 'zhaoliu004',
    realName: '赵六',
    phone: '13800138022',
    email: 'zhaoliu@example.com',
    departmentId: 22,
    departmentName: '产品研发中心/产品部',
    positionId: 3,
    positionName: '部门经理',
    roles: [{ id: 4, name: '普通员工', code: 'employee' }],
    status: 1,
    createTime: '2024-01-03 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 6,
    username: 'sunqi005',
    realName: '孙七',
    phone: '13800138003',
    email: 'sunqi@example.com',
    departmentId: 3,
    departmentName: '交付与运营中心',
    positionId: 2,
    positionName: '中心负责人',
    roles: [{ id: 2, name: '项目管理员', code: 'project_admin' }],
    status: 1,
    createTime: '2024-01-02 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 7,
    username: 'zhouba006',
    realName: '周八',
    phone: '13800138031',
    email: 'zhouba@example.com',
    departmentId: 31,
    departmentName: '交付与运营中心/交付部',
    positionId: 3,
    positionName: '部门经理',
    roles: [{ id: 4, name: '普通员工', code: 'employee' }],
    status: 1,
    createTime: '2024-01-03 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 8,
    username: 'wujiu007',
    realName: '吴九',
    phone: '13800138032',
    email: 'wujiu@example.com',
    departmentId: 32,
    departmentName: '交付与运营中心/运维部',
    positionId: 3,
    positionName: '部门经理',
    roles: [{ id: 4, name: '普通员工', code: 'employee' }],
    status: 1,
    createTime: '2024-01-03 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  }
]

let nextUserId = 100

// 模拟岗位数据
const mockPositions: Position[] = [
  {
    id: 1,
    name: '平台负责人',
    code: 'PLATFORM_OWNER',
    sort: 1,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 2,
    name: '中心负责人',
    code: 'CENTER_OWNER',
    sort: 2,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 3,
    name: '部门经理',
    code: 'DEPT_MANAGER',
    sort: 3,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 4,
    name: '员工',
    code: 'STAFF',
    sort: 4,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  }
]

let nextPositionId = 10

// 模拟菜单数据
const mockMenuData: Menu[] = [
  {
    id: 1,
    name: 'OrganizationTemplate',
    title: '系统管理',
    type: 'directory',
    icon: 'OfficeBuilding',
    path: '/organization-template',
    sort: 1,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2024-01-01 10:00:00',
    children: [
      {
        id: 11,
        parentId: 1,
        name: 'OrganizationTemplateUser',
        title: '用户管理',
        type: 'menu',
        path: '/organization-template/user',
        component: '@/views/organization/user/index.vue',
        permission: 'organization:user:view',
        sort: 1,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      },
      {
        id: 12,
        parentId: 1,
        name: 'OrganizationTemplateDepartment',
        title: '部门管理',
        type: 'menu',
        path: '/organization-template/department',
        component: '@/views/organization/department/index.vue',
        permission: 'organization:department:view',
        sort: 2,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      }
    ]
  },
  {
    id: 2,
    name: 'PermissionTemplate',
    title: '权限管理',
    type: 'directory',
    icon: 'Lock',
    path: '/permission-template',
    sort: 2,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2024-01-01 10:00:00',
    children: [
      {
        id: 21,
        parentId: 2,
        name: 'PermissionTemplateRole',
        title: '角色管理',
        type: 'menu',
        path: '/permission-template/role',
        component: '@/views/permission/role/index.vue',
        permission: 'permission:role:view',
        sort: 1,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      },
      {
        id: 22,
        parentId: 2,
        name: 'PermissionTemplateMenu',
        title: '菜单管理',
        type: 'menu',
        path: '/permission-template/menu',
        component: '@/views/permission/menu/index.vue',
        permission: 'permission:menu:view',
        sort: 2,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      }
    ]
  },
  {
    id: 3,
    name: 'ProjectManagement',
    title: '项目管理',
    type: 'directory',
    icon: 'FolderOpened',
    path: '/project',
    sort: 3,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2024-01-01 10:00:00',
    children: [
      {
        id: 31,
        parentId: 3,
        name: 'ProjectCard',
        title: '项目卡片管理',
        type: 'menu',
        path: '/project/card',
        component: '@/views/project/card/index.vue',
        permission: 'project:card:view',
        sort: 1,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      },
      {
        id: 32,
        parentId: 3,
        name: 'ProjectDetailContent',
        title: '详情内容管理',
        type: 'menu',
        path: '/project/detail-content',
        component: '@/views/project/detail-content/index.vue',
        permission: 'project:detail:view',
        sort: 2,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      },
      {
        id: 33,
        parentId: 3,
        name: 'ProjectCredential',
        title: '账号凭据管理',
        type: 'menu',
        path: '/project/credential',
        component: '@/views/project/credential/index.vue',
        permission: 'project:credential:view',
        sort: 3,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      },
      {
        id: 34,
        parentId: 3,
        name: 'ProjectQrcode',
        title: '二维码管理',
        type: 'menu',
        path: '/project/qrcode',
        component: '@/views/project/qrcode/index.vue',
        permission: 'project:qrcode:view',
        sort: 4,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      }
    ]
  },
  {
    id: 4,
    name: 'StatusManagement',
    title: '状态检测',
    type: 'directory',
    icon: 'Monitor',
    path: '/status',
    sort: 4,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2024-01-01 10:00:00',
    children: [
      {
        id: 41,
        parentId: 4,
        name: 'StatusConfig',
        title: '检测配置',
        type: 'menu',
        path: '/status/config',
        component: '@/views/status/config/index.vue',
        permission: 'status:config:view',
        sort: 1,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      },
      {
        id: 42,
        parentId: 4,
        name: 'StatusRecord',
        title: '检测记录',
        type: 'menu',
        path: '/status/record',
        component: '@/views/status/record/index.vue',
        permission: 'status:record:view',
        sort: 2,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      }
    ]
  },
  {
    id: 5,
    name: 'AuditManagement',
    title: '权限与审计',
    type: 'directory',
    icon: 'Operation',
    path: '/audit',
    sort: 5,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2024-01-01 10:00:00',
    children: [
      {
        id: 51,
        parentId: 5,
        name: 'ProjectPermission',
        title: '项目权限配置',
        type: 'menu',
        path: '/audit/project-permission',
        component: '@/views/audit/project-permission/index.vue',
        permission: 'audit:project-permission:view',
        sort: 1,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      },
      {
        id: 52,
        parentId: 5,
        name: 'OperationLog',
        title: '操作日志',
        type: 'menu',
        path: '/audit/operation-log',
        component: '@/views/audit/operation-log/index.vue',
        permission: 'audit:operation-log:view',
        sort: 2,
        status: 1,
        createTime: '2024-01-01 10:00:00',
        updateTime: '2024-01-01 10:00:00'
      }
    ]
  }
]

/**
 * 获取部门列表 Mock 函数
 */
export function getDepartmentListMock(params: any = {}) {
  const { name, status } = params || {}
  let filteredData = [...mockDepartments]

  // 筛选
  if (name) {
    filteredData = filterDepartmentsByName(filteredData, name)
  }
  if (status !== undefined && status !== null && status !== '') {
    const statusValue = typeof status === 'string' ? parseInt(status) : status
    filteredData = filterDepartmentsByStatus(filteredData, statusValue)
  }

  return filteredData
}

/**
 * 递归筛选部门（按名称）
 */
function filterDepartmentsByName(departments: Department[], name: string): Department[] {
  return departments
    .map((dept) => {
      const matchesName = dept.name.includes(name)
      const filteredChildren = dept.children ? filterDepartmentsByName(dept.children, name) : []

      if (matchesName || filteredChildren.length > 0) {
        return {
          ...dept,
          children: filteredChildren.length > 0 ? filteredChildren : dept.children
        }
      }
      return null
    })
    .filter(Boolean) as Department[]
}

/**
 * 递归筛选部门（按状态）
 */
function filterDepartmentsByStatus(departments: Department[], status: number): Department[] {
  return departments
    .filter((dept) => dept.status === status)
    .map((dept) => ({
      ...dept,
      children: dept.children ? filterDepartmentsByStatus(dept.children, status) : []
    }))
}

/**
 * 添加部门 Mock 函数
 */
export function addDepartmentMock(data: Partial<Department>) {
  const newDepartment: Department = {
    id: nextDepartmentId++,
    name: data.name || '',
    parentId: data.parentId || null,
    code: data.code || '',
    type: data.type || '部门',
    leader: data.leader || '',
    phone: data.phone || '',
    sort: data.sort || 1,
    status: data.status || 1,
    createTime: new Date().toLocaleString('zh-CN'),
    updateTime: new Date().toLocaleString('zh-CN')
  }

  if (data.parentId) {
    // 添加到父部门的 children
    const addToParent = (departments: Department[]): boolean => {
      for (const dept of departments) {
        if (dept.id === data.parentId) {
          if (!dept.children) {
            dept.children = []
          }
          dept.children.push(newDepartment)
          return true
        }
        if (dept.children && addToParent(dept.children)) {
          return true
        }
      }
      return false
    }
    addToParent(mockDepartments)
  } else {
    mockDepartments.push(newDepartment)
  }

  return newDepartment
}

/**
 * 更新部门 Mock 函数
 */
export function updateDepartmentMock(id: number, data: Partial<Department>) {
  const updateInTree = (departments: Department[]): boolean => {
    for (let i = 0; i < departments.length; i++) {
      if (departments[i].id === id) {
        departments[i] = {
          ...departments[i],
          ...data,
          updateTime: new Date().toLocaleString('zh-CN')
        }
        return true
      }
      if (departments[i].children && updateInTree(departments[i].children!)) {
        return true
      }
    }
    return false
  }

  return updateInTree(mockDepartments)
}

/**
 * 更新部门状态 Mock 函数
 */
export function updateDepartmentStatusMock(id: number, status: number) {
  const updateInTree = (departments: Department[]): boolean => {
    for (let i = 0; i < departments.length; i++) {
      if (departments[i].id === id) {
        departments[i].status = status
        departments[i].updateTime = new Date().toLocaleString('zh-CN')
        return true
      }
      if (departments[i].children && updateInTree(departments[i].children!)) {
        return true
      }
    }
    return false
  }

  return updateInTree(mockDepartments)
}

/**
 * 删除部门 Mock 函数
 */
export function deleteDepartmentMock(id: number) {
  const deleteFromTree = (departments: Department[]): boolean => {
    for (let i = 0; i < departments.length; i++) {
      if (departments[i].id === id) {
        departments.splice(i, 1)
        return true
      }
      if (departments[i].children && deleteFromTree(departments[i].children!)) {
        return true
      }
    }
    return false
  }

  if (deleteFromTree(mockDepartments)) {
    return true
  }
  throw new Error('部门不存在')
}

/**
 * 获取用户列表 Mock 函数
 */
export function getUserListMock(params: any = {}) {
  const { realName, username, phone, departmentId, status, page = 1, pageSize = 20 } = params || {}
  let filteredData = [...mockUsers]

  // 筛选
  if (realName) {
    filteredData = filteredData.filter((user) => user.realName?.includes(realName))
  }
  if (username) {
    filteredData = filteredData.filter((user) => user.username.includes(username))
  }
  if (phone) {
    filteredData = filteredData.filter((user) => user.phone?.includes(phone))
  }
  if (departmentId !== undefined && departmentId !== null && departmentId !== '') {
    const deptId = typeof departmentId === 'string' ? parseInt(departmentId) : departmentId
    filteredData = filteredData.filter((user) => user.departmentId === deptId)
  }
  if (status !== undefined && status !== null && status !== '') {
    const statusValue = typeof status === 'string' ? parseInt(status) : status
    filteredData = filteredData.filter((user) => user.status === statusValue)
  }

  // 分页
  const start = (page - 1) * pageSize
  const end = start + Number(pageSize)
  const list = filteredData.slice(start, end)

  return {
    list,
    total: filteredData.length
  }
}

/**
 * 添加用户 Mock 函数
 */
export function addUserMock(data: Partial<AdminUser>) {
  const newUser: AdminUser = {
    id: nextUserId++,
    username: data.username || '',
    realName: data.realName || '',
    phone: data.phone || '',
    email: data.email || '',
    departmentId: data.departmentId || 1,
    departmentName: data.departmentName || '',
    positionId: data.positionId || 1,
    positionName: data.positionName || '',
    roles: data.roles || [],
    status: data.status || 1,
    createTime: new Date().toLocaleString('zh-CN'),
    updateTime: new Date().toLocaleString('zh-CN')
  }
  mockUsers.push(newUser)
  return newUser
}

/**
 * 更新用户 Mock 函数
 */
export function updateUserMock(id: number, data: Partial<AdminUser>) {
  const index = mockUsers.findIndex((user) => user.id === id)
  if (index !== -1) {
    mockUsers[index] = {
      ...mockUsers[index],
      ...data,
      updateTime: new Date().toLocaleString('zh-CN')
    }
    return true
  }
  return false
}

/**
 * 批量删除用户 Mock 函数
 */
export function batchDeleteUsersMock(ids: number[]) {
  ids.forEach((id) => {
    const index = mockUsers.findIndex((user) => user.id === id)
    if (index !== -1) {
      mockUsers.splice(index, 1)
    }
  })
  return true
}

/**
 * 更新用户状态 Mock 函数
 */
export function updateUserStatusMock(id: number, status: number) {
  const index = mockUsers.findIndex((user) => user.id === id)
  if (index !== -1) {
    mockUsers[index].status = status
    mockUsers[index].updateTime = new Date().toLocaleString('zh-CN')
    return true
  }
  return false
}

/**
 * 重置用户密码 Mock 函数
 */
export function resetUserPasswordMock(id: number, password: string) {
  const index = mockUsers.findIndex((user) => user.id === id)
  if (index === -1) {
    throw new Error('用户不存在')
  }
  if (!/^(?=.*[A-Za-z])(?=.*\d).{8,30}$/.test(password)) {
    throw new Error('密码需包含字母和数字，长度不少于8位')
  }
  mockUsers[index].updateTime = new Date().toLocaleString('zh-CN')
  return true
}

/**
 * 批量设置岗位 Mock 函数
 */
export function batchSetPositionMock(ids: number[], positionName: string) {
  ids.forEach((id) => {
    const index = mockUsers.findIndex((user) => user.id === id)
    if (index !== -1) {
      mockUsers[index].positionName = positionName
      mockUsers[index].updateTime = new Date().toLocaleString('zh-CN')
    }
  })
  return true
}

/**
 * 批量设置角色 Mock 函数
 */
export function batchSetRoleMock(ids: number[], roleIds: number[]) {
  ids.forEach((id) => {
    const index = mockUsers.findIndex((user) => user.id === id)
    if (index !== -1) {
      // 这里简化处理，实际应该从角色列表中获取
      mockUsers[index].roles = roleIds.map((roleId) => ({
        id: roleId,
        name: '角色',
        code: 'role'
      }))
      mockUsers[index].updateTime = new Date().toLocaleString('zh-CN')
    }
  })
  return true
}

/**
 * 删除用户 Mock 函数
 */
export function deleteUserMock(id: number) {
  const index = mockUsers.findIndex((user) => user.id === id)
  if (index !== -1) {
    mockUsers.splice(index, 1)
    return true
  }
  throw new Error('用户不存在')
}

/**
 * 获取岗位列表 Mock 函数
 */
export function getPositionListMock(params: any = {}) {
  const { name, status, page = 1, pageSize = 20 } = params || {}
  let filteredData = [...mockPositions]

  // 筛选
  if (name) {
    filteredData = filteredData.filter((position) => position.name.includes(name))
  }
  if (status !== undefined && status !== null && status !== '') {
    const statusValue = typeof status === 'string' ? parseInt(status) : status
    filteredData = filteredData.filter((position) => position.status === statusValue)
  }

  // 分页
  const start = (page - 1) * pageSize
  const end = start + Number(pageSize)
  const list = filteredData.slice(start, end)

  return {
    list,
    total: filteredData.length
  }
}

/**
 * 添加岗位 Mock 函数
 */
export function addPositionMock(data: Partial<Position>) {
  const newPosition: Position = {
    id: nextPositionId++,
    name: data.name || '',
    code: data.code || '',
    sort: data.sort || 1,
    status: data.status || 1,
    createTime: new Date().toLocaleString('zh-CN'),
    updateTime: new Date().toLocaleString('zh-CN')
  }
  mockPositions.push(newPosition)
  return newPosition
}

/**
 * 更新岗位 Mock 函数
 */
export function updatePositionMock(id: number, data: Partial<Position>) {
  const index = mockPositions.findIndex((position) => position.id === id)
  if (index !== -1) {
    mockPositions[index] = {
      ...mockPositions[index],
      ...data,
      updateTime: new Date().toLocaleString('zh-CN')
    }
    return true
  }
  return false
}

/**
 * 更新岗位状态 Mock 函数
 */
export function updatePositionStatusMock(id: number, status: number) {
  const index = mockPositions.findIndex((position) => position.id === id)
  if (index !== -1) {
    mockPositions[index].status = status
    mockPositions[index].updateTime = new Date().toLocaleString('zh-CN')
    return true
  }
  return false
}

/**
 * 删除岗位 Mock 函数
 */
export function deletePositionMock(id: number) {
  const index = mockPositions.findIndex((position) => position.id === id)
  if (index !== -1) {
    mockPositions.splice(index, 1)
    return true
  }
  throw new Error('岗位不存在')
}

// ==================== 角色管理 Mock 函数 ====================

// 模拟角色数据
const mockRoles: Role[] = [
  {
    id: 1,
    name: '超级管理员',
    code: 'super_admin',
    description: '拥有系统所有权限',
    sort: 1,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 2,
    name: '项目管理员',
    code: 'project_admin',
    description: '维护指定项目资料、凭据、二维码和检测配置',
    sort: 2,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 3,
    name: '部门管理员',
    code: 'dept_admin',
    description: '查看本部门用户和授权项目，协助维护部门项目资料',
    sort: 3,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  },
  {
    id: 4,
    name: '普通员工',
    code: 'employee',
    description: '使用前台项目门户查看授权项目入口和访问说明',
    sort: 4,
    status: 1,
    createTime: '2024-01-01 10:00:00',
    updateTime: '2025-03-01 09:00:00'
  }
]

let nextRoleId = 10

/**
 * 获取角色列表 Mock 函数
 */
export function getRoleListMock(params: any) {
  const { name, status, page = 1, pageSize = 20 } = params || {}
  let filteredData = [...mockRoles]

  // 筛选
  if (name) {
    filteredData = filteredData.filter((role) => role.name.includes(name))
  }
  if (status !== undefined && status !== null && status !== '') {
    const statusValue = typeof status === 'string' ? parseInt(status) : status
    filteredData = filteredData.filter((role) => role.status === statusValue)
  }

  // 分页
  const start = (page - 1) * pageSize
  const end = start + Number(pageSize)
  const list = filteredData.slice(start, end)

  return {
    list,
    total: filteredData.length
  }
}

/**
 * 添加角色 Mock 函数
 */
export function addRoleMock(data: Partial<Role>) {
  const newRole: Role = {
    id: nextRoleId++,
    name: data.name || '',
    code: data.code || '',
    description: data.description || '',
    sort: data.sort || 1,
    status: data.status || 1,
    createTime: new Date().toLocaleString('zh-CN'),
    updateTime: new Date().toLocaleString('zh-CN')
  }
  mockRoles.push(newRole)
  return newRole
}

/**
 * 更新角色 Mock 函数
 */
export function updateRoleMock(id: number, data: Partial<Role>) {
  const index = mockRoles.findIndex((role) => role.id === id)
  if (index !== -1) {
    mockRoles[index] = {
      ...mockRoles[index],
      ...data,
      updateTime: new Date().toLocaleString('zh-CN')
    }
    return true
  }
  return false
}

/**
 * 删除角色 Mock 函数
 */
export function deleteRoleMock(id: number) {
  const index = mockRoles.findIndex((role) => role.id === id)
  if (index !== -1) {
    mockRoles.splice(index, 1)
    return true
  }
  return false
}

/**
 * 更新角色状态 Mock 函数
 */
export function updateRoleStatusMock(id: number, status: number) {
  const index = mockRoles.findIndex((role) => role.id === id)
  if (index !== -1) {
    mockRoles[index].status = status
    mockRoles[index].updateTime = new Date().toLocaleString('zh-CN')
    return true
  }
  return false
}

/**
 * 分配角色权限 Mock 函数
 */
export function assignRolePermissionsMock(id: number, menuIds: number[]) {
  const index = mockRoles.findIndex((role) => role.id === id)
  if (index !== -1) {
    // 这里简化处理，实际应该存储角色和菜单的关联关系
    return true
  }
  return false
}

// ==================== 菜单管理 Mock 函数 ====================

/**
 * 获取菜单列表 Mock 函数
 */
export function getMenuListMock(params: any = {}) {
  return {
    code: 200,
    data: mockMenuData,
    message: '获取成功'
  }
}

/**
 * 添加菜单 Mock 函数
 */
export function addMenuMock(data: Partial<Menu>) {
  return {
    code: 200,
    data: { id: Date.now(), ...data },
    message: '添加成功'
  }
}

/**
 * 更新菜单 Mock 函数
 */
export function updateMenuMock(id: number, data: Partial<Menu>) {
  return {
    code: 200,
    data: null,
    message: '更新成功'
  }
}

/**
 * 删除菜单 Mock 函数
 */
export function deleteMenuMock(id: number) {
  return {
    code: 200,
    data: null,
    message: '删除成功'
  }
}

/**
 * 更新菜单状态 Mock 函数
 */
export function updateMenuStatusMock(id: number, status: number) {
  return {
    code: 200,
    data: null,
    message: '更新状态成功'
  }
}

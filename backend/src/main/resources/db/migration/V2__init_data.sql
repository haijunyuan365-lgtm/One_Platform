INSERT INTO sys_department (id, parent_id, name, code, leader, sort, status)
VALUES
  (1, NULL, '公司总部', 'HQ', '管理员', 1, 1),
  (2, 1, '产品研发中心', 'PRODUCT-RD', '李四', 10, 1),
  (21, 2, '研发部', 'RD', '赵六', 11, 1),
  (22, 2, '产品部', 'PRODUCT', '王五', 12, 1),
  (3, 1, '交付与运营中心', 'DELIVERY-OPS', '周八', 20, 1),
  (31, 3, '交付部', 'DELIVERY', '孙七', 21, 1),
  (32, 3, '运维部', 'OPS', '李四', 22, 1);

INSERT INTO sys_role (id, code, name, description, sort, status, is_system)
VALUES
  (1, 'super_admin', '超级管理员', '平台最高权限角色', 1, 1, 1),
  (2, 'project_admin', '项目管理员', '负责指定项目资料维护', 2, 1, 1),
  (3, 'dept_admin', '部门管理员', '负责本部门授权协调', 3, 1, 1),
  (4, 'employee', '普通员工', '查看授权项目入口', 4, 1, 1),
  (5, 'guest', '访客用户', '查看指定项目基础信息', 5, 1, 1);

INSERT INTO sys_user (id, username, password_hash, real_name, nickname, email, phone, department_id, status)
VALUES
  (1, 'admin', 'INIT_HASH_PENDING', '系统管理员', '管理员', 'admin@one-platform.local', '13800138000', 1, 1),
  (2, 'lisi', 'INIT_HASH_PENDING', '李四', '李四', 'lisi@one-platform.local', '13800138001', 32, 1),
  (3, 'wangwu', 'INIT_HASH_PENDING', '王五', '王五', 'wangwu@one-platform.local', '13800138002', 22, 1),
  (4, 'zhaoliu', 'INIT_HASH_PENDING', '赵六', '赵六', 'zhaoliu@one-platform.local', '13800138003', 21, 1);

INSERT INTO sys_user_role (user_id, role_id)
VALUES
  (1, 1),
  (2, 2),
  (3, 3),
  (4, 4);

INSERT INTO sys_menu (id, parent_id, name, title, type, path, component, permission, icon, sort, status)
VALUES
  (1, NULL, 'SystemManagement', '系统管理', 'directory', '/organization', NULL, 'system:view', 'Setting', 10, 1),
  (11, 1, 'UserManagement', '用户管理', 'menu', '/organization/user', '@/views/organization/user/index.vue', 'organization:user:view', 'User', 11, 1),
  (12, 1, 'DepartmentManagement', '部门管理', 'menu', '/organization/department', '@/views/organization/department/index.vue', 'organization:department:view', 'OfficeBuilding', 12, 1),
  (13, 1, 'RoleManagement', '角色管理', 'menu', '/permission/role', '@/views/permission/role/index.vue', 'permission:role:view', 'UserFilled', 13, 1),
  (2, NULL, 'ProjectManagement', '项目管理', 'directory', '/project', NULL, 'project:view', 'FolderOpened', 20, 1),
  (21, 2, 'ProjectCard', '项目卡片管理', 'menu', '/project/card', '@/views/project/card/index.vue', 'project:card:view', 'Files', 21, 1),
  (22, 2, 'ProjectDetailContent', '详情内容管理', 'menu', '/project/detail-content', '@/views/project/detail-content/index.vue', 'project:detail:view', 'Document', 22, 1),
  (23, 2, 'ProjectCredential', '账号凭据管理', 'menu', '/project/credential', '@/views/project/credential/index.vue', 'project:credential:view', 'Key', 23, 1),
  (24, 2, 'ProjectQrcode', '二维码管理', 'menu', '/project/qrcode', '@/views/project/qrcode/index.vue', 'project:qrcode:view', 'Picture', 24, 1),
  (3, NULL, 'StatusManagement', '状态检测', 'directory', '/status', NULL, 'status:view', 'Monitor', 30, 1),
  (31, 3, 'StatusConfig', '检测配置', 'menu', '/status/config', '@/views/status/config/index.vue', 'status:config:view', 'Tools', 31, 1),
  (32, 3, 'StatusRecord', '检测记录', 'menu', '/status/record', '@/views/status/record/index.vue', 'status:record:view', 'Clock', 32, 1),
  (4, NULL, 'AuditManagement', '权限与审计', 'directory', '/audit', NULL, 'audit:view', 'Operation', 40, 1),
  (41, 4, 'ProjectPermission', '项目权限配置', 'menu', '/audit/project-permission', '@/views/audit/project-permission/index.vue', 'audit:project-permission:view', 'Lock', 41, 1),
  (42, 4, 'OperationLog', '操作日志', 'menu', '/audit/operation-log', '@/views/audit/operation-log/index.vue', 'audit:operation-log:view', 'List', 42, 1);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

INSERT INTO op_project (id, name, short_name, logo_url, category, tags, description, maintainer_id, maintainer_name, sort, enabled, status, last_check_time, response_time, abnormal_reason)
VALUES
  (1, '公司统一门户', '统一门户', '/assets/project/portal.svg', '内部系统', '正式,内网,SSO', '公司内部应用入口，统一展示项目地址、账号资料和运行状态。', 1, '管理员', 1, 1, '可用', '2026-06-05 09:10:00', 126, NULL),
  (2, '客户项目交付看板', '交付看板', '/assets/project/delivery.svg', '客户项目', '正式,外网', '展示客户项目里程碑、风险、问题和验收资料的交付看板。', 3, '王五', 2, 1, '维护中', '2026-06-05 08:50:00', 243, '周五例行发布窗口'),
  (3, 'AI 原型生成平台', 'AI 原型', '/assets/project/ai.svg', 'AI工具', '测试,内网,AI', '为产品经理提供需求文档、原型页面和测试用例的智能生成工具。', 4, '赵六', 3, 1, '可用', '2026-06-05 09:12:00', 98, NULL),
  (4, '经营数据分析平台', '数据分析', '/assets/project/bi.svg', '数据平台', '正式,内网,报表', '集中展示销售、交付、财务和运维指标，支持经营例会复盘。', 2, '李四', 4, 1, '异常', '2026-06-05 08:58:00', 0, 'HTTP 502'),
  (5, '运维监控中心', '监控中心', '/assets/project/ops.svg', '运维服务', '正式,内网,监控', '统一查看服务健康度、告警、CI/CD 和服务器资源。', 2, '李四', 5, 1, '可用', '2026-06-05 09:08:00', 164, NULL);

INSERT INTO op_project_address (id, project_id, name, type, url, is_default, is_detection, sort, status)
VALUES
  (1, 1, '正式地址', 'Web', 'https://portal.example.test', 1, 1, 1, 1),
  (2, 1, '后台地址', '后台', 'https://portal.example.test/admin', 0, 0, 2, 1),
  (3, 2, '正式地址', 'Web', 'https://delivery.example.test', 1, 1, 1, 1),
  (4, 2, '项目文档', '文档', 'https://docs.example.test/delivery', 0, 0, 2, 1),
  (5, 3, '测试地址', 'Web', 'https://prototype.example.test', 1, 1, 1, 1),
  (6, 4, '正式地址', 'Web', 'https://bi.example.test', 1, 1, 1, 1),
  (7, 5, '监控首页', 'Web', 'https://ops.example.test', 1, 1, 1, 1);

INSERT INTO op_project_instruction (project_id, browser_requirement, vpn_requirement, notes, maintainer, contact_phone)
VALUES
  (1, 'Chrome 120+ 或 Edge 120+', '需连接公司 VPN 后访问', '资料由管理员维护，若无法访问请优先联系维护人。', '管理员', '13800138000'),
  (2, 'Chrome 120+ 或 Edge 120+', '外网可访问', '周五发布窗口可能出现维护提示。', '王五', '13800138002'),
  (3, 'Chrome 120+ 或 Edge 120+', '需连接公司 VPN 后访问', '测试环境账号仅供产品与研发体验。', '赵六', '13800138003'),
  (4, 'Chrome 120+ 或 Edge 120+', '需连接公司 VPN 后访问', '经营数据平台异常时请联系运维负责人。', '李四', '13800138001'),
  (5, 'Chrome 120+ 或 Edge 120+', '需连接公司 VPN 后访问', '运维值班系统仅限授权人员使用。', '李四', '13800138001');

INSERT INTO op_project_credential (id, project_id, name, username, password_cipher, password_masked, environment, description, expire_date, status)
VALUES
  (1, 1, '演示账号', 'portal_demo', 'v1:wHHNdw18fLiqfpva:8Qoq8RODFNLlrIi+D+1da6/u0Et51Nk66/N4MK6x', '************', '演示', '门户演示账号，仅用于内部培训。', '2026-12-31', 1),
  (2, 1, '管理员账号', 'portal_admin', 'v1:9xfx1Owl31wFzM6G:8LGB8N4d/rZCurBVUWdBY0aaiBayWbBhAz/MYRIgow==', '************', '正式', '超级管理员仅限平台管理员使用。', '2026-09-30', 1),
  (3, 2, '客户演示账号', 'delivery_demo', 'v1:4ihCGFkPRI/YoM3y:JOQvOnoSxDnyqgdp8k8KgkJxODvZ3e6nKi5VmKqA4SE=', '************', '演示', '客户验收演示账号。', '2026-08-31', 1),
  (4, 3, '测试账号', 'prototype_demo', 'v1:ZKnF3PPhpf6gY+3g:AmMH6JsA5J+w8rieFtdcbi68wiTLL1Igzz6RD4JgDVRZ', '************', '测试', '供产品经理体验 AI 原型生成。', NULL, 1),
  (5, 4, '经营查看账号', 'bi_viewer', 'v1:lQiq86zpseOnrs1L:J62vog64eaNDxCDSWwBGzmra4YiT0Hdy9tnqbw==', '************', '正式', '经营层查看账号。', NULL, 1),
  (6, 5, '监控查看账号', 'ops_viewer', 'v1:9VeadU+X2fQYCVP0:ZzxD9vnVVuAAi6ZxgxgAt/kvuIr2ae9D1j7Db+o=', '************', '正式', '查看服务状态与告警。', NULL, 1);

INSERT INTO op_project_qrcode (id, project_id, name, image_url, audience, description, status)
VALUES
  (1, 1, '门户移动入口', '/assets/qrcode/portal.png', '全员', '移动端快速访问入口。', 1),
  (2, 2, '客户验收入口', '/assets/qrcode/delivery.png', '项目干系人', '客户项目验收用。', 1),
  (3, 3, 'AI工具入口', '/assets/qrcode/ai.png', '产品与研发', '扫码进入 AI 原型工具。', 1),
  (4, 4, '经营日报入口', '/assets/qrcode/bi.png', '经营层', '移动端查看日报摘要。', 1),
  (5, 5, '告警订阅', '/assets/qrcode/ops.png', '运维值班', '订阅告警通知。', 1);

INSERT INTO op_project_permission (project_id, target_type, target_id, target_name, visible, password_view, password_copy, qrcode_view, status)
VALUES
  (1, '角色', 1, '超级管理员', 1, 1, 1, 1, 1),
  (1, '部门', 21, '产品研发中心/研发部', 1, 1, 0, 1, 1),
  (2, '用户', 3, '王五', 1, 1, 1, 1, 1),
  (3, '角色', 4, '普通员工', 1, 0, 0, 1, 1),
  (4, '部门', 31, '交付与运营中心/交付部', 1, 1, 0, 1, 1),
  (5, '用户', 2, '李四', 1, 1, 1, 1, 1);

INSERT INTO op_status_config (project_id, auto_enabled, method, check_address, timeout_seconds, frequency_minutes, expected_status_codes, manual_status, current_status, last_check_time, response_time, abnormal_reason)
SELECT p.id, 1, 'HTTP/HTTPS', a.url, 5, 5, '200,302,401,403', '无覆盖', p.status, p.last_check_time, p.response_time, p.abnormal_reason
FROM op_project p
LEFT JOIN op_project_address a ON a.id = (
  SELECT MIN(pa.id)
  FROM op_project_address pa
  WHERE pa.project_id = p.id
    AND pa.is_detection = 1
    AND pa.status = 1
    AND pa.deleted = 0
);

CREATE TABLE sys_department (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NULL,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(50) NULL,
  leader VARCHAR(50) NULL,
  phone VARCHAR(20) NULL,
  sort INT NOT NULL DEFAULT 100,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  INDEX idx_sys_department_parent (parent_id),
  INDEX idx_sys_department_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(50) NULL,
  real_name VARCHAR(50) NOT NULL,
  avatar VARCHAR(500) NULL,
  email VARCHAR(100) NULL,
  phone VARCHAR(20) NULL,
  department_id BIGINT NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  last_login_time DATETIME NULL,
  last_login_ip VARCHAR(64) NULL,
  failed_login_count INT NOT NULL DEFAULT 0,
  locked_until DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  UNIQUE KEY uk_sys_user_username (username),
  INDEX idx_sys_user_department (department_id),
  INDEX idx_sys_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(300) NULL,
  sort INT NOT NULL DEFAULT 100,
  status TINYINT NOT NULL DEFAULT 1,
  is_system TINYINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  UNIQUE KEY uk_sys_role_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NULL,
  name VARCHAR(100) NOT NULL,
  title VARCHAR(100) NOT NULL,
  type VARCHAR(20) NOT NULL,
  path VARCHAR(200) NULL,
  component VARCHAR(200) NULL,
  permission VARCHAR(100) NULL,
  icon VARCHAR(100) NULL,
  sort INT NOT NULL DEFAULT 100,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  INDEX idx_sys_menu_parent (parent_id),
  INDEX idx_sys_menu_permission (permission)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  UNIQUE KEY uk_sys_user_role (user_id, role_id),
  INDEX idx_sys_user_role_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_role_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  UNIQUE KEY uk_sys_role_menu (role_id, menu_id),
  INDEX idx_sys_role_menu_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_file (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  origin_name VARCHAR(255) NOT NULL,
  storage_name VARCHAR(255) NOT NULL,
  storage_path VARCHAR(500) NOT NULL,
  url VARCHAR(500) NOT NULL,
  content_type VARCHAR(100) NOT NULL,
  size BIGINT NOT NULL,
  biz_type VARCHAR(50) NOT NULL,
  uploader_id BIGINT NOT NULL,
  uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  INDEX idx_sys_file_biz_type (biz_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_project (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  short_name VARCHAR(30) NULL,
  logo_file_id BIGINT NULL,
  logo_url VARCHAR(500) NULL,
  category VARCHAR(30) NOT NULL,
  tags VARCHAR(500) NULL,
  description VARCHAR(300) NULL,
  maintainer_id BIGINT NULL,
  maintainer_name VARCHAR(50) NULL,
  sort INT NOT NULL DEFAULT 100,
  enabled TINYINT NOT NULL DEFAULT 1,
  status VARCHAR(20) NOT NULL DEFAULT '未检测',
  last_check_time DATETIME NULL,
  response_time INT NULL,
  abnormal_reason VARCHAR(500) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  UNIQUE KEY uk_op_project_name (name),
  INDEX idx_op_project_category (category),
  INDEX idx_op_project_enabled (enabled),
  INDEX idx_op_project_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_project_address (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  type VARCHAR(20) NOT NULL,
  url VARCHAR(1000) NOT NULL,
  is_default TINYINT NOT NULL DEFAULT 0,
  is_detection TINYINT NOT NULL DEFAULT 0,
  sort INT NOT NULL DEFAULT 100,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  INDEX idx_project_address_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_project_instruction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  browser_requirement VARCHAR(500) NULL,
  vpn_requirement VARCHAR(500) NULL,
  notes TEXT NULL,
  maintainer VARCHAR(50) NULL,
  contact_phone VARCHAR(20) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  UNIQUE KEY uk_project_instruction_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_project_credential (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  username VARCHAR(100) NOT NULL,
  password_cipher TEXT NOT NULL,
  password_masked VARCHAR(100) NOT NULL,
  environment VARCHAR(20) NOT NULL,
  description VARCHAR(500) NULL,
  expire_date DATE NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  INDEX idx_credential_project (project_id),
  INDEX idx_credential_environment (environment),
  INDEX idx_credential_expire (expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_project_qrcode (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  file_id BIGINT NULL,
  image_url VARCHAR(500) NOT NULL,
  audience VARCHAR(100) NULL,
  description VARCHAR(500) NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  INDEX idx_project_qrcode_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_project_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  target_type VARCHAR(20) NOT NULL,
  target_id BIGINT NOT NULL,
  target_name VARCHAR(100) NOT NULL,
  visible TINYINT NOT NULL DEFAULT 1,
  password_view TINYINT NOT NULL DEFAULT 0,
  password_copy TINYINT NOT NULL DEFAULT 0,
  qrcode_view TINYINT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  UNIQUE KEY uk_project_permission (project_id, target_type, target_id),
  INDEX idx_project_permission_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_project_favorite (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  project_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_project_favorite (user_id, project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_project_recent_visit (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  project_id BIGINT NOT NULL,
  visit_count INT NOT NULL DEFAULT 1,
  last_visit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_project_recent_visit (user_id, project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_status_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  auto_enabled TINYINT NOT NULL DEFAULT 1,
  method VARCHAR(20) NOT NULL DEFAULT 'HTTP/HTTPS',
  check_address VARCHAR(1000) NULL,
  check_port INT NULL,
  timeout_seconds INT NOT NULL DEFAULT 5,
  frequency_minutes INT NOT NULL DEFAULT 5,
  expected_status_codes VARCHAR(100) NULL,
  manual_status VARCHAR(20) NOT NULL DEFAULT '无覆盖',
  manual_reason VARCHAR(500) NULL,
  manual_expire_at DATETIME NULL,
  current_status VARCHAR(20) NOT NULL DEFAULT '未检测',
  last_check_time DATETIME NULL,
  response_time INT NULL,
  abnormal_reason VARCHAR(500) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  updated_by BIGINT NULL,
  deleted TINYINT NOT NULL DEFAULT 0,
  remark VARCHAR(500) NULL,
  UNIQUE KEY uk_status_config_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_status_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  method VARCHAR(20) NOT NULL,
  check_address VARCHAR(1000) NOT NULL,
  result VARCHAR(20) NOT NULL,
  response_time INT NOT NULL DEFAULT 0,
  error_reason VARCHAR(500) NULL,
  check_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  operator VARCHAR(50) NOT NULL,
  INDEX idx_status_record_project_time (project_id, check_time),
  INDEX idx_status_record_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE op_operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  operate_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  operator_id BIGINT NULL,
  operator VARCHAR(50) NULL,
  account VARCHAR(50) NULL,
  department_id BIGINT NULL,
  department VARCHAR(100) NULL,
  project_id BIGINT NULL,
  project_name VARCHAR(100) NULL,
  operation_type VARCHAR(50) NOT NULL,
  target VARCHAR(200) NULL,
  result VARCHAR(20) NOT NULL,
  ip VARCHAR(64) NULL,
  device VARCHAR(300) NULL,
  remark VARCHAR(1000) NULL,
  INDEX idx_operation_log_time (operate_time),
  INDEX idx_operation_log_operator (operator_id),
  INDEX idx_operation_log_project (project_id),
  INDEX idx_operation_log_type (operation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

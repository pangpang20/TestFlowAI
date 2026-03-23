-- TestFlowAI MySQL 数据库初始化脚本
-- 自动创建所有表结构和初始数据

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 用户相关表
-- ============================================

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    user_id VARCHAR(36) PRIMARY KEY COMMENT '用户 ID（UUID）',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    avatar VARCHAR(500) COMMENT '头像 URL',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态（active/disabled）',
    last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者',
    INDEX idx_user_username (username),
    INDEX idx_user_email (email),
    INDEX idx_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS t_role (
    role_id VARCHAR(36) PRIMARY KEY COMMENT '角色 ID（UUID）',
    role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
    display_name VARCHAR(100) NOT NULL COMMENT '显示名称',
    description VARCHAR(200) COMMENT '描述',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS t_permission (
    permission_id VARCHAR(36) PRIMARY KEY COMMENT '权限 ID（UUID）',
    permission_code VARCHAR(100) UNIQUE NOT NULL COMMENT '权限代码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    resource VARCHAR(50) NOT NULL COMMENT '资源类型',
    action VARCHAR(20) NOT NULL COMMENT '操作类型',
    description VARCHAR(200) COMMENT '描述',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS t_user_role (
    user_id VARCHAR(36) NOT NULL COMMENT '用户 ID',
    role_id VARCHAR(36) NOT NULL COMMENT '角色 ID',
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES t_user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES t_role(role_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS t_role_permission (
    role_id VARCHAR(36) NOT NULL COMMENT '角色 ID',
    permission_id VARCHAR(36) NOT NULL COMMENT '权限 ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者',
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES t_role(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES t_permission(permission_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 角色继承表
CREATE TABLE IF NOT EXISTS t_role_inheritance (
    child_role_id VARCHAR(36) NOT NULL COMMENT '子角色 ID',
    parent_role_id VARCHAR(36) NOT NULL COMMENT '父角色 ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者',
    PRIMARY KEY (child_role_id, parent_role_id),
    FOREIGN KEY (child_role_id) REFERENCES t_role(role_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_role_id) REFERENCES t_role(role_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色继承表';

-- 审计日志表
CREATE TABLE IF NOT EXISTS t_audit_log (
    log_id VARCHAR(36) PRIMARY KEY COMMENT '日志 ID（UUID）',
    user_id VARCHAR(36) NOT NULL COMMENT '用户 ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    operation VARCHAR(50) NOT NULL COMMENT '操作类型',
    resource VARCHAR(50) COMMENT '资源类型',
    resource_id VARCHAR(36) COMMENT '资源 ID',
    details TEXT COMMENT '操作详情（JSON）',
    ip_address VARCHAR(50) COMMENT 'IP 地址',
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    result VARCHAR(20) NOT NULL COMMENT '操作结果（success/failure）',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者',
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_operation (operation),
    INDEX idx_audit_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- ============================================
-- 2. 测试流相关表
-- ============================================

-- 测试流表
CREATE TABLE IF NOT EXISTS t_testflow (
    test_id VARCHAR(100) PRIMARY KEY COMMENT '测试流 ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    version VARCHAR(20) NOT NULL COMMENT '版本号',
    app_url VARCHAR(500) COMMENT '应用 URL',
    steps TEXT NOT NULL COMMENT '步骤 JSON',
    variables TEXT COMMENT '变量 JSON',
    tags TEXT COMMENT '标签 JSON',
    expected_report TEXT COMMENT '预期报告 JSON',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者',
    INDEX idx_testflow_title (title),
    INDEX idx_testflow_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试流表';

-- 测试流版本表
CREATE TABLE IF NOT EXISTS t_testflow_version (
    version_id VARCHAR(36) PRIMARY KEY COMMENT '版本 ID（UUID）',
    test_id VARCHAR(100) NOT NULL COMMENT '测试流 ID',
    version VARCHAR(20) NOT NULL COMMENT '版本号',
    steps TEXT NOT NULL COMMENT '步骤 JSON',
    variables TEXT COMMENT '变量 JSON',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者',
    INDEX idx_version_test (test_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试流版本表';

-- ============================================
-- 3. 执行相关表
-- ============================================

-- 执行记录表
CREATE TABLE IF NOT EXISTS t_execution (
    execution_id VARCHAR(36) PRIMARY KEY COMMENT '执行 ID（UUID）',
    test_id VARCHAR(100) NOT NULL COMMENT '测试流 ID',
    mode VARCHAR(20) NOT NULL COMMENT '执行模式',
    status VARCHAR(20) NOT NULL COMMENT '执行状态',
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP NULL COMMENT '结束时间',
    total_steps INT NOT NULL DEFAULT 0 COMMENT '总步骤数',
    passed_steps INT NOT NULL DEFAULT 0 COMMENT '通过步骤数',
    failed_steps INT NOT NULL DEFAULT 0 COMMENT '失败步骤数',
    input TEXT COMMENT '执行输入 JSON',
    output TEXT COMMENT '执行输出 JSON',
    step_results TEXT COMMENT '步骤结果 JSON',
    screenshots TEXT COMMENT '截图路径 JSON',
    loop_context TEXT COMMENT '循环上下文 JSON',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者',
    INDEX idx_execution_test (test_id),
    INDEX idx_execution_status (status),
    INDEX idx_execution_start (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='执行记录表';

-- 步骤执行结果表
CREATE TABLE IF NOT EXISTS t_step_result (
    result_id VARCHAR(36) PRIMARY KEY COMMENT '结果 ID（UUID）',
    execution_id VARCHAR(36) NOT NULL COMMENT '执行 ID',
    step_id INT NOT NULL COMMENT '步骤 ID',
    status VARCHAR(20) NOT NULL COMMENT '状态',
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP NULL COMMENT '结束时间',
    error_message TEXT COMMENT '错误信息',
    screenshot VARCHAR(500) COMMENT '截图路径',
    log TEXT COMMENT '日志',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者',
    INDEX idx_step_execution (execution_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='步骤执行结果表';

-- ============================================
-- 4. 报告相关表
-- ============================================

-- 测试报告表
CREATE TABLE IF NOT EXISTS t_report (
    report_id VARCHAR(36) PRIMARY KEY COMMENT '报告 ID（UUID）',
    execution_id VARCHAR(36) NOT NULL COMMENT '执行 ID',
    test_id VARCHAR(100) NOT NULL COMMENT '测试流 ID',
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    summary TEXT NOT NULL COMMENT '摘要 JSON',
    details TEXT NOT NULL COMMENT '详情 JSON',
    comparisons TEXT COMMENT '对比数据 JSON',
    format VARCHAR(20) NOT NULL COMMENT '格式',
    file_path VARCHAR(500) COMMENT '文件路径',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at TIMESTAMP NULL COMMENT '删除时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记（1-删除，0-未删除）',
    created_by VARCHAR(36) COMMENT '创建者',
    updated_by VARCHAR(36) COMMENT '更新者',
    deleted_by VARCHAR(36) COMMENT '删除者',
    INDEX idx_report_execution (execution_id),
    INDEX idx_report_test (test_id),
    INDEX idx_report_generated (generated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试报告表';

-- ============================================
-- 5. 初始化数据
-- ============================================

-- 插入默认角色
INSERT INTO t_role (role_id, role_name, display_name, description, deleted) VALUES
('role-admin-001', 'admin', '管理员', '系统管理员，拥有所有权限', 0),
('role-engineer-002', 'test_engineer', '测试工程师', '测试工程师，可创建和执行测试', 0),
('role-viewer-003', 'viewer', '只读用户', '只读用户，仅可查看', 0);

-- 插入默认权限
INSERT INTO t_permission (permission_id, permission_code, permission_name, resource, action, description, deleted) VALUES
('perm-tf-create-001', 'testflow:create', '创建测试流', 'testflow', 'create', '创建新的测试流', 0),
('perm-tf-read-002', 'testflow:read', '查看测试流', 'testflow', 'read', '查看测试流列表和详情', 0),
('perm-tf-update-003', 'testflow:update', '更新测试流', 'testflow', 'update', '更新测试流', 0),
('perm-tf-delete-004', 'testflow:delete', '删除测试流', 'testflow', 'delete', '删除测试流', 0),
('perm-exec-execute-005', 'execution:execute', '执行测试', 'execution', 'execute', '执行测试流', 0),
('perm-exec-read-006', 'execution:read', '查看执行结果', 'execution', 'read', '查看执行历史和结果', 0),
('perm-report-read-007', 'report:read', '查看报告', 'report', 'read', '查看测试报告', 0),
('perm-user-create-008', 'user:create', '创建用户', 'user', 'create', '创建新用户', 0),
('perm-user-read-009', 'user:read', '查看用户', 'user', 'read', '查看用户列表', 0),
('perm-user-update-010', 'user:update', '更新用户', 'user', 'update', '更新用户信息', 0),
('perm-user-delete-011', 'user:delete', '删除用户', 'user', 'delete', '删除用户', 0),
('perm-role-read-012', 'role:read', '查看角色', 'role', 'read', '查看角色列表', 0),
('perm-perm-read-013', 'permission:read', '查看权限', 'permission', 'read', '查看权限列表', 0),
('perm-audit-read-014', 'audit:read', '查看审计日志', 'audit', 'read', '查看审计日志', 0);

-- 管理员拥有所有权限
INSERT INTO t_role_permission (role_id, permission_id, deleted) VALUES
('role-admin-001', 'perm-tf-create-001', 0),
('role-admin-001', 'perm-tf-read-002', 0),
('role-admin-001', 'perm-tf-update-003', 0),
('role-admin-001', 'perm-tf-delete-004', 0),
('role-admin-001', 'perm-exec-execute-005', 0),
('role-admin-001', 'perm-exec-read-006', 0),
('role-admin-001', 'perm-report-read-007', 0),
('role-admin-001', 'perm-user-create-008', 0),
('role-admin-001', 'perm-user-read-009', 0),
('role-admin-001', 'perm-user-update-010', 0),
('role-admin-001', 'perm-user-delete-011', 0),
('role-admin-001', 'perm-role-read-012', 0),
('role-admin-001', 'perm-perm-read-013', 0),
('role-admin-001', 'perm-audit-read-014', 0);

-- 测试工程师拥有测试相关权限
INSERT INTO t_role_permission (role_id, permission_id, deleted) VALUES
('role-engineer-002', 'perm-tf-create-001', 0),
('role-engineer-002', 'perm-tf-read-002', 0),
('role-engineer-002', 'perm-tf-update-003', 0),
('role-engineer-002', 'perm-exec-execute-005', 0),
('role-engineer-002', 'perm-exec-read-006', 0),
('role-engineer-002', 'perm-report-read-007', 0);

-- 只读用户仅有查看权限
INSERT INTO t_role_permission (role_id, permission_id, deleted) VALUES
('role-viewer-003', 'perm-tf-read-002', 0),
('role-viewer-003', 'perm-exec-read-006', 0),
('role-viewer-003', 'perm-report-read-007', 0);

-- 插入默认管理员账户（密码为 admin123，BCrypt 加密）
INSERT INTO t_user (user_id, username, password, email, status, deleted) VALUES
('user-admin-001', 'admin', '$2b$10$uh2N2o0wbkBCGykMI4G9puDq9GqTBcmfftF2myIWkuPCLZiTyYST.', 'admin@testflowai.com', 'active', 0);

-- 分配管理员角色给默认管理员
INSERT INTO t_user_role (user_id, role_id, deleted) VALUES
('user-admin-001', 'role-admin-001', 0);

SET FOREIGN_KEY_CHECKS = 1;

-- 添加角色权限管理表

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 权限表
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource VARCHAR(100) NOT NULL UNIQUE COMMENT '资源标识，如 post:read, post:write',
    description VARCHAR(200) NOT NULL,
    permission_group VARCHAR(64) COMMENT '权限分组',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_resource (resource),
    INDEX idx_group (permission_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(36) NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(36),
    PRIMARY KEY (user_id, role_id),
    INDEX idx_user (user_id),
    INDEX idx_role (role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id),
    INDEX idx_role (role_id),
    INDEX idx_permission (permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认角色
INSERT INTO roles (code, name, description) VALUES
('ADMIN', '管理员', '系统管理员，拥有所有权限'),
('EDITOR', '编辑', '内容编辑，可以管理所有文章'),
('AUTHOR', '作者', '内容作者，可以创建和管理自己的文章'),
('READER', '读者', '普通读者，只能阅读公开内容');

-- 插入默认权限
INSERT INTO permissions (resource, description, permission_group) VALUES
-- 文章权限
('post:read', '读取文章', 'post'),
('post:create', '创建文章', 'post'),
('post:update', '更新文章', 'post'),
('post:delete', '删除文章', 'post'),
('post:publish', '发布文章', 'post'),
-- 分类权限
('category:read', '读取分类', 'category'),
('category:manage', '管理分类', 'category'),
-- 用户权限
('user:read', '读取用户信息', 'user'),
('user:manage', '管理用户', 'user'),
-- 角色权限
('role:read', '读取角色', 'role'),
('role:manage', '管理角色', 'role'),
-- 系统权限
('system:config', '系统配置', 'system');

-- 为默认角色分配权限
-- ADMIN 拥有所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p WHERE r.code = 'ADMIN';

-- EDITOR 拥有内容管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.code = 'EDITOR' AND p.resource IN ('post:read', 'post:create', 'post:update', 'post:delete', 'post:publish', 'category:read', 'category:manage');

-- AUTHOR 拥有基础内容权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.code = 'AUTHOR' AND p.resource IN ('post:read', 'post:create', 'post:update', 'category:read');

-- READER 只有读取权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.code = 'READER' AND p.resource IN ('post:read', 'category:read');

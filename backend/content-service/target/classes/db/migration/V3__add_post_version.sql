-- 添加文章版本控制表

CREATE TABLE IF NOT EXISTS post_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL COMMENT '文章ID',
    version_number INT NOT NULL COMMENT '版本号',
    content_snapshot LONGTEXT COMMENT '内容快照（完整内容）',
    content_diff TEXT COMMENT '内容差异（相对于上一版本）',
    version_comment VARCHAR(500) COMMENT '版本说明',
    created_by VARCHAR(36) COMMENT '创建人',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_post (post_id),
    INDEX idx_version (post_id, version_number),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    UNIQUE KEY uk_post_version (post_id, version_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

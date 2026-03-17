-- 二维码记录表
CREATE TABLE IF NOT EXISTS qrcode_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '二维码ID',
    content VARCHAR(500) NOT NULL COMMENT '二维码内容',
    url VARCHAR(500) COMMENT '二维码URL',
    image_path VARCHAR(500) NOT NULL COMMENT '二维码图片路径',
    size_mm INT DEFAULT 10 COMMENT '二维码尺寸（毫米/行）',
    format VARCHAR(20) DEFAULT 'jpg' COMMENT '二维码格式',
    is_black_white BOOLEAN DEFAULT TRUE COMMENT '是否为黑白二维码',
    anti_fake_version INT DEFAULT 0 COMMENT '防伪特征版本',
    anti_fake_data LONGTEXT COMMENT '防伪特征数据',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    INDEX idx_content (content),
    INDEX idx_url (url),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='二维码记录表';

-- 验证记录表
CREATE TABLE IF NOT EXISTS verification_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '验证记录ID',
    qrcode_id BIGINT NOT NULL COMMENT '二维码ID',
    verify_result INT DEFAULT 0 COMMENT '验证结果：0-正品 1-复印 2-真伪度',
    verify_result_desc VARCHAR(100) COMMENT '验证结果描述',
    authenticity_score DOUBLE COMMENT '真伪度百分比',
    status INT DEFAULT 0 COMMENT '验证状态：0-待验证 1-已验证 2-验证失败',
    verify_method INT DEFAULT 0 COMMENT '验证方式：0-手机拍照 1-API直接验证',
    verify_ip VARCHAR(50) COMMENT '验证IP地址',
    device_info VARCHAR(500) COMMENT '设备信息',
    verify_detail LONGTEXT COMMENT '验证详情',
    verify_time DATETIME COMMENT '验证时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    FOREIGN KEY (qrcode_id) REFERENCES qrcode_record(id) ON DELETE CASCADE,
    INDEX idx_qrcode_id (qrcode_id),
    INDEX idx_verify_result (verify_result),
    INDEX idx_verify_time (verify_time),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证记录表';

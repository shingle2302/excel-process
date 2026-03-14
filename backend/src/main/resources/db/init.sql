CREATE DATABASE IF NOT EXISTS excel_process CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE excel_process;

-- 客户端表
CREATE TABLE IF NOT EXISTS client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(100) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    client_name VARCHAR(100) NOT NULL,
    client_desc VARCHAR(500),
    status VARCHAR(20) DEFAULT '启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 任务定义表
CREATE TABLE IF NOT EXISTS task_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    type VARCHAR(20) NOT NULL,
    client_id BIGINT,
    params TEXT,
    callback_url VARCHAR(500),
    callback_method VARCHAR(10),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id)
);

-- 任务表
CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_definition_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT '待处理',
    file_path VARCHAR(500),
    params TEXT,
    result TEXT,
    error_message TEXT,
    progress INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    start_time DATETIME,
    end_time DATETIME,
    FOREIGN KEY (task_definition_id) REFERENCES task_definition(id)
);

-- 数据列定义表
CREATE TABLE IF NOT EXISTS column_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_definition_id BIGINT,
    field_name VARCHAR(100) NOT NULL,
    column_name VARCHAR(100) NOT NULL,
    column_type VARCHAR(20) NOT NULL,
    column_format VARCHAR(100),
    description VARCHAR(500),
    validation_rules TEXT,
    default_value VARCHAR(255),
    mapping_rules TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (task_definition_id) REFERENCES task_definition(id)
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    client_id BIGINT,
    operation_type VARCHAR(50) NOT NULL,
    operation_detail TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id)
);

-- 插入测试数据
INSERT INTO client (client_id, client_secret, client_name, client_desc, status) VALUES
('test-client', 'test-secret', '测试客户端', '用于测试的客户端', '启用');

INSERT INTO task_definition (name, description, type, client_id, params, callback_url, callback_method) VALUES
('export-users', '导出用户数据', '导出', 1, '{"batch": true, "batchSize": 10000, "format": "excel", "pageSize": 10000}', 'http://localhost:8080/callback', 'POST'),
('import-users', '导入用户数据', '导入', 1, '{"mode": "append"}', 'http://localhost:8080/callback', 'POST');

INSERT INTO column_definition (task_definition_id, field_name, column_name, column_type, column_format, description) VALUES
(1, 'id', 'ID', 'number', '0', '用户ID'),
(1, 'username', '用户名', 'string', '', '用户名称'),
(1, 'email', '邮箱', 'string', '', '用户邮箱'),
(1, 'createTime', '创建时间', 'date', 'yyyy-MM-dd HH:mm:ss', '用户创建时间'),
(2, 'id', 'ID', 'number', '0', '用户ID'),
(2, 'username', '用户名', 'string', '', '用户名称'),
(2, 'email', '邮箱', 'string', '', '用户邮箱'),
(2, 'createTime', '创建时间', 'date', 'yyyy-MM-dd HH:mm:ss', '用户创建时间');
CREATE DATABASE IF NOT EXISTS excel_process CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE excel_process;

-- 客户端表
CREATE TABLE IF NOT EXISTS client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(100) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    client_name VARCHAR(100) NOT NULL,
    client_desc VARCHAR(500),
    status VARCHAR(20) DEFAULT '启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 任务定义表
CREATE TABLE IF NOT EXISTS task_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    type VARCHAR(20) NOT NULL,
    client_id BIGINT,
    params TEXT,
    callback_url VARCHAR(500),
    callback_method VARCHAR(10),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id)
);

-- 任务表
CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_definition_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT '待处理',
    file_path VARCHAR(500),
    params TEXT,
    result TEXT,
    error_message TEXT,
    progress INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    start_time DATETIME,
    end_time DATETIME,
    FOREIGN KEY (task_definition_id) REFERENCES task_definition(id)
);

-- 数据列定义表
CREATE TABLE IF NOT EXISTS column_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_definition_id BIGINT,
    field_name VARCHAR(100) NOT NULL,
    column_name VARCHAR(100) NOT NULL,
    column_type VARCHAR(20) NOT NULL,
    column_format VARCHAR(100),
    description VARCHAR(500),
    validation_rules TEXT,
    default_value VARCHAR(255),
    mapping_rules TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (task_definition_id) REFERENCES task_definition(id)
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    client_id BIGINT,
    operation_type VARCHAR(50) NOT NULL,
    operation_detail TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id)
);

-- 插入测试数据
INSERT INTO client (client_id, client_secret, client_name, client_desc, status) VALUES
('test-client', 'test-secret', '测试客户端', '用于测试的客户端', '启用');

INSERT INTO task_definition (name, description, type, client_id, params, callback_url, callback_method) VALUES
('export-users', '导出用户数据', '导出', 1, '{"batch": true, "batchSize": 10000, "format": "excel", "pageSize": 10000}', 'http://localhost:8080/callback', 'POST'),
('import-users', '导入用户数据', '导入', 1, '{"mode": "append"}', 'http://localhost:8080/callback', 'POST');

INSERT INTO column_definition (task_definition_id, field_name, column_name, column_type, column_format, description) VALUES
(1, 'id', 'ID', 'number', '0', '用户ID'),
(1, 'username', '用户名', 'string', '', '用户名称'),
(1, 'email', '邮箱', 'string', '', '用户邮箱'),
(1, 'createTime', '创建时间', 'date', 'yyyy-MM-dd HH:mm:ss', '用户创建时间'),
(2, 'id', 'ID', 'number', '0', '用户ID'),
(2, 'username', '用户名', 'string', '', '用户名称'),
(2, 'email', '邮箱', 'string', '', '用户邮箱'),
(2, 'createTime', '创建时间', 'date', 'yyyy-MM-dd HH:mm:ss', '用户创建时间');
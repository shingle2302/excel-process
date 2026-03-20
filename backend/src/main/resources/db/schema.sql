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

-- 管理端用户表
CREATE TABLE IF NOT EXISTS user_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    role_code VARCHAR(30) NOT NULL DEFAULT 'ADMIN',
    status VARCHAR(20) NOT NULL DEFAULT '启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 数据源配置表
CREATE TABLE IF NOT EXISTS data_source_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    connection_config TEXT,
    description VARCHAR(500),
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
    data_fetch_type VARCHAR(20) DEFAULT 'sql',
    data_source_id BIGINT,
    query_sql TEXT,
    http_method VARCHAR(10),
    http_url VARCHAR(500),
    http_need_auth BOOLEAN DEFAULT FALSE,
    auth_url VARCHAR(500),
    auth_params TEXT,
    request_params TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id),
    FOREIGN KEY (data_source_id) REFERENCES data_source_config(id)
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
    data_fetch_type VARCHAR(20),
    data_source_id BIGINT,
    query_sql TEXT,
    http_method VARCHAR(10),
    http_url VARCHAR(500),
    http_need_auth BOOLEAN DEFAULT FALSE,
    auth_url VARCHAR(500),
    auth_params TEXT,
    request_params TEXT,
    params TEXT,
    result TEXT,
    error_message TEXT,
    progress INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    start_time DATETIME,
    end_time DATETIME,
    FOREIGN KEY (task_definition_id) REFERENCES task_definition(id),
    FOREIGN KEY (data_source_id) REFERENCES data_source_config(id)
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

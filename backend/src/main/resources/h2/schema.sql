-- 客户端表
CREATE TABLE IF NOT EXISTS client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(100) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    client_name VARCHAR(100) NOT NULL,
    client_desc VARCHAR(500),
    status VARCHAR(20) DEFAULT '启用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
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
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
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
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (task_definition_id) REFERENCES task_definition(id)
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    client_id BIGINT,
    operation_type VARCHAR(50) NOT NULL,
    operation_detail TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id)
);
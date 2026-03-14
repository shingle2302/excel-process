-- 插入测试数据
INSERT INTO client (client_id, client_secret, client_name, client_desc, status) VALUES ('test-client', 'test-secret', '测试客户端', '用于测试的客户端', '启用');

INSERT INTO client (client_id, client_secret, client_name, client_desc, status) VALUES ('admin', 'admin123', '系统管理员', '系统默认管理员账号', '启用');

INSERT INTO task_definition (name, description, type, client_id, params, callback_url, callback_method) VALUES ('export-users', '导出用户数据', '导出', 1, '{"batch": true, "batchSize": 10000, "format": "excel", "pageSize": 10000}', 'http://localhost:8080/callback', 'POST');

INSERT INTO task_definition (name, description, type, client_id, params, callback_url, callback_method) VALUES ('import-users', '导入用户数据', '导入', 1, '{"mode": "append"}', 'http://localhost:8080/callback', 'POST');

INSERT INTO column_definition (task_definition_id, field_name, column_name, column_type, column_format, description) VALUES (1, 'id', 'ID', 'number', '0', '用户ID');

INSERT INTO column_definition (task_definition_id, field_name, column_name, column_type, column_format, description) VALUES (1, 'username', '用户名', 'string', '', '用户名称');

INSERT INTO column_definition (task_definition_id, field_name, column_name, column_type, column_format, description) VALUES (1, 'email', '邮箱', 'string', '', '用户邮箱');

INSERT INTO column_definition (task_definition_id, field_name, column_name, column_type, column_format, description) VALUES (1, 'createTime', '创建时间', 'date', 'yyyy-MM-dd HH:mm:ss', '用户创建时间');

INSERT INTO column_definition (task_definition_id, field_name, column_name, column_type, column_format, description) VALUES (2, 'id', 'ID', 'number', '0', '用户ID');

INSERT INTO column_definition (task_definition_id, field_name, column_name, column_type, column_format, description) VALUES (2, 'username', '用户名', 'string', '', '用户名称');

INSERT INTO column_definition (task_definition_id, field_name, column_name, column_type, column_format, description) VALUES (2, 'email', '邮箱', 'string', '', '用户邮箱');

INSERT INTO column_definition (task_definition_id, field_name, column_name, column_type, column_format, description) VALUES (2, 'createTime', '创建时间', 'date', 'yyyy-MM-dd HH:mm:ss', '用户创建时间');
# Excel处理服务

基于Spring Boot的微服务，提供Excel导入/导出功能，支持任务管理、任务定义、客户端、数据源和列定义管理。

## 系统定位

面向「可配置、可追踪、可扩展」的数据任务运营后台，统一管理Excel导入/导出任务。

## 用户角色

| 角色 | 说明 |
|------|------|
| 平台管理员 | Web管理端维护客户端、任务定义、数据源、列定义 |
| 外部系统 | 通过API创建任务，使用clientId+clientSecret认证 |

## 功能模块

| 模块 | 说明 |
|------|------|
| 首页总览 | 运营指标卡片、任务健康度统计、快捷入口 |
| 任务管理 | 列表查询、状态筛选、详情查看、进度更新、删除 |
| 任务定义 | 导入/导出模板，支持SQL/HTTP数据获取方式、回调配置 |
| 客户端管理 | CRUD、启停控制、API Key认证 |
| 数据源管理 | 连接配置JSON维护、Socket连通性测试 |
| 列定义管理 | 字段映射（字段名、列名、类型、格式） |
| Excel导入 | 解析.xlsx/.xls/.csv，返回JSON结构数据 |
| Excel导出 | 按列定义生成Excel文件下载 |

## 技术栈

- Java 17 / Spring Boot 3 / Spring Security
- MyBatis Plus 3.5 / MySQL 8+
- EasyExcel (Excel处理)
- Redis (缓存)
- MinIO (文件存储)

## 快速开始

```bash
# 1. 创建数据库
excel_process

# 2. 初始化表结构
mysql -u root -p excel_process < src/main/resources/db/schema.sql

# 3. 修改配置
vim src/main/resources/application.yml

# 4. 编译运行
mvn clean package -DskipTests
java -jar target/excel-process-service-1.0.0.jar
```

## 核心流程

### 后台创建任务
1. 传入任务数据+API Key → 2. 校验客户端启用状态 → 3. 合并任务定义配置 → 4. 初始化为"待处理"

### 外部创建任务
1. 调用/api/tasks/external → 2. 验证clientId/clientSecret → 3. 校验任务定义归属 → 4. 继承配置创建任务

### Excel导入
1. 上传文件+taskDefinitionId → 2. 加载列定义映射 → 3. 解析Excel数据 → 4. 返回JSON结果

### Excel导出
1. 提交taskDefinitionId+data → 2. 加载列定义 → 3. 生成Excel → 4. HTTP下载

## API接口

### 鉴权与登录
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 登录获取API Key |

### 任务管理
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/tasks | 创建任务 |
| POST | /api/tasks/external | 外部创建任务 |
| GET | /api/tasks | 任务列表 |
| GET | /api/tasks/query | 条件分页查询 |
| GET | /api/tasks/{id} | 任务详情 |
| PUT | /api/tasks/{id}/status | 更新状态 |
| PUT | /api/tasks/{id}/progress | 更新进度 |
| DELETE | /api/tasks/{id} | 删除已完成任务 |

### 任务定义
| 方法 | 路径 | 说明 |
|------|------|------|
| CRUD | /api/task-definitions | 完整CRUD |
| GET | /api/task-definitions/name/{name} | 按名称查询 |
| GET | /api/task-definitions/client/{clientId} | 按客户端查询 |

### 客户端
| 方法 | 路径 | 说明 |
|------|------|------|
| CRUD | /api/clients | 完整CRUD |
| GET | /api/clients/client-id/{clientId} | 按clientId查询 |
| PUT | /api/clients/client-id/{clientId}/status | 启停控制 |

### 数据源
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/data-sources/test-connection | 连通性测试 |
| CRUD | /api/data-sources | 完整CRUD |

### 列定义
| 方法 | 路径 | 说明 |
|------|------|------|
| CRUD | /api/column-definitions | 完整CRUD |
| GET | /api/column-definitions/task-definition/{id} | 按任务定义查询 |
| DELETE | /api/column-definitions/task-definition/{id} | 按任务定义删除 |

### Excel处理
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/excel/import | 导入Excel |
| POST | /api/excel/export | 导出Excel |

### 看板
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/dashboard/overview | 首页统计数据 |

## 认证机制

除白名单接口外（登录、外部任务、健康检查），其他接口需在请求头添加 `X-API-Key` 认证，仅"启用"状态的客户端可访问。

未授权访问统一返回 401。

## 数据表

| 表名 | 说明 |
|------|------|
| client | 客户端信息与认证状态 |
| data_source_config | 数据源连接配置 |
| task_definition | 任务模板定义 |
| task | 任务实例与执行状态 |
| column_definition | 列映射定义 |


## 商业化文档

- 系统架构文档：`../docs/系统架构文档.md`
- API文档说明：`../docs/API文档说明.md`
- 部署文档：`../docs/部署文档.md`
- 用户手册：`../docs/用户手册.md`
- 管理员手册：`../docs/管理员手册.md`
- 故障排查手册：`../docs/故障排查手册.md`
- 开发指南：`../docs/开发指南.md`
- API集成文档：`../docs/API集成文档.md`
- SDK使用文档：`../docs/SDK使用文档.md`
- 培训与支持：`../docs/培训与支持.md`
- 运行后可访问 Swagger UI：`/swagger-ui/index.html`
- OpenAPI JSON：`/v3/api-docs`

## 监控

- 健康检查: GET /actuator/health
- 基础信息: GET /actuator/info

## 许可证

MIT License

- **任务管理**：配置任务定义、创建任务、查看任务列表、查看任务详情、删除任务
- **导入功能**：上传Excel文件、解析数据、处理数据、返回JSON格式数据
- **导出功能**：查询数据、处理数据、生成Excel文件、返回文件
- **数据列定义管理**：配置数据列定义、查看列表、创建、删除
- **客户端管理**：客户端注册、认证、授权

## 技术栈

- **后端**：Java 17+, Spring Boot 3.x, MyBatis Plus 3.5.x, Spring Cloud Alibaba 2023.x
- **前端**：Vue 3, Element Plus
- **数据库**：MySQL 8.0+
- **文件存储**：MinIO/S3兼容存储
- **缓存**：Redis
- **消息队列**：RabbitMQ
- **监控**：Prometheus + Grafana
- **分布式追踪**：Jaeger/Zipkin

## 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/your-repo/excel-process-service.git
   cd excel-process-service
   ```

2. **配置环境**
   - 安装Java 17+
   - 安装MySQL 8.0+
   - 安装Redis
   - 安装RabbitMQ
   - 安装MinIO
   - 安装Nacos

3. **配置数据库**
   - 创建数据库：`excel_process`
   - 运行初始化脚本：`src/main/resources/db/schema.sql`

4. **配置应用**
   - 修改`src/main/resources/application.yml`中的配置

5. **编译项目**
   ```bash
   mvn clean package -DskipTests
   ```

6. **启动应用**
   ```bash
   java -jar target/excel-process-service-1.0.0.jar
   ```

## API接口

### 任务管理
- `POST /api/tasks`：创建任务
- `GET /api/tasks`：获取任务列表
- `GET /api/tasks/{id}`：获取任务详情
- `PUT /api/tasks/{id}/status`：更新任务状态
- `PUT /api/tasks/{id}/progress`：更新任务进度
- `DELETE /api/tasks/{id}`：删除任务

### 任务定义管理
- `POST /api/task-definitions`：创建任务定义
- `GET /api/task-definitions`：获取任务定义列表
- `GET /api/task-definitions/{id}`：获取任务定义详情
- `PUT /api/task-definitions/{id}`：更新任务定义
- `DELETE /api/task-definitions/{id}`：删除任务定义
- `GET /api/task-definitions/name/{name}`：根据名称获取任务定义
- `GET /api/task-definitions/client/{clientId}`：根据客户端ID获取任务定义

### 客户端管理
- `POST /api/clients`：创建客户端
- `GET /api/clients`：获取客户端列表
- `GET /api/clients/{id}`：获取客户端详情
- `PUT /api/clients/{id}`：更新客户端
- `DELETE /api/clients/{id}`：删除客户端
- `GET /api/clients/client-id/{clientId}`：根据客户端ID获取客户端
- `PUT /api/clients/client-id/{clientId}/status`：更新客户端状态

### 数据列定义管理
- `POST /api/column-definitions`：创建数据列定义
- `GET /api/column-definitions`：获取数据列定义列表
- `GET /api/column-definitions/{id}`：获取数据列定义详情
- `PUT /api/column-definitions/{id}`：更新数据列定义
- `DELETE /api/column-definitions/{id}`：删除数据列定义
- `GET /api/column-definitions/task-definition/{taskDefinitionId}`：根据任务定义ID获取数据列定义
- `DELETE /api/column-definitions/task-definition/{taskDefinitionId}`：根据任务定义ID删除数据列定义

### Excel处理
- `POST /api/excel/import`：导入Excel文件
- `POST /api/excel/export`：导出Excel文件

## 客户端认证

所有API接口（除了客户端管理和任务定义管理）都需要在请求头中添加`X-API-Key`字段，值为客户端的API Key。

## 示例

### 创建任务
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "X-API-Key: test-client" \
  -d '{
    "name": "test-task",
    "description": "测试任务",
    "type": "导出",
    "taskDefinitionId": 1,
    "params": "{\"batch\": true, \"batchSize\": 10000, \"format\": \"excel\"}"
  }'
```

### 导入Excel文件
```bash
curl -X POST http://localhost:8080/api/excel/import \
  -H "X-API-Key: test-client" \
  -F "file=@test.xlsx" \
  -F "columnDefinitions=[{\"fieldName\": \"id\", \"columnName\": \"ID\", \"columnType\": \"number\"}, {\"fieldName\": \"name\", \"columnName\": \"名称\", \"columnType\": \"string\"}]"
```


## 商业化文档

- 系统架构文档：`../docs/系统架构文档.md`
- API文档说明：`../docs/API文档说明.md`
- 部署文档：`../docs/部署文档.md`
- 用户手册：`../docs/用户手册.md`
- 管理员手册：`../docs/管理员手册.md`
- 故障排查手册：`../docs/故障排查手册.md`
- 开发指南：`../docs/开发指南.md`
- API集成文档：`../docs/API集成文档.md`
- SDK使用文档：`../docs/SDK使用文档.md`
- 培训与支持：`../docs/培训与支持.md`
- 运行后可访问 Swagger UI：`/swagger-ui/index.html`
- OpenAPI JSON：`/v3/api-docs`

## 监控

- 健康检查：`GET /actuator/health`
- 指标：`GET /actuator/metrics`
- Prometheus：`GET /actuator/prometheus`

## 贡献

欢迎提交issue和pull request。

## 许可证

MIT License
# Excel处理服务

Excel处理服务是一个基于Spring Boot的微服务，提供Excel文件的导入/导出功能，支持任务管理和数据列定义管理。

## 功能特性

- **任务管理**：配置任务定义、创建任务、查看任务列表、查看任务详情、删除任务
- **导入功能**：上传Excel文件、解析数据、处理数据、返回JSON格式数据
- **导出功能**：查询数据、处理数据、生成Excel文件、返回文件
- **数据列定义管理**：配置数据列定义、查看列表、创建、删除
- **客户端管理**：客户端注册、认证、授权

## 技术栈

- **后端**：Java 17+, Spring Boot 3.x, MyBatis Plus 3.5.x, Spring Cloud Alibaba 2023.x
- **前端**：Vue 3, Element Plus
- **数据库**：MySQL 8.0+
- **文件存储**：MinIO/S3兼容存储
- **缓存**：Redis
- **消息队列**：RabbitMQ
- **监控**：Prometheus + Grafana
- **分布式追踪**：Jaeger/Zipkin

## 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/your-repo/excel-process-service.git
   cd excel-process-service
   ```

2. **配置环境**
   - 安装Java 17+
   - 安装MySQL 8.0+
   - 安装Redis
   - 安装RabbitMQ
   - 安装MinIO
   - 安装Nacos

3. **配置数据库**
   - 创建数据库：`excel_process`
   - 运行初始化脚本：`src/main/resources/db/schema.sql`

4. **配置应用**
   - 修改`src/main/resources/application.yml`中的配置

5. **编译项目**
   ```bash
   mvn clean package -DskipTests
   ```

6. **启动应用**
   ```bash
   java -jar target/excel-process-service-1.0.0.jar
   ```

## API接口

### 任务管理
- `POST /api/tasks`：创建任务
- `GET /api/tasks`：获取任务列表
- `GET /api/tasks/{id}`：获取任务详情
- `PUT /api/tasks/{id}/status`：更新任务状态
- `PUT /api/tasks/{id}/progress`：更新任务进度
- `DELETE /api/tasks/{id}`：删除任务

### 任务定义管理
- `POST /api/task-definitions`：创建任务定义
- `GET /api/task-definitions`：获取任务定义列表
- `GET /api/task-definitions/{id}`：获取任务定义详情
- `PUT /api/task-definitions/{id}`：更新任务定义
- `DELETE /api/task-definitions/{id}`：删除任务定义
- `GET /api/task-definitions/name/{name}`：根据名称获取任务定义
- `GET /api/task-definitions/client/{clientId}`：根据客户端ID获取任务定义

### 客户端管理
- `POST /api/clients`：创建客户端
- `GET /api/clients`：获取客户端列表
- `GET /api/clients/{id}`：获取客户端详情
- `PUT /api/clients/{id}`：更新客户端
- `DELETE /api/clients/{id}`：删除客户端
- `GET /api/clients/client-id/{clientId}`：根据客户端ID获取客户端
- `PUT /api/clients/client-id/{clientId}/status`：更新客户端状态

### 数据列定义管理
- `POST /api/column-definitions`：创建数据列定义
- `GET /api/column-definitions`：获取数据列定义列表
- `GET /api/column-definitions/{id}`：获取数据列定义详情
- `PUT /api/column-definitions/{id}`：更新数据列定义
- `DELETE /api/column-definitions/{id}`：删除数据列定义
- `GET /api/column-definitions/task-definition/{taskDefinitionId}`：根据任务定义ID获取数据列定义
- `DELETE /api/column-definitions/task-definition/{taskDefinitionId}`：根据任务定义ID删除数据列定义

### Excel处理
- `POST /api/excel/import`：导入Excel文件
- `POST /api/excel/export`：导出Excel文件

## 客户端认证

所有API接口（除了客户端管理和任务定义管理）都需要在请求头中添加`X-API-Key`字段，值为客户端的API Key。

## 示例

### 创建任务
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "X-API-Key: test-client" \
  -d '{
    "name": "test-task",
    "description": "测试任务",
    "type": "导出",
    "taskDefinitionId": 1,
    "params": "{\"batch\": true, \"batchSize\": 10000, \"format\": \"excel\"}"
  }'
```

### 导入Excel文件
```bash
curl -X POST http://localhost:8080/api/excel/import \
  -H "X-API-Key: test-client" \
  -F "file=@test.xlsx" \
  -F "columnDefinitions=[{\"fieldName\": \"id\", \"columnName\": \"ID\", \"columnType\": \"number\"}, {\"fieldName\": \"name\", \"columnName\": \"名称\", \"columnType\": \"string\"}]"
```


## 商业化文档

- 系统架构文档：`../docs/系统架构文档.md`
- API文档说明：`../docs/API文档说明.md`
- 部署文档：`../docs/部署文档.md`
- 用户手册：`../docs/用户手册.md`
- 管理员手册：`../docs/管理员手册.md`
- 故障排查手册：`../docs/故障排查手册.md`
- 开发指南：`../docs/开发指南.md`
- API集成文档：`../docs/API集成文档.md`
- SDK使用文档：`../docs/SDK使用文档.md`
- 培训与支持：`../docs/培训与支持.md`
- 运行后可访问 Swagger UI：`/swagger-ui/index.html`
- OpenAPI JSON：`/v3/api-docs`

## 监控

- 健康检查：`GET /actuator/health`
- 指标：`GET /actuator/metrics`
- Prometheus：`GET /actuator/prometheus`

## 贡献

欢迎提交issue和pull request。

## 许可证

MIT License

## 外部客户端直连创建任务

- 接口：`POST /api/tasks/external`
- 鉴权方式：请求体内传 `clientId` + `clientSecret`
- 成功后返回客户端信息和任务信息。

示例：

```bash
curl -X POST http://localhost:8080/api/tasks/external \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "test-client",
    "clientSecret": "test-secret",
    "taskDefinitionId": 1,
    "name": "external-task",
    "type": "导出"
  }'
```

## HTTP数据源测试服务（认证/不认证）

为了验证 `http` 类型任务的数据来源配置，提供以下测试接口（均在当前服务中）：

- 认证接口：`POST /api/mock-http-source/auth/login`
- 免认证查询：`POST /api/mock-http-source/query/public`
- 需认证查询：`POST /api/mock-http-source/query/secure`（需要 `Authorization: Bearer mock-token-123`）

示例（认证后查询）：

```bash
# 1) 获取token
curl -X POST http://localhost:8080/api/mock-http-source/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}'

# 2) 带token查询
curl -X POST http://localhost:8080/api/mock-http-source/query/secure \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer mock-token-123" \
  -d '{"bizDate":"2026-03-15"}'
```

### 分析与扩展
- `GET /api/analytics/status-distribution`：状态分布
- `GET /api/analytics/task-trend?days=7`：趋势数据
- `GET /api/analytics/report-summary`：汇总报表
- `POST /api/tasks/{id}/process-async`：异步处理
- `PUT /api/tasks/batch/status`：批量更新状态
- `CRUD /api/templates`：模板系统
- `GET /api/plugins`、`POST /api/plugins/process-task/{taskId}`：插件系统

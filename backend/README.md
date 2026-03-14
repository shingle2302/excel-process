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
   - 运行初始化脚本：`src/main/resources/db/init.sql`

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
   - 运行初始化脚本：`src/main/resources/db/init.sql`

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
   - 运行初始化脚本：`src/main/resources/db/init.sql`

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
   - 运行初始化脚本：`src/main/resources/db/init.sql`

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
   - 运行初始化脚本：`src/main/resources/db/init.sql`

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

## 监控

- 健康检查：`GET /actuator/health`
- 指标：`GET /actuator/metrics`
- Prometheus：`GET /actuator/prometheus`

## 贡献

欢迎提交issue和pull request。

## 许可证

MIT License
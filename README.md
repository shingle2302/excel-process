# Excel处理服务

Excel处理服务是一个前后端分离的应用程序，提供Excel文件的导入/导出功能，支持任务管理和数据列定义管理。

## 项目结构

```
excel-process-service/
├── backend/               # 后端代码
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── pom.xml
│   └── 需求文档.md
├── frontend/              # 前端代码
└── README.md              # 项目总体说明
```

## 技术栈

- **后端**：Java 17+, Spring Boot 3.x, MyBatis Plus 3.5.x, Spring Cloud Alibaba 2023.x
- **前端**：Vue 3, Element Plus, Vite
- **数据库**：MySQL 8.0+
- **文件存储**：MinIO/S3兼容存储
- **缓存**：Redis
- **消息队列**：RabbitMQ
- **监控**：Prometheus + Grafana
- **分布式追踪**：Jaeger/Zipkin

## 快速开始

### 后端

1. **配置环境**
   - 安装Java 17+
   - 安装MySQL 8.0+
   - 安装Redis
   - 安装RabbitMQ
   - 安装MinIO
   - 安装Nacos

2. **配置数据库**
   - 创建数据库：`excel_process`
   - 运行初始化脚本：`backend/src/main/resources/db/init.sql`

3. **配置应用**
   - 修改`backend/src/main/resources/application.yml`中的配置

4. **编译项目**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

5. **启动应用**
   ```bash
   java -jar backend/target/excel-process-service-1.0.0.jar
   ```

### 前端

1. **安装依赖**
   ```bash
   cd frontend
   npm install
   ```

2. **启动开发服务器**
   ```bash
   npm run dev
   ```

3. **构建生产版本**
   ```bash
   npm run build
   ```

## 功能特性

- **任务管理**：配置任务定义、创建任务、查看任务列表、查看任务详情、删除任务
- **导入功能**：上传Excel文件、解析数据、处理数据、返回JSON格式数据
- **导出功能**：查询数据、处理数据、生成Excel文件、返回文件
- **数据列定义管理**：配置数据列定义、查看列表、创建、删除
- **客户端管理**：客户端注册、认证、授权

## API接口

后端API接口请参考 `backend/README.md`

## 许可证

MIT License
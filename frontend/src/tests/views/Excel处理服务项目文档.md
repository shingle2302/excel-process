# Excel处理服务项目文档

## 项目概述

本项目是一个基于Spring Boot 3.x和Vue 3的前后端分离Excel处理服务，提供了任务管理、Excel导入导出、列定义管理和客户端管理等功能。

## 技术栈

### 后端
- **框架**: Spring Boot 3.x (Jakarta EE 9+)
- **数据库**: MySQL 8.0+ (MyBatis Plus 3.5.x)
- **Excel处理**: EasyExcel 4.0.3
- **存储**: MinIO
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **安全**: Spring Security (API Key认证)
- **构建工具**: Maven

### 前端
- **框架**: Vue 3 (Composition API)
- **UI库**: Element Plus
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **构建工具**: Vite
- **测试框架**: Vitest + @vue/test-utils

## 功能模块

### 1. 任务管理
- 任务创建、查询、更新、删除
- 任务状态管理（待处理、处理中、已完成、失败）
- 任务进度跟踪

### 2. Excel导入导出
- Excel文件上传与解析
- Excel数据导出
- 列定义配置（从任务定义获取）

### 3. 列定义管理
- 列定义创建、编辑、删除
- 与任务定义关联

### 4. 客户端管理
- 客户端注册、启用/禁用
- API密钥管理

## 代码结构

### 后端结构

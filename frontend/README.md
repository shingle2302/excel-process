# Excel处理服务前端

## 项目简介

Excel处理服务的前端应用，基于Vue 3 + Element Plus开发，提供任务管理、Excel导入/导出、客户端管理等功能。

## 技术栈

- Vue 3
- Element Plus
- Vue Router
- Axios
- Vite
- Vitest (测试框架)

## 项目结构

```
frontend/
├── package.json          # 项目依赖配置
├── vite.config.js        # Vite配置
├── vitest.config.js      # Vitest配置
├── index.html            # HTML入口文件
├── src/
│   ├── main.js           # 主入口文件
│   ├── App.vue           # 根组件
│   ├── router/           # 路由配置
│   ├── services/         # API服务
│   ├── views/            # 页面组件
│   └── tests/            # 测试文件
```

## 安装依赖

```bash
npm install
```

## 运行开发服务器

```bash
npm run dev
```

## 构建生产版本

```bash
npm run build
```

## 运行测试

### 运行所有测试

```bash
npm test
```

### 运行测试并生成覆盖率报告

```bash
npm run test:coverage
```

## 测试文件说明

测试文件位于 `src/tests` 目录下，包括：

- `setup.js` - 测试环境配置
- `services/api.test.js` - API服务测试
- `views/Home.test.js` - 首页组件测试
- `views/ExcelImport.test.js` - Excel导入组件测试
- `views/ExcelExport.test.js` - Excel导出组件测试
- `views/TaskList.test.js` - 任务列表组件测试
- `views/TaskDetail.test.js` - 任务详情组件测试

## 注意事项

- 前端登录页使用用户账号密码（默认 `admin/ChangeMe@123`，建议生产环境通过环境变量覆盖引导管理员修改），调用 `POST /api/auth/user-login`
- 登录成功后前端会自动保存返回的 `apiKey` 并在后续请求中通过 `X-API-Key` 传递
- 前端通过代理配置将API请求转发到后端服务（默认端口8080）
- 测试环境使用了模拟数据，不需要真实的后端服务

# API集成文档

## 鉴权
Header 传 `X-API-Key`。

## 推荐调用顺序
1. （管理界面）`POST /api/auth/user-login`，参数：`username/password`
2. （SDK/外部系统）`POST /api/auth/login`，参数：`clientId/clientSecret`
3. `POST /api/task-definitions`
4. `POST /api/tasks`
5. `POST /api/tasks/{id}/process-async`
6. `GET /api/tasks/{id}`

## 分析与可视化
- `GET /api/analytics/status-distribution`
- `GET /api/analytics/task-trend`
- `GET /api/analytics/report-summary`

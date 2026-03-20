# API集成文档

## 鉴权
Header 传 `X-API-Key`。

## 推荐调用顺序
1. `POST /api/auth/login`
2. `POST /api/task-definitions`
3. `POST /api/tasks`
4. `POST /api/tasks/{id}/process-async`
5. `GET /api/tasks/{id}`

## 分析与可视化
- `GET /api/analytics/status-distribution`
- `GET /api/analytics/task-trend`
- `GET /api/analytics/report-summary`

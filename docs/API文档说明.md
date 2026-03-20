# API文档说明

项目已接入 SpringDoc OpenAPI 3。

## 访问地址
- OpenAPI JSON: `/v3/api-docs`
- Swagger UI: `/swagger-ui/index.html`

## 鉴权说明
除白名单接口外，调用API需在Header中传入：

```http
X-API-Key: <apiKey>
```

其中：
- 管理界面登录通过 `POST /api/auth/user-login`（`username/password`）获取 `apiKey`
- SDK/外部系统登录通过 `POST /api/auth/login`（`clientId/clientSecret`）获取 `apiKey`

## 主要分组
- 任务管理（/api/tasks）
- 客户端管理（/api/clients）
- Excel处理（/api/excel）

## 常见错误响应格式
```json
{
  "code": 400,
  "message": "请求参数校验失败",
  "details": "field: message",
  "path": "/api/tasks",
  "timestamp": "2026-03-20T10:00:00"
}
```

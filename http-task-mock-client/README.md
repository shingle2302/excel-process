# HttpTaskMockController（独立项目）

该项目独立于主工程，通过引入 `external-task-client-sdk` 来测试外部客户端创建任务能力。

## 1. 先安装 SDK 到本地仓库

```bash
cd ../external-task-client-sdk
mvn clean install
```

## 2. 启动 mock client

```bash
cd ../http-task-mock-client
mvn spring-boot:run
```

默认会调用 `http://localhost:8080` 的主服务，可通过参数覆盖：

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--target.server-base-url=http://127.0.0.1:8080"
```

## 3. 测试创建外部任务

```bash
curl -X POST http://localhost:8081/api/mock-client/tasks/external \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "test-client",
    "clientSecret": "test-secret",
    "taskDefinitionId": 2,
    "name": "http-task-by-sdk",
    "requestParams": "{\"status\":\"active\",\"bizDate\":\"2026-03-15\"}"
  }'
```

> 说明：创建外部任务时无需指定数据来源类型，HTTP 任务只需要传查询参数 `requestParams` 即可。

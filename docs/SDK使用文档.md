# SDK使用文档

SDK 位于 `external-task-client-sdk/`。

## 快速开始
1. 安装依赖并引入 SDK。
2. 通过 `ExternalTaskClient` 创建外部任务。
3. 对接服务端 `/api/tasks/external`。

## 建议
- 使用重试机制处理网络抖动。
- 结合业务方日志记录 requestId 与 taskId。

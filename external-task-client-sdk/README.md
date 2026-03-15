# external-task-client-sdk

用于外部系统直接调用 `POST /api/tasks/external` 创建任务的 Java SDK（jar）。

## 打包

```bash
cd external-task-client-sdk
mvn clean package
```

## 引用

将打包产物安装到本地仓库：

```bash
mvn install
```

业务系统引入：

```xml
<dependency>
  <groupId>com.excel</groupId>
  <artifactId>external-task-client-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
```

## 示例

```java
ExternalTaskClient client = new ExternalTaskClient("http://localhost:8080");
ExternalTaskCreateRequest request = new ExternalTaskCreateRequest();
request.clientId = "test-client";
request.clientSecret = "test-secret";
request.taskDefinitionId = 1L;
request.name = "sdk-create-task";
request.type = "导出";

ExternalTaskCreateResponse response = client.createTask(request);
System.out.println(response.task.id);
```

package com.excel.mockclient.controller;

import com.excel.sdk.ExternalTaskClient;
import com.excel.sdk.model.ExternalTaskCreateRequest;
import com.excel.sdk.model.ExternalTaskCreateResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mock-client")
public class MockTaskController {

    private final ExternalTaskClient externalTaskClient;

    public MockTaskController(ExternalTaskClient externalTaskClient) {
        this.externalTaskClient = externalTaskClient;
    }

    @PostMapping("/tasks/external")
    public ExternalTaskCreateResponse createExternalTask(@RequestBody ExternalTaskCreateRequest request) {
        return externalTaskClient.createTask(request);
    }
}

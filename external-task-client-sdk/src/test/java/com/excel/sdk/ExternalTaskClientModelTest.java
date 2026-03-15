package com.excel.sdk;

import com.excel.sdk.model.ExternalTaskCreateRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExternalTaskClientModelTest {

    @Test
    void shouldBuildRequest() {
        ExternalTaskCreateRequest request = new ExternalTaskCreateRequest();
        request.clientId = "test-client";
        request.clientSecret = "test-secret";
        request.name = "sdk-task";
        request.type = "导出";

        Assertions.assertEquals("test-client", request.clientId);
        Assertions.assertEquals("sdk-task", request.name);
    }
}

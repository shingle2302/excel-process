package com.excel.controller.mock;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mock-http-source")
public class HttpTaskMockController {

    @PostMapping("/auth/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> payload) {
        String username = payload.get("username") == null ? "" : String.valueOf(payload.get("username"));
        String password = payload.get("password") == null ? "" : String.valueOf(payload.get("password"));

        if (!"demo".equals(username) || !"demo123".equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("token", "mock-token-123");
        result.put("tokenType", "Bearer");
        result.put("expiresIn", 3600);
        return result;
    }

    @PostMapping("/query/public")
    public Map<String, Object> publicQuery(@RequestBody(required = false) Map<String, Object> payload) {
        Map<String, Object> result = new HashMap<>();
        result.put("authRequired", false);
        result.put("request", payload == null ? Map.of() : payload);
        result.put("data", new Object[]{
                Map.of("id", 1, "username", "alice", "status", "active"),
                Map.of("id", 2, "username", "bob", "status", "active")
        });
        return result;
    }

    @PostMapping("/query/secure")
    public Map<String, Object> secureQuery(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) Map<String, Object> payload) {
        if (!"Bearer mock-token-123".equals(authorization)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "认证信息无效");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("authRequired", true);
        result.put("request", payload == null ? Map.of() : payload);
        result.put("data", new Object[]{
                Map.of("id", 101, "orderNo", "SO-001", "amount", 188.5),
                Map.of("id", 102, "orderNo", "SO-002", "amount", 299.0)
        });
        return result;
    }
}

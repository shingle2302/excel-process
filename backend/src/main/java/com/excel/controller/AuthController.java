package com.excel.controller;

import com.excel.dto.LoginRequest;
import com.excel.dto.LoginResponse;
import com.excel.entity.Client;
import com.excel.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        boolean valid = clientService.validateClient(request.getClientId(), request.getClientSecret());
        if (!valid) {
            throw new RuntimeException("用户名或密码错误");
        }

        Client client = clientService.getByClientId(request.getClientId())
                .orElseThrow(() -> new RuntimeException("客户端不存在"));

        LoginResponse response = new LoginResponse();
        response.setApiKey(client.getClientId());
        response.setClientName(client.getClientName());
        response.setExpiresIn(2 * 60 * 60); // 2 hours
        return response;
    }
}

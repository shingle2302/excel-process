package com.excel.controller;

import com.excel.dto.LoginRequest;
import com.excel.dto.LoginResponse;
import com.excel.dto.UserLoginRequest;
import com.excel.entity.Client;
import com.excel.entity.UserAccount;
import com.excel.exception.BusinessException;
import com.excel.service.ClientService;
import com.excel.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证")
public class AuthController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/user-login")
    @Operation(summary = "用户登录", description = "管理界面登录，使用 username/password 获取访问凭证")
    public LoginResponse userLogin(@RequestBody UserLoginRequest request) {
        boolean valid = userAuthService.validateUser(request.getUsername(), request.getPassword());
        if (!valid) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        UserAccount user = userAuthService.getByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        LoginResponse response = new LoginResponse();
        response.setApiKey(user.getUsername());
        response.setClientName(user.getDisplayName());
        response.setExpiresIn(2 * 60 * 60);
        return response;
    }

    @PostMapping("/login")
    @Operation(summary = "客户端登录", description = "仅供 SDK/外部系统使用，使用 clientId/clientSecret 获取 API Key")
    public LoginResponse login(@RequestBody LoginRequest request) {
        boolean valid = clientService.validateClient(request.getClientId(), request.getClientSecret());
        if (!valid) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        Client client = clientService.getByClientId(request.getClientId())
                .orElseThrow(() -> new BusinessException(404, "客户端不存在"));

        LoginResponse response = new LoginResponse();
        response.setApiKey(client.getClientId());
        response.setClientName(client.getClientName());
        response.setExpiresIn(2 * 60 * 60);
        return response;
    }
}

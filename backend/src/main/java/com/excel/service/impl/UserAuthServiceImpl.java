package com.excel.service.impl;

import com.excel.service.UserAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Value("${security.ui-login.username:admin}")
    private String username;

    @Value("${security.ui-login.password:admin123}")
    private String password;

    @Override
    public boolean validateUser(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    @Override
    public boolean isActiveUser(String username) {
        return this.username.equals(username);
    }
}

package com.excel.config;

import com.excel.service.UserAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserAuthService userAuthService;
    private final String username;
    private final String password;
    private final String displayName;

    public AdminUserInitializer(
            UserAuthService userAuthService,
            @Value("${security.ui-login.bootstrap-admin.username:admin}") String username,
            @Value("${security.ui-login.bootstrap-admin.password:ChangeMe@123}") String password,
            @Value("${security.ui-login.bootstrap-admin.display-name:系统管理员}") String displayName) {
        this.userAuthService = userAuthService;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }

    @Override
    public void run(String... args) {
        if (userAuthService.getByUsername(username).isEmpty()) {
            userAuthService.createBootstrapAdmin(username, password, displayName);
        }
    }
}

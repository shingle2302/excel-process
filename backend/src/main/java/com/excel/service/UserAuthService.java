package com.excel.service;

import com.excel.entity.UserAccount;

import java.util.Optional;

public interface UserAuthService {

    boolean validateUser(String username, String password);

    boolean isActiveUser(String username);

    Optional<UserAccount> getByUsername(String username);

    UserAccount createBootstrapAdmin(String username, String rawPassword, String displayName);
}

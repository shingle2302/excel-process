package com.excel.service;

public interface UserAuthService {

    boolean validateUser(String username, String password);

    boolean isActiveUser(String username);
}

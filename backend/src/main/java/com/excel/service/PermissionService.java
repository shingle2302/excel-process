package com.excel.service;

public interface PermissionService {
    boolean hasPermission(String clientId, String permission);
}

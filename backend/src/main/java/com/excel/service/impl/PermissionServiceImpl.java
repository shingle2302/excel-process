package com.excel.service.impl;

import com.excel.entity.UserAccount;
import com.excel.service.UserAuthService;
import com.excel.service.PermissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Set<String> OPERATOR_PERMISSIONS = Set.of(
            "task:read", "task:write", "excel:process",
            "task-definition:read", "task-definition:write",
            "column-definition:read", "column-definition:write",
            "datasource:read", "datasource:write",
            "dashboard:read", "analytics:read",
            "template:read", "template:write",
            "plugin:read", "plugin:execute"
    );

    private final Set<String> adminClients;
    private final Set<String> operatorClients;
    private final Set<String> viewerClients;
    private final UserAuthService userAuthService;

    public PermissionServiceImpl(
            UserAuthService userAuthService,
            @Value("${security.rbac.admin-clients:admin-client}") String adminClients,
            @Value("${security.rbac.operator-clients:}") String operatorClients,
            @Value("${security.rbac.viewer-clients:}") String viewerClients) {
        this.userAuthService = userAuthService;
        this.adminClients = parseClients(adminClients);
        this.operatorClients = parseClients(operatorClients);
        this.viewerClients = parseClients(viewerClients);
    }

    @Override
    public boolean hasPermission(String clientId, String permission) {
        if (clientId == null || clientId.isBlank()) {
            return false;
        }
        if (hasUserPermission(clientId, permission)) {
            return true;
        }
        if (adminClients.contains(clientId)) {
            return true;
        }
        if (operatorClients.contains(clientId)) {
            return OPERATOR_PERMISSIONS.contains(permission);
        }
        if (viewerClients.contains(clientId)) {
            return permission.endsWith(":read");
        }
        return false;
    }

    private boolean hasUserPermission(String principal, String permission) {
        return userAuthService.getByUsername(principal)
                .filter(user -> "启用".equals(user.getStatus()))
                .map(UserAccount::getRoleCode)
                .map(roleCode -> {
                    if ("ADMIN".equalsIgnoreCase(roleCode)) {
                        return true;
                    }
                    if ("OPERATOR".equalsIgnoreCase(roleCode)) {
                        return OPERATOR_PERMISSIONS.contains(permission);
                    }
                    return permission.endsWith(":read");
                })
                .orElse(false);
    }

    private Set<String> parseClients(String clients) {
        return Arrays.stream(clients.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));
    }
}

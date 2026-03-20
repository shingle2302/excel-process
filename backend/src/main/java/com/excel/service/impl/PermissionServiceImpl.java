package com.excel.service.impl;

import com.excel.service.PermissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final Set<String> adminClients;
    private final Set<String> operatorClients;
    private final Set<String> viewerClients;

    public PermissionServiceImpl(
            @Value("${security.rbac.admin-clients:admin-client}") String adminClients,
            @Value("${security.rbac.operator-clients:}") String operatorClients,
            @Value("${security.rbac.viewer-clients:}") String viewerClients) {
        this.adminClients = parseClients(adminClients);
        this.operatorClients = parseClients(operatorClients);
        this.viewerClients = parseClients(viewerClients);
    }

    @Override
    public boolean hasPermission(String clientId, String permission) {
        if (clientId == null || clientId.isBlank()) {
            return false;
        }
        if (adminClients.contains(clientId)) {
            return true;
        }
        if (operatorClients.contains(clientId)) {
            return "task:read".equals(permission) || "task:write".equals(permission) || "excel:process".equals(permission);
        }
        if (viewerClients.contains(clientId)) {
            return permission.endsWith(":read");
        }
        return false;
    }

    private Set<String> parseClients(String clients) {
        return Arrays.stream(clients.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));
    }
}

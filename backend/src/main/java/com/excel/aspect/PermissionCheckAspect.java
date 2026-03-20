package com.excel.aspect;

import com.excel.annotation.RequirePermission;
import com.excel.exception.BusinessException;
import com.excel.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionCheckAspect {

    private final PermissionService permissionService;

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String clientId = authentication == null ? null : String.valueOf(authentication.getPrincipal());
        if (!permissionService.hasPermission(clientId, requirePermission.value())) {
            throw new BusinessException(403, "权限不足");
        }
    }
}

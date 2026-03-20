package com.excel.aspect;

import com.excel.annotation.OperationLog;
import com.excel.entity.Client;
import com.excel.entity.OperationLogRecord;
import com.excel.service.ClientService;
import com.excel.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ClientService clientService;
    private final HttpServletRequest request;

    @Around("@annotation(operationLog)")
    public Object logOperation(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        OperationLogRecord record = new OperationLogRecord();
        record.setOperationType(operationLog.type());
        record.setOperationDetail(buildDetail(joinPoint, operationLog));
        record.setCreateTime(LocalDateTime.now());

        String clientId = currentClientId();
        Optional<Client> client = clientService.getByClientId(clientId);
        client.map(Client::getId).ifPresent(record::setClientId);

        try {
            Object result = joinPoint.proceed();
            record.setOperationDetail(record.getOperationDetail() + " | status=成功");
            return result;
        } catch (Exception e) {
            record.setOperationDetail(record.getOperationDetail() + " | status=失败 | error=" + e.getMessage());
            throw e;
        } finally {
            operationLogService.save(record);
        }
    }

    private String currentClientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : String.valueOf(authentication.getPrincipal());
    }

    private String buildDetail(ProceedingJoinPoint joinPoint, OperationLog operationLog) {
        String desc = operationLog.value().isBlank() ? joinPoint.getSignature().toShortString() : operationLog.value();
        return String.format("desc=%s, path=%s, method=%s", desc, request.getRequestURI(), request.getMethod());
    }
}

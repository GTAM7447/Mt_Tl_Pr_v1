package com.spring.jwt.aspect;

import com.spring.jwt.audit.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditTrailAspect {

    private final AuditLogService auditLogService;

    @AfterReturning(pointcut = "execution(* com.spring.jwt..service.*ServiceImpl.create*(..)) || " +
                               "execution(* com.spring.jwt.admin.service.*Service.create*(..))",
                    returning = "result")
    public void auditCreate(JoinPoint joinPoint, Object result) {
        try {
            String entityType = extractEntityType(joinPoint);
            Object entityId = extractEntityId(result);
            String details = buildDetails(joinPoint);
            
            auditLogService.logCreate(entityType, entityId, details);
        } catch (Exception e) {
            log.error("Failed to audit create operation: {}", e.getMessage());
        }
    }

    @AfterReturning(pointcut = "execution(* com.spring.jwt..service.*ServiceImpl.update*(..)) || " +
                               "execution(* com.spring.jwt.admin.service.*Service.update*(..))",
                    returning = "result")
    public void auditUpdate(JoinPoint joinPoint, Object result) {
        try {
            String entityType = extractEntityType(joinPoint);
            Object entityId = extractEntityId(joinPoint.getArgs());
            String details = buildDetails(joinPoint);
            
            auditLogService.logUpdate(entityType, entityId, details);
        } catch (Exception e) {
            log.error("Failed to audit update operation: {}", e.getMessage());
        }
    }

    @AfterReturning(pointcut = "execution(* com.spring.jwt..service.*ServiceImpl.delete*(..)) || " +
                               "execution(* com.spring.jwt.admin.service.*Service.delete*(..))")
    public void auditDelete(JoinPoint joinPoint) {
        try {
            String entityType = extractEntityType(joinPoint);
            Object entityId = extractEntityId(joinPoint.getArgs());
            String details = buildDetails(joinPoint);
            
            auditLogService.logDelete(entityType, entityId, details);
        } catch (Exception e) {
            log.error("Failed to audit delete operation: {}", e.getMessage());
        }
    }

    @AfterThrowing(pointcut = "execution(* com.spring.jwt..service.*ServiceImpl.*(..)) || " +
                              "execution(* com.spring.jwt.admin.service.*Service.*(..))",
                   throwing = "exception")
    public void auditFailure(JoinPoint joinPoint, Throwable exception) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String action = extractAction(methodName);
            String entityType = extractEntityType(joinPoint);
            Object entityId = extractEntityId(joinPoint.getArgs());
            
            auditLogService.logFailure(action, entityType, entityId, exception.getMessage());
        } catch (Exception e) {
            log.error("Failed to audit failure: {}", e.getMessage());
        }
    }

    private String extractEntityType(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        return className.replace("ServiceImpl", "").replace("Service", "");
    }

    private Object extractEntityId(Object[] args) {
        if (args == null || args.length == 0) {
            return "N/A";
        }
        
        for (Object arg : args) {
            if (arg instanceof Integer || arg instanceof Long) {
                return arg;
            }
        }
        
        return "N/A";
    }

    private Object extractEntityId(Object result) {
        if (result == null) {
            return "N/A";
        }
        
        try {
            Method getIdMethod = result.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(result);
            return id != null ? id : "N/A";
        } catch (Exception e) {
            try {
                for (Method method : result.getClass().getMethods()) {
                    String methodName = method.getName();
                    if (methodName.endsWith("Id") && methodName.startsWith("get") && 
                        method.getParameterCount() == 0) {
                        Object id = method.invoke(result);
                        return id != null ? id : "N/A";
                    }
                }
            } catch (Exception ex) {
                return "N/A";
            }
        }
        
        return "N/A";
    }

    private String buildDetails(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        int argCount = joinPoint.getArgs() != null ? joinPoint.getArgs().length : 0;
        return String.format("Method: %s, Arguments: %d", methodName, argCount);
    }

    private String extractAction(String methodName) {
        if (methodName.startsWith("create")) return "CREATE";
        if (methodName.startsWith("update")) return "UPDATE";
        if (methodName.startsWith("delete")) return "DELETE";
        if (methodName.startsWith("get") || methodName.startsWith("find")) return "READ";
        return "OPERATION";
    }
}

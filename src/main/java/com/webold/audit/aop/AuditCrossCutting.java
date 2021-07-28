package com.webold.audit.aop;


import com.webold.framework.anotations.Log;
import com.webold.framework.packages.audit.view.AuditFactory;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditCrossCutting {

    private final LogService logService;

    @Pointcut("@annotation(com.webold.framework.anotations.Log)")
    public void log() {
        // Do Nothing ,Aop Running
    }

    @Pointcut("@annotation(com.webold.framework.anotations.NotLog)")
    public void notLog() {
        // Do Nothing ,Aop Running
    }


    @AfterReturning(pointcut = "log() && !notLog()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log annotation = method.getAnnotation(Log.class);
        if (null != annotation) {
            AuditFactory.AuditType auditType = annotation.type();
            if (Boolean.TRUE.equals(auditType.name().equals(AuditFactory.AuditType.ALL.name())) ||
                    Boolean.TRUE.equals(auditType.name().equals(AuditFactory.AuditType.AFTER_RETURNING.name())))
                logService.logAfterReturning(joinPoint, result);
        } else
            logService.logAfterReturning(joinPoint, result);

    }

    @Before("log() && !notLog()")
    public void logBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log annotation = method.getAnnotation(Log.class);
        if (annotation != null) {
            AuditFactory.AuditType auditType = annotation.type();
            if (Boolean.TRUE.equals(auditType.name().equals(AuditFactory.AuditType.ALL.name())) ||
                    Boolean.TRUE.equals(auditType.name().equals(AuditFactory.AuditType.BEFORE.name())))
                logService.logBefore(joinPoint);
        } else
            logService.logBefore(joinPoint);
    }

    @AfterThrowing(pointcut = "log() && !notLog()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log annotation = method.getAnnotation(Log.class);
        if (null != annotation) {
            AuditFactory.AuditType auditType = annotation.type();
            if (Boolean.TRUE.equals(auditType.name().equals(AuditFactory.AuditType.ALL.name())) ||
                    Boolean.TRUE.equals(auditType.name().equals(AuditFactory.AuditType.AFTER_TROWING.name())))
                logService.logAfterThrowing(joinPoint, exception);
        } else
            logService.logAfterThrowing(joinPoint, exception);

    }
}

package com.nicico.cost.audit.aop;


import com.nicico.cost.audit.service.AuditService;
import com.nicico.cost.framework.service.exception.ServiceException;
import com.nicico.cost.framework.packages.audit.view.AuditExceptionVM;
import com.nicico.cost.framework.packages.audit.view.AuditFactory;
import com.nicico.cost.framework.packages.audit.view.AuditHeader;
import com.nicico.cost.framework.utility.ApplicationRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static com.nicico.cost.framework.config.general.GeneralStatic.*;

@Aspect
@Component
@RequiredArgsConstructor
public class CrossCutting {


    private final ApplicationRequest request;
    private final AuditService audit;


    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void service() {
        // Do Nothing ,Aop Running
    }

    @Pointcut("within(@com.nicico.cost.audit.anotations.Log *)")
    public void log() {
        // Do Nothing ,Aop Running
    }


    @AfterReturning(pointcut = "service() || log()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String clazz = joinPoint.getSignature().getDeclaringTypeName();
        List<Object> inputs = Arrays.asList(joinPoint.getArgs());
        AuditHeader header = AuditHeader.builder()
                .appName(applicationName)
                .aClass(clazz)
                .method(methodName)
                .correlationId(request.getHeader(CORRELATION_ID))
                .instanceId(instanceId)
                .time(new Timestamp(System.currentTimeMillis()))
                .status(AuditFactory.Status.OK)
                .level(LogLevel.INFO)
                .type(AuditFactory.AuditType.AFTER_RETURNING)
                .uri(request.getRequestURI())
                .token(request.getHeader(AUTHORIZATION))
                .build();
        AuditFactory.AuditVM auditVM = AuditFactory.builder().header(header).ok(result).inputs(inputs).build();
        audit.info(auditVM);
    }

    @Before("service() || log()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String clazz = joinPoint.getSignature().getDeclaringTypeName();
        List<Object> input = Arrays.asList(joinPoint.getArgs());
        AuditHeader header = AuditHeader.builder()
                .appName(applicationName)
                .aClass(clazz)
                .method(methodName)
                .correlationId(request.getHeader(CORRELATION_ID))
                .instanceId(instanceId)
                .time(new Timestamp(System.currentTimeMillis()))
                .status(AuditFactory.Status.OK)
                .level(LogLevel.INFO)
                .type(AuditFactory.AuditType.BEFORE)
                .uri(request.getRequestURI())
                .token(request.getHeader(AUTHORIZATION))
                .build();
        AuditFactory.AuditVM auditVM = AuditFactory.builder().header(header).inputs(input).build();
        audit.info(auditVM);
    }

    @AfterThrowing(pointcut = "service() || log()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String clazz = joinPoint.getSignature().getDeclaringTypeName();
        List<Object> input = Arrays.asList(joinPoint.getArgs());
        AuditExceptionVM auditException;
        if (exception instanceof ServiceException) {
            auditException = AuditExceptionVM.builder().excClazz(exception.getStackTrace()[0].getClassName())
                    .excMethod(exception.getStackTrace()[0].getMethodName())
                    .excLine(exception.getStackTrace()[0].getLineNumber())
                    .excMessage(((ServiceException) exception).getExceptionMessage()).excCode(((ServiceException) exception).getExceptionCode()).build();
        } else {
            auditException = AuditExceptionVM.builder().excClazz(exception.getStackTrace()[0].getClassName())
                    .excMethod(exception.getStackTrace()[0].getMethodName())
                    .excLine(exception.getStackTrace()[0].getLineNumber())
                    .excMessage(exception.getMessage()).build();
        }
        AuditHeader header = AuditHeader.builder()
                .appName(applicationName)
                .aClass(clazz)
                .method(methodName)
                .correlationId(request.getHeader(CORRELATION_ID))
                .instanceId(instanceId)
                .time(new Timestamp(System.currentTimeMillis()))
                .status(AuditFactory.Status.ERROR)
                .level(LogLevel.INFO)
                .type(AuditFactory.AuditType.AFTER_RETURNING)
                .uri(request.getRequestURI())
                .token(request.getHeader(AUTHORIZATION))
                .build();
        AuditFactory.AuditVM auditVM = AuditFactory.builder().header(header).inputs(input).exception(auditException).build();
        audit.error(auditVM);
    }


}

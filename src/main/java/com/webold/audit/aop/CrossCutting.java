package com.webold.audit.aop;

import com.webold.core.packages.audit.view.AuditHeader;
import com.webold.core.packages.audit.view.AuditVM;
import com.webold.core.utility.ApplicationRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static com.webold.core.config.general.GeneralStatic.APP_KEY;
import static com.webold.core.config.general.GeneralStatic.CORRELATION_ID;

@Aspect
@Component
public class CrossCutting {


    @Value("${spring.application.name}")
    private String microserviceName;

    private final ApplicationRequest request;

    public CrossCutting(ApplicationRequest request) {
        this.request = request;
    }


    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void service() {
        // Do Nothing ,Aop Running
    }

    @Pointcut("within(@com.webold.audit.anotations.Log *)")
    public void log() {
        // Do Nothing ,Aop Running
    }


    @AfterReturning(pointcut = "service() || log()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String clazz = joinPoint.getSignature().getDeclaringTypeName();
        List<Object> input = Arrays.asList(joinPoint.getArgs());
        AuditHeader.builder()
                .appName(microserviceName)
                .aClass(clazz)
                .method(methodName)
                .correlationId(request.getHeader(CORRELATION_ID))
                .instanceId()
        AuditVM auditReqVM = AuditVM.builder().input(input)
                .method(methodName)
                .clazz(clazz)
                .microServiceName(microserviceName)
                .result(result)
                .rrn(applicationRequest.getHeader(RRN))
                .appKey(applicationRequest.getHeader(APP_KEY))
                .token(applicationRequest.getHeader(AUTHORIZATION))
                .type(AuditType.AFTER_RETURNING)
                .level(LogLevel.INFO.name())
                .time(new SimpleDateFormat(DATE_PATTERN).format(new Timestamp(System.currentTimeMillis())))
                .build();
        applicationLogger.log(auditReqVM, LogLevel.INFO);
    }

    @Before("service() || log()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String clazz = joinPoint.getSignature().getDeclaringTypeName();
        List<Object> input = Arrays.asList(joinPoint.getArgs());
        AuditReqVM auditReqVM = AuditReqVM.builder().input(input)
                .method(methodName)
                .clazz(clazz)
                .microServiceName(microserviceName)
                .rrn(applicationRequest.getHeader(RRN))
                .appKey(applicationRequest.getHeader(APP_KEY))
                .token(applicationRequest.getHeader(AUTHORIZATION))
                .type(AuditType.BEFORE)
                .level(LogLevel.INFO.name())
                .time(new SimpleDateFormat(DATE_PATTERN).format(new Timestamp(System.currentTimeMillis())))
                .build();
        applicationLogger.log(auditReqVM, LogLevel.INFO);
    }

    @AfterThrowing(pointcut = "service() || log()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String clazz = joinPoint.getSignature().getDeclaringTypeName();
        List<Object> input = Arrays.asList(joinPoint.getArgs());
        AuditException auditException;
        if (exception instanceof ServiceException) {
            auditException = AuditException.builder().excClazz(exception.getStackTrace()[0].getClassName())
                    .excMethod(exception.getStackTrace()[0].getMethodName())
                    .excLine(exception.getStackTrace()[0].getLineNumber())
                    .excMessage(((ServiceException) exception).getExceptionMessage()).excCode(((ServiceException) exception).getExceptionCode()).build();
        } else {
            auditException = AuditException.builder().excClazz(exception.getStackTrace()[0].getClassName())
                    .excMethod(exception.getStackTrace()[0].getMethodName())
                    .excLine(exception.getStackTrace()[0].getLineNumber())
                    .excMessage(exception.getMessage()).build();
        }
        AuditReqVM auditReqVM = AuditReqVM.builder().input(input)
                .method(methodName)
                .clazz(clazz)
                .microServiceName(microserviceName)
                .rrn(applicationRequest.getHeader(RRN))
                .appKey(applicationRequest.getHeader(APP_KEY))
                .token(applicationRequest.getHeader(AUTHORIZATION))
                .type(AuditType.AFTER_TROWING)
                .level(LogLevel.ERROR.name())
                .exception(auditException)
                .time(new SimpleDateFormat(DATE_PATTERN).format(new Timestamp(System.currentTimeMillis())))
                .build();
        applicationLogger.log(auditReqVM, LogLevel.ERROR);
    }


}

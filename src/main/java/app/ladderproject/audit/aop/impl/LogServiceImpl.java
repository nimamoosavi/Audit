package app.ladderproject.audit.aop.impl;

import app.ladderproject.audit.aop.LogService;
import app.ladderproject.audit.service.AuditService;
import app.ladderproject.core.packages.audit.view.AuditExceptionVM;
import app.ladderproject.core.packages.audit.view.AuditFactory;
import app.ladderproject.core.packages.audit.view.AuditHeader;
import app.ladderproject.core.service.exception.ServiceException;
import app.ladderproject.core.utility.RequestUtility;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static app.ladderproject.core.config.general.GeneralStatic.*;

@Component
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final RequestUtility request;
    private final AuditService audit;

    @Override
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

    @Override
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

    @Override
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

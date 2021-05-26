package com.nicico.cost.audit.aop;

import org.aspectj.lang.JoinPoint;

/**
 * @author nima
 * @apiNote this services used for Log in aop Service
 * @since 1.8
 */
public interface LogService {
    /**
     * @param joinPoint is a point of execution in the base code where the advice specified in a corresponding pointcut is applied.
     * @param result    the Object that response from method
     */
    void logAfterReturning(JoinPoint joinPoint, Object result);

    /**
     * @param joinPoint is a point of execution in the base code where the advice specified in a corresponding pointcut is applied.
     */
    void logBefore(JoinPoint joinPoint);

    /**
     * @param joinPoint is a point of execution in the base code where the advice specified in a corresponding pointcut is applied.
     * @param exception is the exception you must throw in aop
     */
    void logAfterThrowing(JoinPoint joinPoint, Throwable exception);
}

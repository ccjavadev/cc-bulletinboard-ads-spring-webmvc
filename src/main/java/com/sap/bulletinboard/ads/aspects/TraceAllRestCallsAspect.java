package com.sap.bulletinboard.ads.aspects;

import static com.sap.bulletinboard.ads.aspects.AspectUtils.getLoggerForJoinPoint;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;

@Aspect
/**
 * Scenario 1: Simple trace in all REST resource methods Demonstrate via GET to api/v1.0/ads
 */
public class TraceAllRestCallsAspect {
    /**
     * classes annotated with @RestController (i.e., JAX RS resources)
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restResource() {
    }

    /**
     * public methods
     */
    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    /**
     * The actual AspectJ advice to be executed before each method, for which both the pointcut definition is fulfilled.
     * Uses MethodJoinPointWriter to log a method trace statement
     */
    @Before("restResource() && publicMethod()")
    public void traceRestMethodInvocations(JoinPoint joinPoint) throws Throwable {
        Logger logger = getLoggerForJoinPoint(joinPoint);
        MethodJoinPointWriter methodWriter = new MethodJoinPointWriter(joinPoint);

        logger.trace("tracing rest method: {}", methodWriter.describeMethod());
    }
}
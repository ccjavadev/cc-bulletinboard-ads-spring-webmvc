package com.sap.bulletinboard.ads.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;

@Aspect
/**
 * 
 * Scenario 2: Output all setter method executions in package 'model' including parameters (excluding methods or
 * parameters annotated with @DoNotLog).
 * 
 * Demonstrate via POST with parameters (contact details will not be logged) 
 * { "title": "xyz", 
 *   "price": "9.99",
 *   "contact": "privacy@somemail.org" } to api/v1.0/ads
 */
public class TraceModelSetterParametersAspect {
    /**
     * public methods with name starting with 'set'
     */
    @Pointcut("execution(public * set*(..))")
    public void publicSetterMethod() {
    }

    /**
     * all classes in models package
     */
    @Pointcut("within(com.sap.bulletinboard.ads.models.*)")
    public void inModelsPackage() {
    }

    /**
     * annotated with {@link DoNotLog}
     */
    @Pointcut("@annotation(com.sap.bulletinboard.ads.aspects.DoNotLog)")
    public void doNotLog() {
    }

    /**
     * The actual AspectJ advice to be executed before each matching method. Uses MethodJoinPointWriter to log a method
     * trace statement including parameter values.
     */
    @Before("publicSetterMethod() && inModelsPackage() && !doNotLog()")
    public void logAllArgumentsForMethods(JoinPoint joinPoint) throws Throwable {
        Logger logger = AspectUtils.getLoggerForJoinPoint(joinPoint);
        MethodJoinPointWriter methodWriter = new MethodJoinPointWriter(joinPoint);

        logger.debug("tracing method with arguments: {}", methodWriter.describeMethod());
    }

}
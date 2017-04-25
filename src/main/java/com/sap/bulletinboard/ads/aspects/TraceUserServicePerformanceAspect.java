package com.sap.bulletinboard.ads.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;

import com.sap.bulletinboard.ads.services.UserServiceClient.User;

@Aspect
/**
 * Scenario 3: Performance evaluation of single method call (UserServiceClient.getById)
 *
 */
public class TraceUserServicePerformanceAspect {

    /**
     * execution of public method with name getById
     */
    @Pointcut("execution(public * isPremiumUser(..))")
    public void getByIdMethod() {
    }

    /**
     * in class UserServiceClient
     */
    @Pointcut("within(com.sap.bulletinboard.ads.services.UserServiceClient)")
    public void inClassUserServiceClient() {
    }

    /**
     * Trace method for UserServiceClient.getById, in which the User-Id, the Premium-User-flag and the duration of the
     * UserService call are to be logged.
     * 
     * The @Around advice is used to completely wrap a call. This example also demonstrates how to access the return
     * value of the actual call.
     */
    @Around("getByIdMethod() && inClassUserServiceClient()")
    public Object logPerformanceOfUserServiceClient(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // Invokes the actual method execution
        Object result = joinPoint.proceed();

        long durationInMs = System.currentTimeMillis() - startTime;

        writeLogData(joinPoint, result, durationInMs);

        // Needs to return the result of the method, since @Around wraps the
        // actual method execution
        return result;
    }

    private void writeLogData(ProceedingJoinPoint joinPoint, Object result, long durationInMs) {
        Logger logger = AspectUtils.getLoggerForJoinPoint(joinPoint);

        String userRepresentation;
        String isPremiumUser;
        if (result instanceof User) {
            User user = (User) result;
            // TODO move user to models and add id property
            // userRepresentation = user.getId();
            userRepresentation = "null";
            isPremiumUser = String.valueOf(user.premiumUser);
        } else {
            userRepresentation = "null";
            isPremiumUser = "null";
        }

        logger.debug("tracing users service -  user: {}, premiumUser: {}, duration [ms]: {}", userRepresentation,
                isPremiumUser, durationInMs);
    }

}
package com.sap.bulletinboard.ads.aspects;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectUtils {

    /**
     * returns the Logger for the class in which the JoinPoint is located
     */
    public static Logger getLoggerForJoinPoint(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getStaticPart().getSourceLocation().getWithinType();

        Logger logger = LoggerFactory.getLogger(clazz);
        return logger;
    }

}

package com.sap.bulletinboard.ads.aspects;

import static org.aspectj.lang.JoinPoint.METHOD_EXECUTION;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Stream;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Outputs a method name, type, signature and arguments from an AspectJ JoinPoint.
 */
public class MethodJoinPointWriter {
    private static final String DO_NOT_LOG = "@" + DoNotLog.class.getSimpleName();
    private JoinPoint joinPoint;

    MethodJoinPointWriter(JoinPoint joinPoint) {
        this.joinPoint = joinPoint;
        if (noMethodExecution()) {
            throw new RuntimeException("Invalid JoinPoint, requires METHOD_EXECUTION");
        }
    }

    private boolean noMethodExecution() {
        return !(METHOD_EXECUTION.equals(joinPoint.getKind()) && joinPoint.getSignature() instanceof MethodSignature);
    }

    /**
     * Returns method signature and arguments as String:
     * "<ReturnType> <DeclaringClass>.<MethodName> (<ParameterType> [<ParameterName>] = <ParameterValue>)"
     */
    String describeMethod() {
        Method method = getMethod();
        return method.getReturnType().getName() + " " + method.getDeclaringClass().getName() + "." + method.getName()
                + " (" + getMethodArguments() + ")";
    }

    private String getMethodArguments() {
        StringBuilder stringBuilder = new StringBuilder();
        Object[] arguments = joinPoint.getArgs();
        Method method = getMethod();

        for (int argumentIndex = 0; argumentIndex < method.getParameterCount(); argumentIndex++) {
            appendArgumentText(stringBuilder, arguments, method, argumentIndex);
        }
        return stringBuilder.toString();
    }

    private void appendArgumentText(StringBuilder stringBuilder, Object[] arguments, Method method, int argumentIndex) {
        if (argumentIndex > 0) {
            stringBuilder.append(", ");
        }

        Parameter parameter = method.getParameters()[argumentIndex];
        stringBuilder.append(parameter.getType().getName());
        stringBuilder.append(getParameterName(parameter));
        stringBuilder.append(" = ");
        stringBuilder
                .append(getParameterValue(arguments[argumentIndex], method.getParameterAnnotations()[argumentIndex]));
    }

    private String getParameterName(Parameter parameter) {
        return parameter.isNamePresent() ? " " + parameter.getName() : "";
    }

    private String getParameterValue(Object argument, Annotation[] annotations) {
        if (containsDoNotLog(annotations)) {
            return DO_NOT_LOG;
        } else {
            if (argument != null)
                return argument.toString();
            else
                return "argument is null";
        }
    }

    private boolean containsDoNotLog(Annotation[] annotations) {
        Stream<Annotation> annotationStream = Arrays.asList(annotations).stream();
        return annotationStream.anyMatch(annotation -> annotation.annotationType().equals(DoNotLog.class));
    }

    private Method getMethod() {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }
}
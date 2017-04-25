package com.sap.bulletinboard.ads.aspects;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Disables logging of annotated parameters or methods when logging with AspectJ.
 */
@Target({ PARAMETER, METHOD })
@Retention(RUNTIME)
public @interface DoNotLog {
}

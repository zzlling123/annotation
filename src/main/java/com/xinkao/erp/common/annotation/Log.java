package com.xinkao.erp.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.xinkao.erp.common.enums.system.OperationType;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    
    String content() default "";
    
    OperationType operationType() default OperationType.OTHER;
    
    boolean isSaveRequestData() default true;

    
    boolean isSaveResponseData() default true;
}

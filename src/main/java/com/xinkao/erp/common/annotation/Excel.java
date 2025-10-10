package com.xinkao.erp.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel {
    
     String prompt() default "";

     
     String readConverterExp() default "";

    
     String separator() default ",";
     
     String [] dict() default "";
}

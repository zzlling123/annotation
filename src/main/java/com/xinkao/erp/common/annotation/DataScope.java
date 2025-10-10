package com.xinkao.erp.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope
{
    public String role() default "";

//    public String userAlias() default "";
//
//	public boolean isUser() default false;
}

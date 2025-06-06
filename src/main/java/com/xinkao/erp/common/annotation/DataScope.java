package com.xinkao.erp.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope
{
    /**
     * 角色列表
     */
    public String role() default "";

//    /**
//     * 用户表的别名
//     */
//    public String userAlias() default "";
//
//	/**
//	 * 是否过滤用户权限
//	 */
//	public boolean isUser() default false;
}

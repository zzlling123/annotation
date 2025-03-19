package com.xinkao.erp.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 导出excel注解
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel {
    /**
     * 提示信息
     */
     String prompt() default "";

     /**
     * 读取内容转表达式 (如: 0=否,1=是)
     */
     String readConverterExp() default "";

    /**
     * 分隔符，读取字符串组内容
     */
     String separator() default ",";
     /**
      * 下拉列表,使用逗号分隔符
      */
     String [] dict() default "";
}

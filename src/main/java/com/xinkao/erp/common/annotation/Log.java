package com.xinkao.erp.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.xinkao.erp.common.enums.system.OperationType;

/**
 * 操作日志记录注解
 * <p>根据实际的业务,可新增业务操作类型</p>
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 日志模块内容
     * @return
     */
    String content() default "";
    /**
     * 业务操作类型
     * @return
     */
    OperationType operationType() default OperationType.OTHER;
    /**
     * 是否保留请求的参数
     * @return
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保留相应参数
     */
    boolean isSaveResponseData() default true;
}

package com.xinkao.erp.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.xinkao.erp.common.enums.busi.UserLevelEnum;

/**
 * 数据权限验证
 * @author hys_thanks
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DataAuth {
	/**
	 * 标注权限的范围列表:10-学校 20-区县 30-市局
	 * @return
	 */
	UserLevelEnum[] authList();
}

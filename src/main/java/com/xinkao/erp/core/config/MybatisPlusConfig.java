package com.xinkao.erp.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.xinkao.erp.common.enums.system.TableSplitEnum;
import com.xinkao.erp.core.mybatisplus.handler.DynamicTableHolder;

import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class MybatisPlusConfig {
	
	private List<String> splitTableList = new ArrayList<String>(
			Arrays.asList(
					TableSplitEnum.SYS_USER_OPT_LOG.getTableName(),
					TableSplitEnum.SYS_USER_LOGIN_LOG.getTableName(),
					TableSplitEnum.KW_SITE_USER_OPT_LOG.getTableName(),
					TableSplitEnum.KW_SITE_USER_LOGIN_LOG.getTableName(),
					TableSplitEnum.EXAM_STUDENT_SCORE_LOG.getTableName(),
					TableSplitEnum.KW_STUDENT.getTableName(),
					TableSplitEnum.KW_STUDENT_CARD.getTableName(),
					TableSplitEnum.KW_STUDENT_SCORE.getTableName(),
					TableSplitEnum.KW_STUDENT_SCORE_UPLOAD_LOG.getTableName()
					));

    /**
     * 插件
     * @return
     */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
		dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
			String dynamicTableName = DynamicTableHolder.get();
			return StringUtils.isNotEmpty(dynamicTableName) ? dynamicTableName : tableName;
		});
		// 设置动态表名拦截器
		interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
		return interceptor;
	}

}

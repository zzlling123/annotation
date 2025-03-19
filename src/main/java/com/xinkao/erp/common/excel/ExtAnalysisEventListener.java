package com.xinkao.erp.common.excel;

import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

public abstract class ExtAnalysisEventListener<T> extends AnalysisEventListener<T> {

    public void valid(T t) {
        Field[] fields = t.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                ReflectionUtils.makeAccessible(field);
                NotNull notNull = field.getAnnotation(NotNull.class);
                if (notNull != null) {
                    Object object = ReflectionUtils.getField(field, t);
                    if (object == null || !StringUtils.hasText(object.toString())) {
                        throw new ExcelAnalysisException(notNull.message());
                    }
                }
            }
        }
    }

}

package com.xinkao.erp.common.model.support;

import com.xinkao.erp.common.util.ReflectionUtil;
import com.xinkao.erp.common.util.mapper.BeanMapper;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import org.springframework.lang.Nullable;
public interface InputConverter<D> {

    default D convertTo() {
        ParameterizedType currentType = parameterizedType();

        Objects.requireNonNull(currentType,
            "参数类型不能为空");

        Class<D> domainClass = (Class<D>) currentType.getActualTypeArguments()[0];

        return BeanMapper.map(this, domainClass);
    }

    @Nullable
    default ParameterizedType parameterizedType() {
        return ReflectionUtil.getParameterizedType(InputConverter.class, this.getClass());
    }
}

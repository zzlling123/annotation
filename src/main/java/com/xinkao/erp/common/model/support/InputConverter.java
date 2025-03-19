package com.xinkao.erp.common.model.support;

import com.xinkao.erp.common.util.ReflectionUtil;
import com.xinkao.erp.common.util.mapper.BeanMapper;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import org.springframework.lang.Nullable;

/**
 * dozer转换接口
 **/
public interface InputConverter<D> {

    default D convertTo() {
        // 获取其参数化的类型
        ParameterizedType currentType = parameterizedType();

        Objects.requireNonNull(currentType,
            "参数类型不能为空");

        Class<D> domainClass = (Class<D>) currentType.getActualTypeArguments()[0];

        return BeanMapper.map(this, domainClass);
    }

    /**
     * 获取当前类的参数化类
     * @return
     */
    @Nullable
    default ParameterizedType parameterizedType() {
        return ReflectionUtil.getParameterizedType(InputConverter.class, this.getClass());
    }
}

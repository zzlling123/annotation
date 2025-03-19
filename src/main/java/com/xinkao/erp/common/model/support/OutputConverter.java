package com.xinkao.erp.common.model.support;

import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import com.xinkao.erp.common.util.mapper.BeanMapper;

public interface OutputConverter<VoT extends OutputConverter<VoT, D>, D>  {

    @NonNull
    default VoT convertFrom(@NonNull D domain) {
        Assert.notNull(domain, "源数据不能为空");
        BeanMapper.map(domain, this);
        return (VoT) this;
    }
}

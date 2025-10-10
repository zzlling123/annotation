package com.xinkao.erp.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.query.SymbolQuery;
import com.xinkao.erp.question.entity.Symbol;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.question.param.SymbolParam;

public interface SymbolService extends BaseService<Symbol> {

    Page<Symbol> page(SymbolQuery query, Pageable pageable);

    BaseResponse<?> save(SymbolParam param);

    BaseResponse<?> update(SymbolParam param);

    BaseResponse<?> del(Integer id);
}

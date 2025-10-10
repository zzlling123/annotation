package com.xinkao.erp.manual.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.manual.entity.Manual;
import com.xinkao.erp.manual.param.ManualParam;
import com.xinkao.erp.manual.query.ManualQuery;
import com.xinkao.erp.manual.vo.ManualVo;

public interface ManualService extends BaseService<Manual> {

    Page<ManualVo> page(ManualQuery query);

    BaseResponse<?> save(ManualParam manualParam);

    BaseResponse<?> update(ManualParam manualParam);

    BaseResponse<?> del(DeleteParam param);

    ManualVo getByUserType();
} 
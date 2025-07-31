package com.xinkao.erp.manual.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.manual.entity.Manual;
import com.xinkao.erp.manual.param.ManualParam;
import com.xinkao.erp.manual.query.ManualQuery;
import com.xinkao.erp.manual.vo.ManualVo;

import java.util.List;

/**
 * <p>
 * 使用文档表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-07-26
 */
public interface ManualService extends BaseService<Manual> {

    /**
     * 分页查询使用文档
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<ManualVo> page(ManualQuery query);

    /**
     * 新增使用文档
     *
     * @param manualParam 文档参数
     * @return 操作结果
     */
    BaseResponse<?> save(ManualParam manualParam);

    /**
     * 修改使用文档
     *
     * @param manualParam 文档参数
     * @return 操作结果
     */
    BaseResponse<?> update(ManualParam manualParam);

    /**
     * 批量删除使用文档
     *
     * @param param 文档ID列表
     * @return 操作结果
     */
    BaseResponse<?> del(DeleteParam param);

    /**
     * 根据用户类型获取文档信息
     *

     * @return 文档信息（单个）
     */
    ManualVo getByUserType();
} 
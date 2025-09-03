package com.xinkao.erp.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.query.SymbolQuery;
import com.xinkao.erp.question.entity.Symbol;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.question.param.SymbolParam;

/**
 * <p>
 * 题目标记名称表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-09-03 19:13:25
 */
public interface SymbolService extends BaseService<Symbol> {

    /**
     * 分页查询标记信息
     *
     * @param query 查询条件
     * @param pageable 分页信息
     * @return 分页结果
     */
    Page<Symbol> page(SymbolQuery query, Pageable pageable);

    /**
     * 新增标记
     *
     * @param param 标记信息
     * @return 操作结果
     */
    BaseResponse<?> save(SymbolParam param);

    /**
     * 编辑标记
     *
     * @param param 标记信息
     * @return 操作结果
     */
    BaseResponse<?> update(SymbolParam param);

    /**
     * 删除标记
     *
     * @param id 标记ID
     * @return 操作结果
     */
    BaseResponse<?> del(Integer id);
}

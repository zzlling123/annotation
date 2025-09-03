package com.xinkao.erp.question.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.query.SymbolQuery;
import com.xinkao.erp.exam.vo.ExamPageVo;
import com.xinkao.erp.manage.entity.ClassInfo;
import com.xinkao.erp.question.entity.Symbol;
import com.xinkao.erp.question.mapper.SymbolMapper;
import com.xinkao.erp.question.param.SymbolParam;
import com.xinkao.erp.question.service.SymbolService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 题目标记名称表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-09-03 19:13:25
 */
@Service
public class SymbolServiceImpl extends BaseServiceImpl<SymbolMapper, Symbol> implements SymbolService {

    @Autowired
    private SymbolMapper symbolMapper;

    @Override
    public Page<Symbol> page(SymbolQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return symbolMapper.page(page, query);
    }

    @Override
    @Transactional
    public BaseResponse<?> save(SymbolParam param) {
        Symbol symbol = BeanUtil.copyProperties(param, Symbol.class);
        if (lambdaQuery().eq(Symbol::getSymbolName, symbol.getSymbolName()).count() > 0){
            return BaseResponse.fail("该名称已存在");
        }
        return save(symbol) ? BaseResponse.ok("保存成功") : BaseResponse.fail("保存失败");
    }

    @Override
    @Transactional
    public BaseResponse update(SymbolParam param) {
        Symbol symbol = BeanUtil.copyProperties(param, Symbol.class);
        if (lambdaQuery().eq(Symbol::getSymbolName, symbol.getSymbolName()).ne(Symbol::getId, symbol.getId()).count() > 0){
            return BaseResponse.fail("该名称已存在");
        }
        return updateById(symbol) ? BaseResponse.ok("修改成功") : BaseResponse.fail("修改失败");
    }

    @Override
    @Transactional
    public BaseResponse del(Integer id) {
        return lambdaUpdate().set(Symbol::getIsDel, 1).eq(Symbol::getId, id).update() ? BaseResponse.ok("删除成功") : BaseResponse.fail("删除失败");
    }
}

package com.xinkao.erp.question.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.query.SymbolQuery;
import com.xinkao.erp.exam.vo.ExamPageVo;
import com.xinkao.erp.question.entity.Symbol;
import com.xinkao.erp.common.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface SymbolMapper extends BaseMapper<Symbol> {

    Page<Symbol> page(Page page, SymbolQuery query);
}

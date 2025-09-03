package com.xinkao.erp.question.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.question.entity.Symbol;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SymbolParam implements InputConverter<Symbol> {

    @ApiModelProperty("ID(编辑时修改)")
    private Integer id;

    /**
     * 题目来源名称
     */
    @TableField("symbol_name")
    private String symbolName;
}
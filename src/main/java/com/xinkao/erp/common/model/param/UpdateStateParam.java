package com.xinkao.erp.common.model.param;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;


@Setter
@Getter
public class UpdateStateParam {

    @NotEmpty(message = "所选内容不能为空")
    private String ids;
    
    @NotEmpty(message = "状态标识不能为空")
    private String state;


}

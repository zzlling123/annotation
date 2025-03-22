package com.xinkao.erp.common.model.param;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@Setter
@Getter
public class DeleteParam {

    private List<String> ids;


}

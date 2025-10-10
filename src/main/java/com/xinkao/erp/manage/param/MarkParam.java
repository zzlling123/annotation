package com.xinkao.erp.manage.param;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.user.entity.Menu;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class MarkParam implements InputConverter<Mark> {

	private String id;
	
	@NotEmpty(message = "标记名称不能为空")
	private String markName;

	
	private String type;

	
	@NotEmpty(message = "父级标记不能为空")
	private String pid;

	
	@NotEmpty(message = "排序不能为空")
	private String sort;
}

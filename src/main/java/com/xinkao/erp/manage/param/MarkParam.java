package com.xinkao.erp.manage.param;

import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.user.entity.Menu;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 标记新增或更新实体
 * @author Ldy
 */
@Data
public class MarkParam implements InputConverter<Mark> {

	//修改时有值
	private String id;
	/**
	 * 标记名称
	 */
	@NotEmpty(message = "标记名称不能为空")
	private String markName;

	/**
	 * 标记Id
	 */
	private String type;

	/**
	 * 父级标记
	 */
	@NotEmpty(message = "父级标记不能为空")
	private String pid;

	/**
	 * 排序从小到大
	 */
	@NotEmpty(message = "排序不能为空")
	private String sort;
}

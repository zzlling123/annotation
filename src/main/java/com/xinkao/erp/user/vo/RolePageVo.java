package com.xinkao.erp.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinkao.erp.common.model.support.OutputConverter;
import com.xinkao.erp.user.entity.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@ApiModel("角色查询实体-VO")
public class RolePageVo implements OutputConverter<RolePageVo, Role> {

	@ApiModelProperty("角色ID")
	private String id;

	@ApiModelProperty("角色名称")
	private String roleName;


	@ApiModelProperty("创建人")
	private String createBy;

	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private String createTime;
}

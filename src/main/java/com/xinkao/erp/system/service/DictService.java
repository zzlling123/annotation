package com.xinkao.erp.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.busi.DictTypeEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.system.entity.Dict;


public interface DictService extends BaseService<Dict> {
	
	Page<Dict> pageDict(String type, String dictValue, Pageable pageable);
	
	List<Dict> selectBy(String type);
	
	Dict selectOne(String type);
    
    boolean saveBy(String type, String name, String dictValue);
    
	boolean deleteByIds(String ids);
	
	Dict setDictBy(String type, String name, String dictValue);

	BaseResponse<?> updateState(UpdateStateParam updateStateParam);
}

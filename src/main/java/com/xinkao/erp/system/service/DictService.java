package com.xinkao.erp.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.busi.DictTypeEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.system.entity.Dict;

/**
 * 字典相关的服务
 **/
public interface DictService extends BaseService<Dict> {
	/**
	 * 分页查询字典列表
	 * @param code 类型
	 * @param dictValue 模糊查询字典值
	 * @param pageable
	 * @return
	 */
	Page<Dict> pageDict(String code, String dictValue, Pageable pageable);
	/**
	 * 查询字典列表
	 * @param type 类型
	 * @return
	 */
	List<Dict> selectBy(String type);
	/**
	 * 查询单个字典的类型
	 * @param type
	 * @return
	 */
	Dict selectOne(String type);
    /**
     * 保存字典
     * @param type 类型
     * @param dictValue 字典值
     * @return
     */
    boolean saveBy(String type, String name, String dictValue);
    /**
     * 删除字典
     * @param ids
     * @return
     */
	boolean deleteByIds(String ids);
	/**
	 * 设置字典值,默认为空
	 * @param type
	 * @param dictValue
	 * @return
	 */
	Dict setDictBy(String type, String name, String dictValue);

	BaseResponse<?> updateState(UpdateStateParam updateStateParam);
}

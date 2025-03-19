package com.xinkao.erp.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.busi.DictTypeEnum;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.system.entity.Dict;

/**
 * 字典相关的服务
 **/
public interface DictService extends BaseService<Dict> {
	/**
	 * 分页查询字典列表
	 * @param type 类型
	 * @param dictValue 模糊查询字典值
	 * @param pageable
	 * @return
	 */
	Page<Dict> pageDict(DictTypeEnum type, String dictValue, Pageable pageable);
	/**
	 * 查询字典列表
	 * @param type 类型
	 * @return
	 */
	List<Dict> selectBy(DictTypeEnum type);
	/**
	 * 查询单个字典的类型
	 * @param type
	 * @return
	 */
	Dict selectOne(DictTypeEnum type);
    /**
     * 保存字典
     * @param type 类型
     * @param dictValue 字典值
     * @return
     */
    boolean saveBy(DictTypeEnum type, String dictValue);
    /**
     * 删除字典
     * @param id
     * @return
     */
	boolean deleteByIds(String ids);
	/**
	 * 设置字典值,默认为空
	 * @param dictType
	 * @param dictValue
	 * @return
	 */
	Dict setDictBy(DictTypeEnum dictType, String dictValue);
}

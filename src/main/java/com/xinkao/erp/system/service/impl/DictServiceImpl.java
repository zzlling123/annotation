package com.xinkao.erp.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.busi.DictTypeEnum;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.system.entity.Dict;
import com.xinkao.erp.system.mapper.DictMapper;
import com.xinkao.erp.system.service.DictService;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 账户相关的实现
 **/
@Service
@Slf4j
public class DictServiceImpl extends BaseServiceImpl<DictMapper, Dict>
    implements DictService {
	/**
	 * 分页查询字典
	 */
	@Override
	public Page<Dict> pageDict(DictTypeEnum type, String dictValue, Pageable pageable) {
		return lambdaQuery()
                .like(StrUtil.isNotEmpty(dictValue), Dict::getDictValue,dictValue)
                .eq(Dict::getDictType, type.getCode())
                .orderByAsc(Dict::getSort)
                .page(pageable.toPage());
	}
	/**
	 * 查询字典列表
	 */
	@Override
	public List<Dict> selectBy(DictTypeEnum type) {
		  return lambdaQuery().eq(Dict::getDictType, type.getCode()).orderByAsc(Dict::getSort).list();
	}
	/**
	 * 保存字典值
	 */
	@Transactional(noRollbackFor = BusinessException.class)
	@Override
	public boolean saveBy(DictTypeEnum type, String dictValue) {
		Dict dict = lambdaQuery().eq(Dict::getDictType, type.getCode())
        .eq(Dict::getDictValue, dictValue)
        .one();
		if(dict != null){
		    throw new BusinessException("此字典值已存在");
		}
		dict = new Dict();
		dict.setDictType(type.getCode());
		dict.setDictLabel(type.getName());
		dict.setDictValue(dictValue);
		Long count = lambdaQuery().eq(Dict::getDictType, type.getCode()).count();
		count = ( count == null ? 0L: count);
		dict.setSort(count.intValue());
		//保存
		save(dict);
	    return true;
	}
	/**
	 * 多个主键删除
	 */
	@Override
	public boolean deleteByIds(String ids) {
		if(StringUtils.isEmpty(ids)) {
			return false;
		}
		String[] idArr = StringUtils.split(ids, ",");
		List<String> idList = Stream.of(idArr).collect(Collectors.toList());
		removeByIds(idList);
		return true;
	}
	/**
	 * 获取单记录
	 */
	@Override
	public Dict selectOne(DictTypeEnum type) {
		return lambdaQuery().eq(Dict::getDictType, type.getCode())
				.last("limit 1").orderByDesc(Dict::getSort).one();
	}
	/**
	 * 设置默认代码
	 */
	@Override
	public Dict setDictBy(DictTypeEnum dictType, String dictValue) {
		Dict dict = lambdaQuery().eq(Dict::getDictType, dictType.getCode())
        .one();
		if(dict != null){
			dict.setDictValue(dictValue);
			//更新
			updateById(dict);
		}else {
			dict = new Dict();
			dict.setDictType(dictType.getCode());
			dict.setDictLabel(dictType.getName());
			dict.setDictValue(dictValue);
			Long count = lambdaQuery().eq(Dict::getDictType, dictType.getCode()).count();
			count = ( count == null ? 0L: count);
			dict.setSort(count.intValue());
			//保存
			save(dict);
		}
		return dict;
	}
}

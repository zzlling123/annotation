package com.xinkao.erp.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.busi.DictTypeEnum;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.model.param.UpdateStateParam;
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


@Service
@Slf4j
public class DictServiceImpl extends BaseServiceImpl<DictMapper, Dict>
    implements DictService {
	
	@Override
	public Page<Dict> pageDict(String type, String dictValue, Pageable pageable) {
		return lambdaQuery()
                .like(StrUtil.isNotEmpty(dictValue), Dict::getDictValue,dictValue)
                .eq(StrUtil.isNotEmpty(type),Dict::getDictType, type)
                .orderByAsc(Dict::getSort)
                .page(pageable.toPage());
	}
	
	@Override
	public List<Dict> selectBy(String type) {
		  return lambdaQuery().eq(Dict::getDictType, type).eq(Dict::getState, 1).orderByAsc(Dict::getSort).list();
	}
	
	@Transactional(noRollbackFor = BusinessException.class)
	@Override
	public boolean saveBy(String type, String name, String dictValue) {
		Dict dict = lambdaQuery().eq(Dict::getDictType, type)
        .eq(Dict::getDictValue, dictValue)
        .one();
		if(dict != null){
		    throw new BusinessException("此字典值已存在");
		}
		dict = new Dict();
		dict.setDictType(type);
		dict.setDictLabel(name);
		dict.setDictValue(dictValue);
		Long count = lambdaQuery().eq(Dict::getDictType, type).count();
		count = ( count == null ? 0L: count);
		dict.setSort(count.intValue());

		save(dict);
	    return true;
	}
	
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
	
	@Override
	public Dict selectOne(String type) {
		return lambdaQuery().eq(Dict::getDictType, type).eq(Dict::getState, 1)
				.last("limit 1").orderByDesc(Dict::getSort).one();
	}
	
	@Override
	public Dict setDictBy(String type, String name, String dictValue) {
		Dict dict = lambdaQuery().eq(Dict::getDictType, type)
        .one();
		if(dict != null){
			dict.setDictValue(dictValue);

			updateById(dict);
		}else {
			dict = new Dict();
			dict.setDictType(type);
			dict.setDictLabel(name);
			dict.setDictValue(dictValue);
			Long count = lambdaQuery().eq(Dict::getDictType, type).count();
			count = ( count == null ? 0L: count);
			dict.setSort(count.intValue());

			save(dict);
		}
		return dict;
	}

	@Override
	public BaseResponse<?> updateState(UpdateStateParam updateStateParam) {
		String[] ids = updateStateParam.getIds().split(",");
		lambdaUpdate().in(Dict::getId, ids).set(Dict::getState, updateStateParam.getState()).update();
		return BaseResponse.ok();
	}
}

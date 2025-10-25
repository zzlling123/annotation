package com.xinkao.erp.manage.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.manage.param.MarkParam;
import com.xinkao.erp.manage.query.MarkQuery;

import java.util.List;

public interface MarkService extends BaseService<Mark> {

    BaseResponse<List<Mark>> getList(MarkQuery query);

    List<Mark> formatMarkList(Integer pid ,List<Mark> menuList);

    List<Mark> formatMarkForStuList(Integer pid ,List<Mark> menuList,List<Integer> ids);

    BaseResponse save(MarkParam departmentParam);

    BaseResponse update(MarkParam departmentParam);

    BaseResponse del(String ids);

    List<Mark> getListByQuestionId(Integer qid);
}

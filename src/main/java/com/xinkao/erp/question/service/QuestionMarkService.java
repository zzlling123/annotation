package com.xinkao.erp.question.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.question.entity.QuestionMark;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.user.entity.Menu;

import java.util.List;

public interface QuestionMarkService extends BaseService<QuestionMark> {

    BaseResponse<List<Mark>> getListByQid(Integer qid);

    List<Mark> formatMarkListByQid(List<Mark> menuList,List<Integer> markIds);
}

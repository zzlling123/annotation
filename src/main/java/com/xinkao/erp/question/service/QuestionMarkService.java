package com.xinkao.erp.question.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.question.entity.QuestionMark;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.user.entity.Menu;

import java.util.List;

/**
 * <p>
 * 题目-标记关联表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-04-20 22:26:27
 */
public interface QuestionMarkService extends BaseService<QuestionMark> {

    /**
     * 根据题目ID获取标记树状图
     *
     * @return 操作结果
     */
    BaseResponse<List<Mark>> getListByQid(Integer qid);

    List<Mark> formatMarkListByQid(List<Mark> menuList,List<Integer> markIds);
}

package com.xinkao.erp.manage.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.manage.param.MarkParam;
import com.xinkao.erp.manage.query.MarkQuery;

import java.util.List;

/**
 * <p>
 * 操作题标记类型表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-04-20 21:22:31
 */
public interface MarkService extends BaseService<Mark> {

    //根据用户权限获取用户菜单
    BaseResponse<List<Mark>> getList(MarkQuery query);

    List<Mark> formatMarkList(Integer pid ,List<Mark> menuList);

    List<Mark> formatMarkForStuList(Integer pid ,List<Mark> menuList,List<Integer> ids);

    //新增
    BaseResponse save(MarkParam departmentParam);

    //修改
    BaseResponse update(MarkParam departmentParam);

    //删除
    BaseResponse del(String ids);

    //根据题目ID获取标记树状图
    List<Mark> getListByQuestionId(Integer qid);
}

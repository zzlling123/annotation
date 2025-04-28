package com.xinkao.erp.manage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.manage.mapper.MarkMapper;
import com.xinkao.erp.manage.param.MarkParam;
import com.xinkao.erp.manage.query.MarkQuery;
import com.xinkao.erp.manage.service.MarkService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.question.entity.QuestionType;
import com.xinkao.erp.question.service.QuestionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 操作题标记类型表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-04-20 21:22:31
 */
@Service
public class MarkServiceImpl extends BaseServiceImpl<MarkMapper, Mark> implements MarkService {

    @Autowired
    private MarkMapper markMapper;
    @Autowired
    private QuestionTypeService questionTypeService;

    //根据用户权限获取用户菜单
    @Override
    public BaseResponse<List<Mark>> getList(MarkQuery query){
        List<Mark> markList = formatMarkList(0,markMapper.getMarkList(query));
        return BaseResponse.ok("成功！",markList);
    }


    //递归获取子集列表
    /**
     * 格式化菜单列表
     *
     * @param markList 菜单列表
     * @return 格式化后的菜单列表
     */
    @Override
    public List<Mark> formatMarkList(Integer pid, List<Mark> markList) {
        List<Mark> treeList = new ArrayList<>();

        // 1. 筛选当前层级的子节点
        for (Mark mark : markList) {
            if (pid.equals(mark.getPid())) {
                treeList.add(mark);

                // 2. 递归获取子节点
                List<Mark> children = formatMarkList(mark.getId(), markList);

                // 3. 设置子节点列表（非空时设置）
                if (!children.isEmpty()) {
                    mark.setChildMarkList(children);
                }
            }
        }
        return treeList;
    }

    @Override
    public BaseResponse save(MarkParam markParam) {
        Mark mark = markParam.convertTo();
        if ("0".equals(markParam.getPid())){
            QuestionType questionType = questionTypeService.getById(markParam.getType());
            mark.setType(questionType.getId());
            mark.setTypeName(questionType.getTypeName());
        }else{
            Mark markPid = getById(markParam.getPid());
            mark.setType(markPid.getType());
            mark.setTypeName(markPid.getTypeName());
        }
        save(mark);
        return BaseResponse.ok("成功！");
    }

    @Override
    public BaseResponse update(MarkParam markParam) {
        Mark mark = markParam.convertTo();
        if ("0".equals(markParam.getPid())){
            QuestionType questionType = questionTypeService.getById(markParam.getType());
            mark.setType(questionType.getId());
            mark.setTypeName(questionType.getTypeName());
        }else{
            Mark markPid = getById(markParam.getPid());
            mark.setType(markPid.getType());
            mark.setTypeName(markPid.getTypeName());
        }
        updateById(mark);
        return BaseResponse.ok("成功！");
    }

    @Override
    public BaseResponse del(String ids) {
        String[] idsStr = ids.split(",");
        lambdaUpdate().in(Mark::getId, idsStr).set(Mark::getIsDel, 1).update();
        return BaseResponse.ok("成功！");
    }
}

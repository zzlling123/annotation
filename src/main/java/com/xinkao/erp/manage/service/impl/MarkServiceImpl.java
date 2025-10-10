package com.xinkao.erp.manage.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.exam.entity.ExamPageUserQuestion;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.manage.mapper.MarkMapper;
import com.xinkao.erp.manage.param.MarkParam;
import com.xinkao.erp.manage.query.MarkQuery;
import com.xinkao.erp.manage.service.MarkService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.question.entity.QuestionMark;
import com.xinkao.erp.question.entity.QuestionType;
import com.xinkao.erp.question.mapper.QuestionMarkMapper;
import com.xinkao.erp.question.service.QuestionMarkService;
import com.xinkao.erp.question.service.QuestionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarkServiceImpl extends BaseServiceImpl<MarkMapper, Mark> implements MarkService {

    @Autowired
    private MarkMapper markMapper;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private QuestionMarkMapper questionMarkMapper;


    @Override
    public BaseResponse<List<Mark>> getList(MarkQuery query){
        List<Mark> markList = formatMarkList(0,markMapper.getMarkList(query));
        return BaseResponse.ok("成功！",markList);
    }

    @Override
    public List<Mark> formatMarkList(Integer pid, List<Mark> markList) {
        List<Mark> treeList = new ArrayList<>();


        for (Mark mark : markList) {
            if (pid.equals(mark.getPid())) {
                treeList.add(mark);


                List<Mark> children = formatMarkList(mark.getId(), markList);


                if (!children.isEmpty()) {
                    mark.setChildMarkList(children);
                }
            }
        }
        return treeList;
    }

    @Override
    public List<Mark> formatMarkForStuList(Integer pid, List<Mark> markList,List<Integer> ids) {
        List<Mark> treeList = new ArrayList<>();


        for (Mark mark : markList) {
            if (pid.equals(mark.getPid())) {
                treeList.add(mark);


                List<Mark> children = formatMarkList(mark.getId(), markList);


                if (!children.isEmpty()) {
                    mark.setChildMarkList(children);
                }
            }
        }
        return treeList;
    }

    @Override
    public BaseResponse<?> save(MarkParam markParam) {
        Mark mark = BeanUtil.copyProperties(markParam, Mark.class);
        if ("0".equals(markParam.getPid())){
            QuestionType questionType = questionTypeService.getById(markParam.getType());
            mark.setType(questionType.getId());
            mark.setTypeName(questionType.getTypeName());
        }else{
            Mark markPid = getById(markParam.getPid());
            mark.setType(markPid.getType());
            mark.setTypeName(markPid.getTypeName());
        }

        String parent;
        String parentRoute;
        if ("0".equals(markParam.getPid())) {
            parent = "0";
            parentRoute = markParam.getMarkName();
        } else {
            Mark markParent = getById(markParam.getPid());
            parent = markParent.getParent() + "," + markParam.getPid();
            parentRoute = markParent.getParentRoute() + "-" + markParam.getMarkName();
        }
        mark.setParent(parent);
        mark.setParentRoute(parentRoute);
        save(mark);
        return BaseResponse.ok("成功！");
    }

    @Override
    public BaseResponse<?> update(MarkParam markParam) {
        Mark mark = BeanUtil.copyProperties(markParam, Mark.class);
        if ("0".equals(markParam.getPid())){
            QuestionType questionType = questionTypeService.getById(markParam.getType());
            mark.setType(questionType.getId());
            mark.setTypeName(questionType.getTypeName());
        }else{
            Mark markPid = getById(markParam.getPid());
            mark.setType(markPid.getType());
            mark.setTypeName(markPid.getTypeName());
        }

        String parent;
        String parentRoute;
        if ("0".equals(markParam.getPid())) {
            parent = "0";
            parentRoute = markParam.getMarkName();
        } else {
            Mark markParent = getById(markParam.getPid());
            parent = markParent.getParent() + "," + markParam.getPid();
            parentRoute = markParent.getParentRoute() + "-" + markParam.getMarkName();
        }
        mark.setParent(parent);
        mark.setParentRoute(parentRoute);
        updateById(mark);
        return BaseResponse.ok("成功！");
    }

    @Override
    public BaseResponse<?> del(String ids) {
        String[] idsStr = ids.split(",");
        lambdaUpdate().in(Mark::getId, idsStr).set(Mark::getIsDel, 1).update();
        return BaseResponse.ok("成功！");
    }

    @Override
    public List<Mark> getListByQuestionId(Integer id) {
        LambdaQueryWrapper<QuestionMark> questionMarkLambdaQueryWrapper = new LambdaQueryWrapper<>();
        questionMarkLambdaQueryWrapper.eq(QuestionMark::getQid, id);
        List<QuestionMark> questionMarkList = questionMarkMapper.selectList(questionMarkLambdaQueryWrapper);
        List<Integer> questionMarkIdList = questionMarkList.stream().map(QuestionMark::getMid).collect(Collectors.toList());

        List<Mark> markListAll = new ArrayList<>();
        if  (questionMarkIdList.isEmpty()) {
            return markListAll;
        }
        markListAll = lambdaQuery().eq(Mark::getIsDel, 0).in(Mark::getId, questionMarkIdList).list();
        List<Mark> markList;
        for (Integer mid : questionMarkIdList) {
            markList = lambdaQuery().eq(Mark::getIsDel, 0).apply(true,"FIND_IN_SET ('"+mid+"',parent)").list();
            markListAll.addAll(markList);
        }
        return formatMarkList(0,markListAll);
    }


}

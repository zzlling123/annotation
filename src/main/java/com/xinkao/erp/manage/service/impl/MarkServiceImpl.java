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
    @Autowired
    private QuestionMarkMapper questionMarkMapper;

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


    //递归获取子集列表--学生做题页面，pid在dis的白名单中才会显示
    /**
     * 格式化菜单列表
     *
     * @param markList 菜单列表
     * @return 格式化后的菜单列表
     */
    @Override
    public List<Mark> formatMarkForStuList(Integer pid, List<Mark> markList,List<Integer> ids) {
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
        //增加父级部门记录
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
        //获取所有的首级部门
//        List<Mark> markList = lambdaQuery().eq(Mark::getPid, 0).eq(Mark::getIsDel, 0).list();
//        for (Mark mark1 : markList) {//1级
//            //获取所有子集
//            List<Mark> childMarkList = lambdaQuery().eq(Mark::getPid, mark1.getId()).eq(Mark::getIsDel, 0).list();
//            for (Mark mark2 : childMarkList) {//2级
//                String parent2 = mark1.getParent() + "," + mark1.getId();
//                String parentRoute2 = mark1.getParentRoute() + "-" + mark1.getMarkName();
//                mark2.setParent(parent2);
//                mark2.setParentRoute(parentRoute2);
//                updateById(mark2);
//                //获取所有3级
//                List<Mark> childMarkList2 = lambdaQuery().eq(Mark::getPid, mark2.getId()).eq(Mark::getIsDel, 0).list();
//                for (Mark mark3 : childMarkList2) {
//                    String parent3 = mark2.getParent() + "," + mark2.getId();
//                    String parentRoute3 = mark2.getParentRoute() + "-" + mark2.getMarkName();
//                    mark3.setParent(parent3);
//                    mark3.setParentRoute(parentRoute3);
//                    updateById(mark3);
//                }
//            }
//        }
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
        //增加父级部门记录
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
        //查询所有父级中包含此id的标记
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

package com.xinkao.erp.question.service.impl;

import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.exam.entity.ExamClass;
import com.xinkao.erp.manage.service.MarkService;
import com.xinkao.erp.question.entity.QuestionMark;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.question.mapper.QuestionMarkMapper;
import com.xinkao.erp.question.service.QuestionMarkService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 题目-标记关联表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2025-04-20 22:26:27
 */
@Service
public class QuestionMarkServiceImpl extends BaseServiceImpl<QuestionMarkMapper, QuestionMark> implements QuestionMarkService {

    @Autowired
    private MarkService markService;

    @Override
    public BaseResponse<List<Mark>> getListByQid(Integer qid) {
        List<Mark> markList = markService.lambdaQuery().eq(Mark::getIsDel, CommonEnum.IS_DEL.NO.getCode()).list();
        List<Integer> markIds = lambdaQuery().eq(QuestionMark::getQid,qid).list().stream().map(QuestionMark::getMid).collect(Collectors.toList());
        return BaseResponse.ok(formatMarkListByQid(markList,markIds));
    }

    //递归获取子集列表
    /**
     * 格式化菜单列表
     *
     * @param markList 菜单列表
     * @return 格式化后的菜单列表
     */
    @Override
    public List<Mark> formatMarkListByQid(List<Mark> markList,List<Integer> markIds) {
        List<Mark> formatMarkList = new ArrayList<>();
        for (Mark mark : markList) {
            int markId = mark.getId();
            int pid = mark.getPid();

            // 一级菜单
            if (pid == 0 && markIds.contains(markId)) {
                List<Mark> childMarkList = new ArrayList<>();
                for (Mark childMark : markList) {
                    int childPid = childMark.getPid();
                    // 二级菜单
                    if (childPid == markId) {
                        childMarkList.add(childMark);
                    }
                }
                mark.setChildMarkList(childMarkList);
                formatMarkList.add(mark);
            }
        }

        return formatMarkList;
    }
}

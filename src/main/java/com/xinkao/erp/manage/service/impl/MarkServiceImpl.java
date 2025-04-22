package com.xinkao.erp.manage.service.impl;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.manage.mapper.MarkMapper;
import com.xinkao.erp.manage.param.MarkParam;
import com.xinkao.erp.manage.query.MarkQuery;
import com.xinkao.erp.manage.service.MarkService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
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

    //根据用户权限获取用户菜单
    @Override
    public BaseResponse<List<Mark>> getList(MarkQuery query){
        List<Mark> markList = formatMarkList(markMapper.getMarkList(query));
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
    public List<Mark> formatMarkList(List<Mark> markList) {
        List<Mark> formatMarkList = new ArrayList<>();
        for (Mark mark : markList) {
            int markId = mark.getId();
            int pid = mark.getPid();

            // 一级菜单
            if (pid == 0) {
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

    @Override
    public BaseResponse save(MarkParam markParam) {
        Mark mark = markParam.convertTo();
        save(mark);
        return BaseResponse.ok("成功！");
    }

    @Override
    public BaseResponse update(MarkParam markParam) {
        Mark mark = markParam.convertTo();
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

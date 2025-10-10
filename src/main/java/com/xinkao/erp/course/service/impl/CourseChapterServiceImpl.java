package com.xinkao.erp.course.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.CourseChapter;
import com.xinkao.erp.course.mapper.CourseChapterMapper;
import com.xinkao.erp.course.query.CourseChapterQuery;
import com.xinkao.erp.course.service.CourseChapterService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseChapterServiceImpl extends BaseServiceImpl<CourseChapterMapper, CourseChapter> implements CourseChapterService {
    @Autowired
    private CourseChapterMapper courseChapterMapper;

    @Override
    public Page<CourseChapter> page(CourseChapterQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return courseChapterMapper.page(page, query);
    }

    @Override
    public BaseResponse<?> save1(CourseChapter courseChapter) {
        if (lambdaQuery().eq(CourseChapter::getChapterTitle, courseChapter.getChapterTitle()).eq(CourseChapter::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("课程章节标题已存在！");
        }
        return save(courseChapter) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> update(CourseChapter courseChapter) {
        if (lambdaQuery().eq(CourseChapter::getChapterTitle, courseChapter.getChapterTitle()).ne(CourseChapter::getId, courseChapter.getId()).eq(CourseChapter::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("课程章节标题已存在！");
        }
        return updateById(courseChapter) ? BaseResponse.ok("更新成功！") : BaseResponse.fail("更新失败！");
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        return lambdaUpdate().eq(CourseChapter::getId, id).set(CourseChapter::getIsDel, CommonEnum.IS_DEL.YES.getCode()).update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }
}

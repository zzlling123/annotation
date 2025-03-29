package com.xinkao.erp.course.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.Course;
import com.xinkao.erp.course.mapper.CourseChapterMapper;
import com.xinkao.erp.course.mapper.CourseMapper;
import com.xinkao.erp.course.query.CourseQuery;
import com.xinkao.erp.course.service.CourseService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程表	 服务实现类
 * </p>
 *
 * @author zzl
 * @since 2025-03-21 16:53:36
 */
@Service
public class CourseServiceImpl extends BaseServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Page<Course> page(CourseQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return courseMapper.page(page, query);
    }

    @Override
    public BaseResponse<?> save1(Course course) {
        if (lambdaQuery().eq(Course::getCourseName, course.getCourseName()).eq(Course::getCourseStatus, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("课程名称已存在！");
        }
        return save(course) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> update(Course course) {
        if (lambdaQuery().eq(Course::getCourseName, course.getCourseName()).ne(Course::getId, course.getId()).eq(Course::getCourseStatus, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("课程名称已存在！");
        }
        return updateById(course) ? BaseResponse.ok("更新成功！") : BaseResponse.fail("更新失败！");
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        return lambdaUpdate().eq(Course::getId, id).set(Course::getCourseStatus, CommonEnum.IS_DEL.YES.getCode()).update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }
}

package com.xinkao.erp.course.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.UpdateStateParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.Course;
import com.xinkao.erp.course.mapper.CourseChapterMapper;
import com.xinkao.erp.course.mapper.CourseMapper;
import com.xinkao.erp.course.query.CourseQuery;
import com.xinkao.erp.course.service.CourseService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
public class CourseServiceImpl extends BaseServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Value("${path.cres}")
    private String cres;

    @Override
    public Page<Course> page(CourseQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return courseMapper.page(page, query);
    }

    @Override
    public BaseResponse<?> save1(Course course, MultipartFile coverImage) {
        if (lambdaQuery().eq(Course::getCourseName, course.getCourseName()).eq(Course::getCourseStatus, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("课程名称已存在！");
        }
        if (coverImage != null && !coverImage.isEmpty()) {
        }

        return save(course) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }
    private String uploadCoverImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            originalFilename = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "-" + UUID.randomUUID() + fileExtension;
        File dest = new File(cres + uniqueFileName);

        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
        return cres + uniqueFileName;
    }

    @Override
    public BaseResponse<?> update(Course course, MultipartFile coverImage) {
        if (lambdaQuery().eq(Course::getCourseName, course.getCourseName()).ne(Course::getId, course.getId()).eq(Course::getCourseStatus, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("课程名称已存在！");
        }
        if (coverImage != null && !coverImage.isEmpty()) {
            String newImagePath = uploadCoverImage(coverImage);
            course.setCoverImage(coverImage.getName());
        }
        return updateById(course) ? BaseResponse.ok("更新成功！") : BaseResponse.fail("更新失败！");
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        return lambdaUpdate().eq(Course::getId, id).set(Course::getIsDel, CommonEnum.IS_DEL.YES.getCode()).update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }

    @Override
    public BaseResponse updateState(UpdateStateParam updateStateParam){
        String[] ids = updateStateParam.getIds().split(",");
        String content = Objects.equals(updateStateParam.getState(), "1") ? "启用" :"禁用";
        return lambdaUpdate().in(Course::getId, ids).set(Course::getCourseStatus, updateStateParam.getState()).update()?BaseResponse.ok(content+"成功！"):BaseResponse.fail(content+"失败！");
    }
}

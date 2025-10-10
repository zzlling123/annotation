package com.xinkao.erp.course.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.CourseResource;
import com.xinkao.erp.course.entity.CourseResource;
import com.xinkao.erp.course.mapper.CourseResourceMapper;
import com.xinkao.erp.course.mapper.CourseResourceMapper;
import com.xinkao.erp.course.query.CourseResourceQuery;
import com.xinkao.erp.course.service.CourseResourceService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class CourseResourceServiceImpl extends BaseServiceImpl<CourseResourceMapper, CourseResource> implements CourseResourceService {
    @Autowired
    private CourseResourceMapper courseResourceMapper;

    public void convertToHLS(String inputPath, String outputDir) throws IOException {
        String ffmpegCmd = String.format(
                "ffmpeg -i %s -c:v libx264 -c:a aac -strict -2 -hls_time 10 -hls_list_size 0 -f hls %s/output.m3u8",
                inputPath, outputDir
        );

        Process process = Runtime.getRuntime().exec(ffmpegCmd);
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg处理失败，退出码: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("FFmpeg进程被中断", e);
        }
    }

    @Override
    public List<CourseResource> getListByChapterId(Long chapterId) {
        if (chapterId != null) {
            return lambdaQuery().eq(CourseResource::getChapterId, chapterId).eq(CourseResource::getIsDel, 0).list();
        }
        return null;
    }

    @Override
    public Page<CourseResource> page(CourseResourceQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return courseResourceMapper.page(page, query);
    }

    @Override
    public BaseResponse<?> save1(CourseResource courseResource) {
        if (lambdaQuery().eq(CourseResource::getFileName, courseResource.getFileName()).eq(CourseResource::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("课程章节资源标题已存在！");
        }
        return save(courseResource) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> update(CourseResource courseResource) {
        if (lambdaQuery().eq(CourseResource::getFileName, courseResource.getFileName()).ne(CourseResource::getId, courseResource.getId()).eq(CourseResource::getIsDel, CommonEnum.IS_DEL.NO.getCode()).count() > 0) {
            return BaseResponse.fail("课程章节资源标题已存在！");
        }
        return updateById(courseResource) ? BaseResponse.ok("更新成功！") : BaseResponse.fail("更新失败！");
    }

    @Override
    public BaseResponse<?> delete(Integer id) {
        return lambdaUpdate().eq(CourseResource::getId, id).set(CourseResource::getIsDel, CommonEnum.IS_DEL.YES.getCode()).update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }
}

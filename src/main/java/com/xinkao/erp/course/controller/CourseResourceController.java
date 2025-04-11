package com.xinkao.erp.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.course.entity.CourseResource;
import com.xinkao.erp.course.query.CourseResourceQuery;
import com.xinkao.erp.course.service.CourseResourceService;
import com.xinkao.erp.course.utils.FileTypeChecker;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 章节资源表 前端控制器
 *
 * @author zzl
 * @since 2025-03-21 17:19:23
 */
@RestController
@RequestMapping("/course-resource")
public class CourseResourceController {

    @Value("${file.save.root}")
    private String savaLaction ;

    @Autowired
    private CourseResourceService courseResourceService;

    @PrimaryDataSource
    @PostMapping("/list/chapterId")
    @ApiOperation("查询课程章节资源信息通过章节信息ID")
    public List<CourseResource> getResourceList(Long chapterId) {
        return courseResourceService.getListByChapterId(chapterId);
    }

    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页查询课程章节资源信息")
    public BaseResponse<Page<CourseResource>> page(@Valid @RequestBody CourseResourceQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<CourseResource> voPage = courseResourceService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    /**
     * 新增课程章节信息
     *
     * @param courseResource 课程章节信息参数
     * @return 操作结果
     */
//    @PrimaryDataSource
//    @PostMapping("/save")
//    @ApiOperation("新增课程章节资源信息")
//    @Log(content = "新增课程章节资源信息", operationType = OperationType.INSERT, isSaveRequestData = false)
//    public BaseResponse<?> save(@Valid @RequestBody CourseResource courseResource) {
//        return courseResourceService.save1(courseResource);
//    }

    /**
     * 编辑课程章节信息
     *
     * @param courseResource 课程章节信息参数
     * @return 操作结果
     */
    @PostMapping("/update")
    @ApiOperation("编辑课程章节资源信息")
    @Log(content = "编辑课程章节资源信息", operationType = OperationType.UPDATE, isSaveRequestData = false)
    public BaseResponse<?> update(@Valid @RequestBody CourseResource courseResource) {
        return courseResourceService.update(courseResource);
    }

    /**
     * 删除课程章节信息
     *
     * @param id 课程章节ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    @ApiOperation("删除课程章节信息")
    @Log(content = "删除课程章节信息", operationType = OperationType.DELETE, isSaveRequestData = false)
    public BaseResponse<?> delete(@PathVariable Integer id) {
        return courseResourceService.delete(id);
    }

    // 使用HttpServletRequest作为参数
    @PrimaryDataSource
    @PostMapping("/upload/file")
    @ApiOperation("上传课程章节信息")
    public Map<String, Object> uploadRequest(@RequestParam(value="file") MultipartFile[] files,@RequestParam(value="chapterId")Long chapterId, HttpServletRequest request) {
        try {
            boolean flag = false;
            String saveFileName;
            File file1 = new File(savaLaction);
            if (!file1.exists()){
                file1.mkdirs();
            }
            for (MultipartFile multipartFile : files) {
                if (multipartFile.isEmpty()) {
                    return dealResultMap(false, "上传失败");
                }
                System.out.println("fileName:" + multipartFile.getOriginalFilename());
                saveFileName = UUID.randomUUID().toString()+multipartFile.getOriginalFilename();
                File file = new File(savaLaction,saveFileName);
                multipartFile.transferTo(file);
                CourseResource courseResource = new CourseResource();
                courseResource.setFileName(multipartFile.getOriginalFilename());
                courseResource.setResourceType(FileTypeChecker.getFileType(multipartFile.getOriginalFilename()));
                courseResource.setFilePath(file.getAbsolutePath());
                courseResource.setChapterId(chapterId);
                courseResource.setFileSize(file.length());
                courseResourceService.save(courseResource);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return dealResultMap(false, "上传失败");
        }
        return dealResultMap(true, "上传成功");
    }

    // 使用Spring MVC的MultipartFile类作为参数
    @PostMapping("/upload/multipart")
    public Map<String, Object> uploadMultipartFile(MultipartFile file,Long chapterId) {
        String fileName = file.getOriginalFilename();
        File file1 = new File(savaLaction);
        if (!file1.exists()){
            file1.mkdirs();
        }
        String saveFileName;
        try {
            if (file.isEmpty()) {
                return dealResultMap(false, "上传失败");
            }
            System.out.println("fileName:" + file.getOriginalFilename());
            saveFileName = UUID.randomUUID().toString()+file.getOriginalFilename();
            File dest = new File(savaLaction,saveFileName);
            file.transferTo(dest);
            CourseResource courseResource = new CourseResource();
            courseResource.setFileName(file.getOriginalFilename());
            courseResource.setResourceType(FileTypeChecker.getFileType(file.getOriginalFilename()));
            courseResource.setFilePath(dest.getAbsolutePath());
            courseResource.setChapterId(chapterId);
            courseResource.setFileSize(dest.length());
            courseResourceService.save(courseResource);
        } catch (Exception e) {
            e.printStackTrace();
            return dealResultMap(false, "上传失败");
        }
        return dealResultMap(true, "上传成功");
    }

    // 处理上传文件结果
    private Map<String, Object> dealResultMap(boolean success, String msg) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", success);
        result.put("msg", msg);
        return result;
    }

    // 分片上传接口
    @PostMapping("/chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("chunkNumber") int chunkNumber,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("identifier") String identifier) {
        try {
            String uploadDir = "/tmp/uploads/";
            File chunkDir = new File(uploadDir + identifier);
            if (!chunkDir.exists()) {
                chunkDir.mkdirs();
            }

            // 保存分片到临时目录
            File chunkFile = new File(chunkDir, String.format("%s.part", chunkNumber));
            file.transferTo(chunkFile);

            return ResponseEntity.ok("分片上传成功");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("分片上传失败: " + e.getMessage());
        }
    }

    // 合并分片接口
    @PostMapping("/merge")
    public ResponseEntity<String> mergeChunks(
            @RequestParam("fileName") String fileName,
            @RequestParam("identifier") String identifier) {

        String uploadDir = "/tmp/uploads/";
        File chunkDir = new File(uploadDir + identifier);

        if (!chunkDir.exists()) {
            return ResponseEntity.badRequest().body("分片不存在");
        }

        try {
            File outputFile = new File(uploadDir + fileName);
            FileOutputStream fos = new FileOutputStream(outputFile);

            // 按序号合并所有分片
            for (int i = 1; i <= Objects.requireNonNull(chunkDir.list()).length; i++) {
                File chunkFile = new File(chunkDir, i + ".part");
                FileInputStream fis = new FileInputStream(chunkFile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fis.close();
            }
            fos.close();

            // 删除临时分片目录
            FileUtils.deleteDirectory(chunkDir);

            return ResponseEntity.ok("文件合并成功: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("合并失败: " + e.getMessage());
        }
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkFileExists(@RequestParam("md5") String md5) {
        File targetDir = new File("/data/videos/");
        File[] existingFiles = targetDir.listFiles((dir, name) -> name.startsWith(md5));
        if (existingFiles != null && existingFiles.length > 0) {
            return ResponseEntity.ok().body(Collections.singletonMap("url", "/videos/" + existingFiles[0].getName()));
        } else {
            return ResponseEntity.status(404).body("文件不存在，需要上传");
        }
    }

    @PostMapping("/process")
    public ResponseEntity<String> processVideo(@RequestParam("filePath") String filePath) {
        try {
            String outputDir = "/data/hls/" + UUID.randomUUID();
            new File(outputDir).mkdirs();
            courseResourceService.convertToHLS(filePath, outputDir);
            return ResponseEntity.ok("视频切片成功，访问路径: " + outputDir + "/output.m3u8");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("处理失败: " + e.getMessage());
        }
    }

}

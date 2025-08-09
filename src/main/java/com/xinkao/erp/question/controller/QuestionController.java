package com.xinkao.erp.question.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.DataScope;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.exception.BusinessException;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.question.entity.Label;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.question.entity.QuestionLabel;
import com.xinkao.erp.question.entity.QuestionType;
import com.xinkao.erp.question.excel.QuestionImportModel;
import com.xinkao.erp.question.param.QuestionChildParam;
import com.xinkao.erp.question.param.QuestionFormTitleParam;
import com.xinkao.erp.question.param.QuestionParam;
import com.xinkao.erp.question.param.QuestionTypeParam;
import com.xinkao.erp.question.query.QuestionQuery;
import com.xinkao.erp.question.service.LabelService;
import com.xinkao.erp.question.service.QuestionLabelService;
import com.xinkao.erp.question.service.QuestionService;
import com.xinkao.erp.question.service.QuestionTypeService;
import com.xinkao.erp.question.vo.QuestionFormVo;
import com.xinkao.erp.question.vo.QuestionImportResultVO;
import com.xinkao.erp.question.vo.QuestionInfoVo;
import com.xinkao.erp.question.vo.QuestionPageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 题目表 前端控制器
 * </p>
 *
 * @author Ldy
 * @since 2025-03-22 11:19:40
 */
@RestController
@RequestMapping("/question")
public class    QuestionController extends BaseController {

    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionTypeService questionTypeService;
    @Resource
    private LabelService labelService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${path.fileUrl}")
    private String fileUrl;
    @Value("${ipurl.url}")
    private String ipurl;


    /**
     * 获取题目分类下拉列表
     */
    @PrimaryDataSource
    @PostMapping("/getQuestionType")
    @ApiOperation("获取题目分类下拉列表")
    public BaseResponse<List<QuestionType>> getQuestionType() {
        return BaseResponse.ok("成功", questionTypeService.list());
    }

    /**
     * 修改题目分类
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/updateQuestionType")
    @ApiOperation("修改题目分类")
    public BaseResponse<?> updateQuestionType(@RequestBody @Valid QuestionTypeParam param) {
        return questionTypeService.update(param);
    }

    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/upload/file")
    @ApiOperation("上传文件")
    public BaseResponse<String> uploadRequest(@RequestParam(value="file") MultipartFile file,@RequestParam String id, HttpServletRequest request) {
        try {
            String saveFileName;
            File file1 = new File(fileUrl);
            if (!file1.exists()){
                file1.mkdirs();
            }
            if (file.isEmpty()) {
                throw new BusinessException("文件为空");
            }
            System.out.println("fileName:" + file.getOriginalFilename());
            saveFileName = UUID.randomUUID().toString()+file.getOriginalFilename();
            File fileNew = new File(fileUrl,saveFileName);
            // 创建父目录（如果不存在）
            if (!fileNew.getParentFile().exists()) {
                fileNew.getParentFile().mkdirs();
            }
            file.transferTo(fileNew);
            String newFileUrl = ipurl+"/annotation/fileUrl/"+saveFileName;
            return questionTypeService.lambdaUpdate().eq(QuestionType::getId,id).set(QuestionType::getFileUrl,newFileUrl).update()?BaseResponse.ok("上传成功"):BaseResponse.fail("上传失败");
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("上传失败");
        }

    }

    /**
     * 获取题目分类说明文档
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/getQuestionTypeFileUrl/{questionTypeId}")
    @ApiOperation("获取题目分类说明文档")
    public BaseResponse<String> updateQuestionType(@PathVariable String questionTypeId) {
        QuestionType questionType = questionTypeService.getById(questionTypeId);
        String fileUrl = "";
        if (StrUtil.isNotBlank(questionType.getFileUrl())){
            return BaseResponse.ok("成功","https://view.xdocin.com/view?src=${"+questionType.getFileUrl()+"}");
        }
        return BaseResponse.ok(fileUrl);
    }

    /**
     * 获取题目分类下拉列表
     */
    @PrimaryDataSource
    @PostMapping("/getQuestionType/{shape}")
    @ApiOperation("获取题目分类下拉列表")
    public BaseResponse<List<QuestionType>> getQuestionType(@PathVariable String shape) {
        if ("500".equals(shape)){
            //创建固定字符串集合：2,3,4,5
            List<Integer> ids = Arrays.asList(2,4,5,6,7);
            return BaseResponse.ok("成功", questionTypeService.lambdaQuery().in(QuestionType::getId, ids).list());
        }
        return BaseResponse.ok("成功", questionTypeService.list());
    }

    /**
     * 新建自定义标签
     */
    @PrimaryDataSource
    @PostMapping("/saveQuestionLabel")
    @ApiOperation("新建自定义标签")
    public BaseResponse<?> saveQuestionLabel(@RequestParam String labelName) {
        Label label = new Label();
        label.setLabelName(labelName);
        return labelService.save(label)? BaseResponse.ok("成功") : BaseResponse.fail("失败");
    }

    /**
     * 根据标签名称模糊查询标签
     */
    @PrimaryDataSource
    @PostMapping("/getLabelList")
    @ApiOperation("根据标签名称模糊查询标签")
    public BaseResponse<List<Label>> getLabelList(@RequestParam String labelName) {
        return BaseResponse.ok("成功", labelService.lambdaQuery().like(Label::getLabelName, labelName).list());
    }

    /**
     * 分页查询题库
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @PrimaryDataSource
    @DataScope(role = "1,2,18,19")
    @PostMapping("/page")
    @ApiOperation("分页查询题库")
    public BaseResponse<Page<QuestionPageVo>> page(@RequestBody QuestionQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<QuestionPageVo> voPage = questionService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    /**
     * 获取题目详情
     *
     * @param id 题目ID
     * @return 题目详情
     */
    @PrimaryDataSource
    @DataScope(role = "1,2,18,19")
    @GetMapping("/detail/{id}")
    @ApiOperation("获取题目详情")
    public BaseResponse<QuestionInfoVo> getQuestionDetail(@PathVariable Integer id) {
        QuestionInfoVo question = questionService.getQuestionDetail(id);
        //将选项转为List<String>
        if (question == null) {
            return BaseResponse.fail("题目不存在！");
        }
        return BaseResponse.ok(question);
    }

    /**
     * 新增题目
     *
     * @param questionParam 题目参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/save")
    @ApiOperation("新增题目")
    @Log(content = "新增题目",operationType = OperationType.INSERT)
    public BaseResponse<?> save(@Valid @RequestBody QuestionParam questionParam) {
        return questionService.save(questionParam);
    }

    /**
     * 新增题目单二级标题
     *
     * @param questionFormTitleParam 题目参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/saveQuestionFormTitle")
    @ApiOperation("新增题目单二级标题")
    public BaseResponse<?> saveQuestionFormTitle(@Valid @RequestBody QuestionFormTitleParam questionFormTitleParam) {
        return questionService.saveQuestionFormTitle(questionFormTitleParam);
    }

    /**
     * 编辑题目单二级标题
     *
     * @param questionFormTitleParam 题目参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/updateQuestionFormTitle")
    @ApiOperation("编辑题目单二级标题")
    public BaseResponse<?> updateQuestionFormTitle(@Valid @RequestBody QuestionFormTitleParam questionFormTitleParam) {
        return questionService.updateQuestionFormTitle(questionFormTitleParam);
    }

    /**
     * 新增题目单子题
     *
     * @param questionChildParam 题目子题参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/saveQuestionChild")
    @ApiOperation("新增题目单子题")
    public BaseResponse<?> saveQuestionChild(@Valid @RequestBody QuestionChildParam questionChildParam) {
        return questionService.saveQuestionChild(questionChildParam);
    }

    /**
     * 编辑题目单子题
     *
     * @param questionChildParam 题目子题参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/updateQuestionChild")
    @ApiOperation("编辑题目单子题")
    public BaseResponse<?> updateQuestionChild(@Valid @RequestBody QuestionChildParam questionChildParam) {
        return questionService.updateQuestionChild(questionChildParam);
    }

    /**
     * 编辑题目
     *
     * @param questionParam 题目参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/update")
    @ApiOperation("编辑题目")
    @Log(content = "编辑题目",operationType = OperationType.UPDATE)
    public BaseResponse<?> update(@Valid @RequestBody QuestionParam questionParam) {
        System.out.println("测试");
        return questionService.update(questionParam);
    }

    /**
     * 批量删除题目
     *
     * @param param 题目ID列表
     * @return 操作结果
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/del")
    @ApiOperation("批量删除题目")
    @Log(content = "批量删除题目",operationType = OperationType.DELETE)
    public BaseResponse<?> del(@RequestBody DeleteParam param) {
        return questionService.del(param);
    }

    /**
     * 测试方法，题库按照分类、题型插入题目
     * @return
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/selfSave")
    @ApiOperation("题库按照分类、题型插入题目")
    public void selfSave(){
        questionService.selfSave();
    }

    /**
     * 获根据题目ID取题目单详情
     * @return
     */
    @PrimaryDataSource
    @DataScope(role = "1,2")
    @PostMapping("/getQuestionFormInfo/{id}")
    @ApiOperation("获根据题目ID取题目单详情")
    public BaseResponse<List<QuestionFormVo>> getQuestionFormInfo(@PathVariable Integer id){
        return questionService.getQuestionFormInfo(id);
    }

    /**
     * 批量导入题目
     */
    @ApiOperation(value = "批量导入题目")
    @PostMapping("/import")
    public BaseResponse<QuestionImportResultVO> importQuestions(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return BaseResponse.fail("请选择要导入的文件");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            return BaseResponse.fail("请上传Excel文件");
        }

        try {
            QuestionImportResultVO result = questionService.importQuestions(file);

            if (result.getFailCount() > 0) {
                return BaseResponse.other("导入完成，但存在错误数据", result);
            } else {
                return BaseResponse.ok("导入成功", result);
            }
        } catch (Exception e) {
            return BaseResponse.fail("导入失败：" + e.getMessage());
        }
    }


    @PrimaryDataSource
    @PostMapping("/form/import-zip")
    @ApiOperation("题目单导入（zip+相对路径）")
    public BaseResponse<QuestionImportResultVO> importQuestionFormZip(
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return BaseResponse.fail("请上传zip文件");
        }
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".zip")) {
            return BaseResponse.fail("仅支持.zip文件");
        }

        try {
            // 后端在实现中自动创建题目主记录并返回生成的 questionId
            QuestionImportResultVO result = questionService.importQuestionFormZip(file);

            if (result.getFailCount() != null && result.getFailCount() > 0) {
                return BaseResponse.other("导入完成，但存在错误数据", result);
            }
            return BaseResponse.ok("导入成功", result);
        } catch (Exception e) {
            return BaseResponse.fail("导入失败：" + e.getMessage());
        }
    }

    @PrimaryDataSource
    @GetMapping("/titleIntroductionTemplate/common")
    @ApiOperation("下载普通题目批量导入模板.xlsx")
    public void downloadCommonTemplate(HttpServletResponse response) throws IOException {
        File baseDir = new File(System.getProperty("user.dir"), "titleIntroductionTemplate");
        File file = new File(baseDir, "普通题目批量导入模板.xlsx");
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "模板不存在");
            return;
        }
        writeFile(response, file, "普通题目批量导入模板.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @PrimaryDataSource
    @GetMapping("/titleIntroductionTemplate/form")
    @ApiOperation("下载题目单.zip")
    public void downloadFormTemplate(HttpServletResponse response) throws IOException {
        File baseDir = new File(System.getProperty("user.dir"), "titleIntroductionTemplate");
        File file = new File(baseDir, "题目单.zip");
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "模板不存在");
            return;
        }
        writeFile(response, file, "题目单.zip", "application/zip");
    }

    private void writeFile(HttpServletResponse response, File file, String downloadName, String contentType) throws IOException {
        response.reset();
        response.setContentType(contentType);
        String encoded = URLEncoder.encode(downloadName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        java.nio.file.Files.copy(file.toPath(), response.getOutputStream());
        response.flushBuffer();
    }

}
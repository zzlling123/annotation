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
import com.xinkao.erp.question.excel.ImportResultErrorRow;
import com.xinkao.erp.question.excel.QuestionImportModel;
import com.xinkao.erp.question.param.QuestionChildParam;
import com.xinkao.erp.question.param.QuestionFormTitleParam;
import com.xinkao.erp.question.param.QuestionParam;
import com.xinkao.erp.question.param.QuestionTypeParam;
import com.xinkao.erp.question.param.QuestionTypeAddParam;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

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
import java.io.InputStream;

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


    @PrimaryDataSource
    @PostMapping("/getQuestionType")
    public BaseResponse<List<QuestionType>> getQuestionType() {
        return BaseResponse.ok("成功", questionTypeService.list());
    }

    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/saveQuestionType")
    public BaseResponse<?> saveQuestionType(@RequestBody @Valid QuestionTypeAddParam param) {
        return questionTypeService.save(param);
    }




    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/updateQuestionType")
    public BaseResponse<?> updateQuestionType(@RequestBody @Valid QuestionTypeParam param) {
        return questionTypeService.update(param);
    }




    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/delQuestionType")
    public BaseResponse<?> delQuestionType(@RequestBody DeleteParam param) {
        return questionTypeService.delQuestionType(param);
    }

    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/upload/file")
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




    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/getQuestionTypeFileUrl/{questionTypeId}")
    public BaseResponse<String> updateQuestionType(@PathVariable String questionTypeId) {
        QuestionType questionType = questionTypeService.getById(questionTypeId);
        String fileUrl = "";
        if (StrUtil.isNotBlank(questionType.getFileUrl())){
            return BaseResponse.ok("成功","https://view.xdocin.com/view?src=${"+questionType.getFileUrl()+"}");
        }
        return BaseResponse.ok(fileUrl);
    }




    @PrimaryDataSource
    @PostMapping("/getQuestionType/{shape}")
    public BaseResponse<List<QuestionType>> getQuestionType(@PathVariable String shape) {
        if ("500".equals(shape)){

            List<Integer> ids = Arrays.asList(2,4,5,6,7);
            return BaseResponse.ok("成功", questionTypeService.lambdaQuery().in(QuestionType::getId, ids).list());
        }
        return BaseResponse.ok("成功", questionTypeService.list());
    }




    @PrimaryDataSource
    @PostMapping("/saveQuestionLabel")
    public BaseResponse<?> saveQuestionLabel(@RequestParam String labelName) {
        Label label = new Label();
        label.setLabelName(labelName);
        return labelService.save(label)? BaseResponse.ok("成功") : BaseResponse.fail("失败");
    }




    @PrimaryDataSource
    @PostMapping("/getLabelList")
    public BaseResponse<List<Label>> getLabelList(@RequestParam String labelName) {
        return BaseResponse.ok("成功", labelService.lambdaQuery().like(Label::getLabelName, labelName).list());
    }







    @PrimaryDataSource
    @DataScope(role = "1,18,19")
    @PostMapping("/page")
    public BaseResponse<Page<QuestionPageVo>> page(@RequestBody QuestionQuery query) {
        Pageable pageable = query.getPageInfo();
        Page<QuestionPageVo> voPage = questionService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }







    @PrimaryDataSource
    @DataScope(role = "1")
    @GetMapping("/detail/{id}")
    public BaseResponse<QuestionInfoVo> getQuestionDetail(@PathVariable Integer id) {
        QuestionInfoVo question = questionService.getQuestionDetail(id);

        if (question == null) {
            return BaseResponse.fail("题目不存在！");
        }
        return BaseResponse.ok(question);
    }







    @PrimaryDataSource
    @DataScope(role = "1,18")
    @PostMapping("/save")
    @Log(content = "新增题目",operationType = OperationType.INSERT)
    public BaseResponse<?> save(@Valid @RequestBody QuestionParam questionParam) {
        return questionService.save(questionParam);
    }







    @PrimaryDataSource
    @PostMapping("/saveQuestionFormTitle")
    public BaseResponse<?> saveQuestionFormTitle(@Valid @RequestBody QuestionFormTitleParam questionFormTitleParam) {
        return questionService.saveQuestionFormTitle(questionFormTitleParam);
    }







    @PrimaryDataSource
    @PostMapping("/updateQuestionFormTitle")
    public BaseResponse<?> updateQuestionFormTitle(@Valid @RequestBody QuestionFormTitleParam questionFormTitleParam) {
        return questionService.updateQuestionFormTitle(questionFormTitleParam);
    }







    @PrimaryDataSource
    @PostMapping("/saveQuestionChild")
    public BaseResponse<?> saveQuestionChild(@Valid @RequestBody QuestionChildParam questionChildParam) {
        return questionService.saveQuestionChild(questionChildParam);
    }







    @PrimaryDataSource
    @PostMapping("/updateQuestionChild")
    public BaseResponse<?> updateQuestionChild(@Valid @RequestBody QuestionChildParam questionChildParam) {
        return questionService.updateQuestionChild(questionChildParam);
    }







    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/update")
    @Log(content = "编辑题目",operationType = OperationType.UPDATE)
    public BaseResponse<?> update(@Valid @RequestBody QuestionParam questionParam) {
        return questionService.update(questionParam);
    }







    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/del")
    @Log(content = "批量删除题目",operationType = OperationType.DELETE)
    public BaseResponse<?> del(@RequestBody DeleteParam param) {
        return questionService.del(param);
    }







    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/delTitle")
    @Log(content = "批量删除二级标题题目",operationType = OperationType.DELETE)
    public BaseResponse<?> delTitle(@RequestBody DeleteParam param) {
        return questionService.delTitle(param);
    }







    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/delChild")
    @Log(content = "批量删除子题",operationType = OperationType.DELETE)
    public BaseResponse<?> delChild(@RequestBody DeleteParam param) {
        return questionService.delChild(param);
    }





    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/selfSave")
    public void selfSave(){
        questionService.selfSave();
    }





    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/getQuestionFormInfo/{id}")
    public BaseResponse<List<QuestionFormVo>> getQuestionFormInfo(@PathVariable Integer id){
        return questionService.getQuestionFormInfo(id);
    }


    @PrimaryDataSource
    @DataScope(role = "1")
    @GetMapping("/titleIntroductionTemplate/common")
    public void downloadCommonTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("titleIntroductionTemplate/普通题目批量导入模板.xlsx");
        if (!resource.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "模板不存在");
            return;
        }
        try (InputStream in = resource.getInputStream()) {
            writeFile(request, response, in, resource.contentLength(), "普通题目批量导入模板.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        }
    }

    @PrimaryDataSource
    @DataScope(role = "1")
    @GetMapping("/titleIntroductionTemplate/form")
    public void downloadFormTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("titleIntroductionTemplate/题目单.zip");
        if (!resource.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "模板不存在");
            return;
        }
        try (InputStream in = resource.getInputStream()) {
            writeFile(request, response, in, resource.contentLength(), "题目单.zip", "application/zip");
        }
    }


    private void writeFile(HttpServletRequest request, HttpServletResponse response, InputStream inputStream, long contentLength, String downloadName, String contentType) throws IOException {

        String originHeaderValue = request.getHeader("Origin");
        if (originHeaderValue != null && originHeaderValue.length() > 0) {
            response.setHeader("Access-Control-Allow-Origin", originHeaderValue);
            response.setHeader("Vary", "Origin");
        }
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentType(contentType);
        String encoded = URLEncoder.encode(downloadName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        if (contentLength >= 0) {
            response.setHeader("Content-Length", String.valueOf(contentLength));
        }
        StreamUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }

    private void writeJson(HttpServletResponse response, Object body) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String json;
        try {
            json = com.alibaba.fastjson.JSON.toJSONString(body);
        } catch (Exception e) {
            json = "{\"code\":500,\"msg\":\"序列化失败\"}";
        }
        response.getWriter().write(json);
        response.getWriter().flush();
    }



    @DataScope(role = "1")
    @PostMapping("/import")
    public void importQuestions(@RequestParam("file") MultipartFile file,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        if (file.isEmpty()) {
            writeJson(response, BaseResponse.fail("请选择要导入的文件"));
            return;
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            writeJson(response, BaseResponse.fail("请上传Excel文件"));
            return;
        }
        try {
            QuestionImportResultVO result = questionService.importQuestions(file);

            String export = request.getParameter("export");
            if ("excel".equalsIgnoreCase(export)) {
                java.util.List<com.xinkao.erp.question.excel.ImportResultSummaryRow> summary = new java.util.ArrayList<>();
                com.xinkao.erp.question.excel.ImportResultSummaryRow row = new com.xinkao.erp.question.excel.ImportResultSummaryRow();
                row.setTotalCount(result.getTotalCount());
                row.setSuccessCount(result.getSuccessCount());
                row.setFailCount(result.getFailCount());
                summary.add(row);
                java.util.List<com.xinkao.erp.question.excel.ImportResultErrorRow> details = new java.util.ArrayList<>();
                if (result.getRowErrors() != null && !result.getRowErrors().isEmpty()) {
                    for (com.xinkao.erp.question.vo.QuestionImportResultVO.RowError re : result.getRowErrors()) {
                        ImportResultErrorRow d = new ImportResultErrorRow();
                        d.setRowNum(re.getRowNum());
                        d.setMessage(re.getMessage());
                        details.add(d);
                    }
                } else if (result.getErrorMessages() != null) {
                    for (String msg : result.getErrorMessages()) {
                        com.xinkao.erp.question.excel.ImportResultErrorRow d = new com.xinkao.erp.question.excel.ImportResultErrorRow();
                        Integer rn = null;
                        try {
                            int idx1 = msg.indexOf("第");
                            int idx2 = msg.indexOf("行");
                            if (idx1 >= 0 && idx2 > idx1) {
                                String num = msg.substring(idx1 + 1, idx2);
                                rn = Integer.parseInt(num);
                            }
                        } catch (Exception ignore) {}
                        d.setRowNum(rn);
                        d.setMessage(msg);
                        details.add(d);
                    }
                }
                String originHeaderValue = request.getHeader("Origin");
                if (originHeaderValue != null && originHeaderValue.length() > 0) {
                    response.setHeader("Access-Control-Allow-Origin", originHeaderValue);
                    response.setHeader("Vary", "Origin");
                }
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                String downloadName = java.net.URLEncoder.encode("批量导入结果.xlsx", "UTF-8").replaceAll("\\+", "%20");
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + downloadName);
                com.alibaba.excel.ExcelWriter writer = null;
                try {
                    writer = com.alibaba.excel.EasyExcel.write(response.getOutputStream()).build();
                    com.alibaba.excel.write.metadata.WriteSheet s1 = com.alibaba.excel.EasyExcel.writerSheet(0, "汇总").head(com.xinkao.erp.question.excel.ImportResultSummaryRow.class).build();
                    com.alibaba.excel.write.metadata.WriteSheet s2 = com.alibaba.excel.EasyExcel.writerSheet(1, "错误明细").head(com.xinkao.erp.question.excel.ImportResultErrorRow.class).build();
                    writer.write(summary, s1);
                    writer.write(details, s2);
                } finally {
                    if (writer != null) writer.finish();
                }
                return;
            }

            boolean hasKnowledgePointNotMatched = false;
            boolean hasKnowledgePointFuzzyMatched = false;
            boolean hasOtherWarnings = false;
            int warningCount = 0;
            
            if (result.getRowErrors() != null) {
                for (com.xinkao.erp.question.vo.QuestionImportResultVO.RowError error : result.getRowErrors()) {
                    if (Boolean.TRUE.equals(error.getIsWarning())) {
                        warningCount++;
                        if ("KNOWLEDGE_POINT_NOT_MATCHED".equals(error.getWarningType())) {
                            hasKnowledgePointNotMatched = true;
                        } else if ("KNOWLEDGE_POINT_FUZZY_MATCHED".equals(error.getWarningType())) {
                            hasKnowledgePointFuzzyMatched = true;
                        } else {
                            hasOtherWarnings = true;
                        }
                    }
                }
            }
            
            boolean hasAnyWarnings = warningCount > 0;

            StringBuilder warningMsg = new StringBuilder();
            if (hasKnowledgePointNotMatched) {
                warningMsg.append("部分知识点未找到匹配");
            }
            if (hasKnowledgePointFuzzyMatched) {
                if (warningMsg.length() > 0) warningMsg.append("，");
                warningMsg.append("部分知识点为模糊匹配");
            }
            if (hasOtherWarnings) {
                if (warningMsg.length() > 0) warningMsg.append("，");
                warningMsg.append("存在其他警告信息");
            }
            
            if (result.getFailCount() > 0) {
                if (hasAnyWarnings) {
                    String message = String.format("导入完成，但存在%d条警告：%s，请检查并手动调整", warningCount, warningMsg.toString());
                    writeJson(response, BaseResponse.other(message, result));
                } else {
                    writeJson(response, BaseResponse.other("读取完成，但存在错误数据", result));
                }
            } else {
                if (hasAnyWarnings) {
                    String message = String.format("导入成功，但存在%d条警告：%s，建议检查并调整", warningCount, warningMsg.toString());
                    writeJson(response, BaseResponse.other(message, result));
                } else {
                    writeJson(response, BaseResponse.ok("读取成功", result));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(response, BaseResponse.fail("读取失败：" + e.getMessage()));
        }
    }


    @PrimaryDataSource
    @DataScope(role = "1")
    @PostMapping("/form/import-zip")
    public void importQuestionFormZipV2(@RequestParam("file") MultipartFile file,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        if (file == null || file.isEmpty()) {
            writeJson(response, BaseResponse.fail("请上传zip文件"));
            return;
        }
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".zip")) {
            writeJson(response, BaseResponse.fail("仅支持.zip文件"));
            return;
        }
        try {
            QuestionImportResultVO result = questionService.importQuestionFormZipV2(file);
            String export = request.getParameter("export");
            if ("excel".equalsIgnoreCase(export)) {
                java.util.List<com.xinkao.erp.question.excel.ImportResultSummaryRow> summary = new java.util.ArrayList<>();
                com.xinkao.erp.question.excel.ImportResultSummaryRow row = new com.xinkao.erp.question.excel.ImportResultSummaryRow();
                row.setTotalCount(result.getTotalCount());
                row.setSuccessCount(result.getSuccessCount());
                row.setFailCount(result.getFailCount());
                summary.add(row);
                java.util.List<com.xinkao.erp.question.excel.ImportResultErrorRow> details = new java.util.ArrayList<>();

                if (result.getRowErrors() != null && !result.getRowErrors().isEmpty()) {
                    for (com.xinkao.erp.question.vo.QuestionImportResultVO.RowError re : result.getRowErrors()) {
                        ImportResultErrorRow d = new ImportResultErrorRow();
                        d.setRowNum(re.getRowNum());
                        d.setMessage(re.getMessage());
                        details.add(d);
                    }
                }

                if (result.getErrorMessages() != null && !result.getErrorMessages().isEmpty()) {
                    for (String msg : result.getErrorMessages()) {
                        ImportResultErrorRow d = new ImportResultErrorRow();
                        Integer rn = null;
                        try {
                            int idx1 = msg.indexOf("第");
                            int idx2 = msg.indexOf("行");
                            if (idx1 >= 0 && idx2 > idx1) {
                                String num = msg.substring(idx1 + 1, idx2);
                                rn = Integer.parseInt(num);
                            }
                        } catch (Exception ignore) {}
                        d.setRowNum(rn);
                        d.setMessage(msg);
                        details.add(d);
                    }
                }

                String originHeaderValue = request.getHeader("Origin");
                if (originHeaderValue != null && originHeaderValue.length() > 0) {
                    response.setHeader("Access-Control-Allow-Origin", originHeaderValue);
                    response.setHeader("Vary", "Origin");
                }
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                String downloadName = java.net.URLEncoder.encode("题目单导入结果.xlsx", "UTF-8").replaceAll("\\+", "%20");
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + downloadName);
                com.alibaba.excel.ExcelWriter writer = null;
                try {
                    writer = com.alibaba.excel.EasyExcel.write(response.getOutputStream()).build();
                    com.alibaba.excel.write.metadata.WriteSheet s1 = com.alibaba.excel.EasyExcel.writerSheet(0, "汇总").head(com.xinkao.erp.question.excel.ImportResultSummaryRow.class).build();
                    com.alibaba.excel.write.metadata.WriteSheet s2 = com.alibaba.excel.EasyExcel.writerSheet(1, "错误明细").head(com.xinkao.erp.question.excel.ImportResultErrorRow.class).build();
                    writer.write(summary, s1);
                    writer.write(details, s2);
                } finally {
                    if (writer != null) writer.finish();
                }
                return;
            }


            boolean hasKnowledgePointNotMatched = false;
            boolean hasKnowledgePointFuzzyMatched = false;
            boolean hasQuestionTypeNotMatched = false;
            boolean hasOtherWarnings = false;
            int warningCount = 0;
            
            if (result.getRowErrors() != null) {
                for (com.xinkao.erp.question.vo.QuestionImportResultVO.RowError error : result.getRowErrors()) {

                    warningCount++;
                    if ("KNOWLEDGE_POINT_NOT_MATCHED".equals(error.getWarningType())) {
                        hasKnowledgePointNotMatched = true;
                    } else if ("KNOWLEDGE_POINT_FUZZY_MATCHED".equals(error.getWarningType())) {
                        hasKnowledgePointFuzzyMatched = true;
                    } else if ("QUESTION_TYPE_NOT_MATCHED".equals(error.getWarningType())) {
                        hasQuestionTypeNotMatched = true;
                    } else {
                        hasOtherWarnings = true;
                    }
                }
            }
            
            boolean hasAnyWarnings = warningCount > 0;

            StringBuilder warningMsg = new StringBuilder();
            if (hasKnowledgePointNotMatched) {
                warningMsg.append("部分知识点未找到匹配");
            }
            if (hasKnowledgePointFuzzyMatched) {
                if (warningMsg.length() > 0) warningMsg.append("，");
                warningMsg.append("部分知识点为模糊匹配");
            }
            if (hasQuestionTypeNotMatched) {
                if (warningMsg.length() > 0) warningMsg.append("，");
                warningMsg.append("部分题目分类不存在");
            }
            if (hasOtherWarnings) {
                if (warningMsg.length() > 0) warningMsg.append("，");
                warningMsg.append("存在其他警告信息");
            }
            
            if (result.getFailCount() != null && result.getFailCount() > 0) {
                if (hasAnyWarnings) {
                    String message = String.format("题目单导入完成，但存在%d条警告：%s，请检查并手动调整", warningCount, warningMsg.toString());
                    writeJson(response, BaseResponse.other(message, result));
                } else {
                    writeJson(response, BaseResponse.other("导入完成，但存在错误数据", result));
                }
            } else {
                if (hasAnyWarnings) {
                    String message = String.format("题目单导入成功，但存在%d条警告：%s，建议检查并调整", warningCount, warningMsg.toString());
                    writeJson(response, BaseResponse.other(message, result));
                } else {
                    writeJson(response, BaseResponse.ok("导入成功", result));
                }
            }
        } catch (Exception e) {
            writeJson(response, BaseResponse.fail("导入失败：" + e.getMessage()));
        }
    }


}
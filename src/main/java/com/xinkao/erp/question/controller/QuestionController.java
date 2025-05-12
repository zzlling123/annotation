package com.xinkao.erp.question.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.enums.system.OperationType;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.question.entity.Label;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.question.entity.QuestionLabel;
import com.xinkao.erp.question.entity.QuestionType;
import com.xinkao.erp.question.param.QuestionParam;
import com.xinkao.erp.question.query.QuestionQuery;
import com.xinkao.erp.question.service.LabelService;
import com.xinkao.erp.question.service.QuestionLabelService;
import com.xinkao.erp.question.service.QuestionService;
import com.xinkao.erp.question.service.QuestionTypeService;
import com.xinkao.erp.question.vo.QuestionInfoVo;
import com.xinkao.erp.question.vo.QuestionPageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

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
     * 获取题目分类下拉列表
     */
    @PrimaryDataSource
    @PostMapping("/getQuestionType/{shape}")
    @ApiOperation("获取题目分类下拉列表")
    public BaseResponse<List<QuestionType>> getQuestionType(@PathVariable String shape) {
        if ("500".equals(shape)){
            //创建固定字符串集合：2,3,4,5
            List<Integer> ids = Arrays.asList(2, 4, 5 ,6);
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
    @PostMapping("/save")
    @ApiOperation("新增题目")
    @Log(content = "新增题目",operationType = OperationType.INSERT)
    public BaseResponse<?> save(@Valid @RequestBody QuestionParam questionParam) {
        return questionService.save(questionParam);
    }

    /**
     * 编辑题目
     *
     * @param questionParam 题目参数
     * @return 操作结果
     */
    @PrimaryDataSource
    @PostMapping("/update")
    @ApiOperation("编辑题目")
    @Log(content = "编辑题目",operationType = OperationType.UPDATE)
    public BaseResponse<?> update(@Valid @RequestBody QuestionParam questionParam) {
        return questionService.update(questionParam);
    }

    /**
     * 批量删除题目
     *
     * @param param 题目ID列表
     * @return 操作结果
     */
    @PrimaryDataSource
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
    @PostMapping("/selfSave")
    @ApiOperation("题库按照分类、题型插入题目")
    public void selfSave(){
        questionService.selfSave();
    }
}
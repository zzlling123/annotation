package com.xinkao.erp.question.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.question.param.QuestionChildParam;
import com.xinkao.erp.question.param.QuestionFormTitleParam;
import com.xinkao.erp.question.param.QuestionParam;
import com.xinkao.erp.question.query.QuestionQuery;
import com.xinkao.erp.question.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 题库表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-22 11:19:40
 */
public interface QuestionService extends BaseService<Question> {

    /**
     * 分页查询题库
     *
     * @param query 查询条件
     * @param pageable 分页信息
     * @return 分页结果
     */
    Page<QuestionPageVo> page(QuestionQuery query, Pageable pageable);


    Page<QuestionExercisePageVo> page1(QuestionQuery query, Pageable pageable);

    /**
     * 获取题目详情
     *
     * @param id 题目ID
     * @return 题目详情
     */
    QuestionInfoVo getQuestionDetail(Integer id);

    /**
     * 新增题库
     *
     * @param questionParam 题库参数
     * @return 操作结果
     */
    BaseResponse<?> save(QuestionParam questionParam);

    /**
     * 新增题目单二级标题
     *
     * @param questionFormTitleParam 题目参数
     * @return 操作结果
     */
    BaseResponse<?> saveQuestionFormTitle(QuestionFormTitleParam questionFormTitleParam);

    /**
     * 编辑题目单二级标题
     *
     * @param questionFormTitleParam 题目参数
     * @return 操作结果
     */
    BaseResponse<?> updateQuestionFormTitle(QuestionFormTitleParam questionFormTitleParam);

    /**
     * 新增题目单子题
     *
     * @param questionChildParam 题目子题参数
     * @return 操作结果
     */
    BaseResponse<?> saveQuestionChild(QuestionChildParam questionChildParam);

    /**
     * 编辑题目单子题
     *
     * @param questionChildParam 题目子题参数
     * @return 操作结果
     */
    BaseResponse<?> updateQuestionChild(QuestionChildParam questionChildParam);

    /**
     * 编辑题库
     *
     * @param questionParam 题库参数
     * @return 操作结果
     */
    BaseResponse<?> update(QuestionParam questionParam);

    /**
     * 批量删除题库
     *
     * @param param 题库ID列表
     * @return 操作结果
     */
    BaseResponse<?> del(DeleteParam param);


    void selfSave();

    BaseResponse<List<QuestionFormVo>> getQuestionFormInfo(Integer questionId);

    /**
     * 批量导入题目
     */
    QuestionImportResultVO importQuestions(MultipartFile file);

    /**
     * 批量导入试题单 V1
     */
    QuestionImportResultVO importQuestionFormZip(MultipartFile file) throws IOException;

    /**
     * 批量导入试题单 V2（多Sheet结构化，不使用分隔符）
     */
    QuestionImportResultVO importQuestionFormZipV2(MultipartFile file) throws IOException;
}
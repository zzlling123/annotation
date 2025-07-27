package com.xinkao.erp.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.exam.entity.Exam;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.exam.excel.ExamPageSetVo;
import com.xinkao.erp.exam.param.ExamParam;
import com.xinkao.erp.exam.query.ExamQuery;
import com.xinkao.erp.exam.vo.ExamDetailVo;
import com.xinkao.erp.exam.vo.ExamPageVo;

import java.util.List;

/**
 * <p>
 * 考试表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
public interface ExamService extends BaseService<Exam> {

    /**
     * 分页查询考试信息
     *
     * @param query 查询条件
     * @param pageable 分页信息
     * @return 分页结果
     */
    Page<ExamPageVo> page(ExamQuery query, Pageable pageable);

    /**
     * 查看考试详情
     *
     * @param id 考试ID
     * @return 考试详情
     */
    ExamDetailVo detail(Integer id);

    /**
     * 新增考试
     *
     * @param examParam 考试信息
     * @return 操作结果
     */
    BaseResponse<?> save(ExamParam examParam);

    /**
     * 编辑考试
     *
     * @param examParam 考试信息
     * @return 操作结果
     */
    BaseResponse<?> update(ExamParam examParam);

    /**
     * 删除考试
     *
     * @param id 考试ID
     * @return 操作结果
     */
    BaseResponse<?> del(Integer id);

    /**
     * 根据题目类型和试卷形状获取试卷填充题库数量
     *
     */
    List<ExamPageSetVo> getExamPageSetByTypeAndShape(String examId);
}
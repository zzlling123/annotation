package com.xinkao.erp.exam.service;

import com.xinkao.erp.common.model.HandleResult;
import com.xinkao.erp.exam.entity.ExamPageSet;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.exam.entity.ExamPageSetType;
import com.xinkao.erp.exam.excel.ExamPageSetImportErrorModel;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 考试设置表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:09:19
 */
public interface ExamPageSetService extends BaseService<ExamPageSet> {

    void importExamPageSetType(HttpServletResponse response, Map<Integer, List<ExamPageSetType>> addExamPageSetPointMap, HandleResult handleResult, List<ExamPageSetImportErrorModel> examPageSetImportErrorModelList, String token);

}

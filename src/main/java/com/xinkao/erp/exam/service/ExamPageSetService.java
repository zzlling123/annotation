package com.xinkao.erp.exam.service;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.HandleResult;
import com.xinkao.erp.exam.entity.ExamPageSet;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.exam.entity.ExamPageSetType;
import com.xinkao.erp.exam.excel.ExamPageSetImportErrorModel;
import com.xinkao.erp.exam.param.ExamPageSetParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ExamPageSetService extends BaseService<ExamPageSet> {

    void importExamPageSetType(HttpServletResponse response, Map<Integer, List<ExamPageSetType>> addExamPageSetPointMap, HandleResult handleResult, List<ExamPageSetImportErrorModel> examPageSetImportErrorModelList, String token);

    BaseResponse<?> saveExamPageSetPoint(String examId, List<ExamPageSetParam> list);
}

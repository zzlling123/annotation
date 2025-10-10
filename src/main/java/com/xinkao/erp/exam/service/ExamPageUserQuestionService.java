package com.xinkao.erp.exam.service;

import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.exam.entity.ExamPageSet;
import com.xinkao.erp.exam.entity.ExamPageUser;
import com.xinkao.erp.exam.entity.ExamPageUserQuestion;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.user.entity.User;

import java.util.List;
import java.util.Map;

public interface ExamPageUserQuestionService extends BaseService<ExamPageUserQuestion> {

    void rollMaking(ExamPageSet examPageSet, List<User> userList, String token);

    BaseResponse<Map<String,Integer>> getProgress(String examId, String token);
}

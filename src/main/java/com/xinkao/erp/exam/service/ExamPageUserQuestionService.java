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

/**
 * <p>
 * 试卷表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2025-03-29 16:06:02
 */
public interface ExamPageUserQuestionService extends BaseService<ExamPageUserQuestion> {

    /**
     * 制卷
     * @param examPageSet
     * @param userList
     * @param token
     * @return
     */
    BaseResponse<JSONObject> rollMaking(ExamPageSet examPageSet, List<User> userList, String token);

    /**
     * 查询生成进度
     * @param examPageSet
     * @param token
     * @return
     */
    BaseResponse<Map<String,Integer>> getProgress(String examId, String token);
}

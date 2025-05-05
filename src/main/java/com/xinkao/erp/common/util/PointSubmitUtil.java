package com.xinkao.erp.common.util;

import com.alibaba.fastjson.JSONArray;
import com.xinkao.erp.exam.entity.ExamPageUserAnswer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PointSubmitUtil {
    public Integer get3DPointScore(ExamPageUserAnswer examPageUserAnswer) {
        // 解析标准答案和学生作答答案
        List<Map<String, List<Map<String, Object>>>> rightAnswer = getAnswerFor3dJson(examPageUserAnswer.getRightAnswer());
        List<Map<String, List<Map<String, Object>>>> userAnswer = getAnswerFor3dJson(examPageUserAnswer.getUserAnswer());

        // 比对帧的个数
        if (rightAnswer.size() != userAnswer.size()) {
            return 0;
        }

        // 遍历每一帧进行比对
        for (int i = 0; i < rightAnswer.size(); i++) {
            Map<String, List<Map<String, Object>>> rightFrame = rightAnswer.get(i);
            Map<String, List<Map<String, Object>>> userFrame = userAnswer.get(i);

            // 比对每个数组的key
            if (!rightFrame.keySet().equals(userFrame.keySet())) {
                return 0;
            }

            // 比对每个数组的attr的第三个数组
            for (String key : rightFrame.keySet()) {
                List<Map<String, Object>> rightAttrs = rightFrame.get(key);
                List<Map<String, Object>> userAttrs = userFrame.get(key);

                for (int j = 0; j < rightAttrs.size(); j++) {
                    JSONArray rightAttr = (JSONArray) rightAttrs.get(j).get("attr");
                    JSONArray userAttr = (JSONArray) userAttrs.get(j).get("attr");
                    //获取rightAttr的第三个数组
                    List<String> rightThirdAttr = (List<String>) rightAttr.get(2);
                    List<String> userThirdAttr = (List<String>) userAttr.get(2);

                    if (!rightThirdAttr.equals(userThirdAttr)) {
                        return 0;
                    }

                    // 计算position的误差
                    Map<String, Double> rightPosition = (Map<String, Double>) rightAttrs.get(j).get("position");
                    Map<String, Double> userPosition = (Map<String, Double>) userAttrs.get(j).get("position");

                    double xDiff = Math.abs(rightPosition.get("x") - userPosition.get("x"));
                    double yDiff = Math.abs(rightPosition.get("y") - userPosition.get("y"));
                    double zDiff = Math.abs(rightPosition.get("z") - userPosition.get("z"));

                    if (xDiff > 3 || yDiff > 3 || zDiff > 3) {
                        return 0;
                    }
                }
            }
        }

        // 如果以上条件都满足，则返回满分
        return 10;
    }

    public List<Map<String, List<Map<String, Object>>>> getAnswerFor3dJson(String answer){
        return null;
    }
}

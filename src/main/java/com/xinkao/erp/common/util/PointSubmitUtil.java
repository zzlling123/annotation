package com.xinkao.erp.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.exam.entity.ExamPageUserAnswer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PointSubmitUtil {
    public Integer get3DPointScore(ExamPageUserAnswer examPageUserAnswer) {
        // 解析标准答案和学生作答答案
        Map<String,List<Map<String, List<Map<String, Object>>>>> rightAnswer = getAnswerFor3dJson(examPageUserAnswer.getRightAnswer());
        Map<String,List<Map<String, List<Map<String, Object>>>>> userAnswer = getAnswerFor3dJson(examPageUserAnswer.getUserAnswer());

        // 比对帧的个数
        if (rightAnswer.size() != userAnswer.size()) {
            return 0;
        }
        if (rightAnswer.size() == 0 || userAnswer.size() == 0) {
            return 0;
        }
        //按照每帧进行循环
        for (String s : rightAnswer.keySet()) {
            List<Map<String, List<Map<String, Object>>>> rightFrame = rightAnswer.get(s);
            List<Map<String, List<Map<String, Object>>>> userFrame = userAnswer.get(s);
            if (rightFrame.size() != userFrame.size()) {
                return 0;
            }
            // 将该帧内每个数组key进行比对
            for (int i = 0; i < rightFrame.size(); i++) {
                Map<String, List<Map<String, Object>>> rightFrameForOne = rightFrame.get(i);
                Map<String, List<Map<String, Object>>> userFrameForOne = userFrame.get(i);

                // 比对每个数组的key
                if (!rightFrameForOne.keySet().equals(userFrameForOne.keySet())) {
                    return 0;
                }

                // 比对每个数组的attr的第三个数组
                for (String key : rightFrameForOne.keySet()) {
                    List<Map<String, Object>> rightAttrs = rightFrameForOne.get(key);
                    List<Map<String, Object>> userAttrs = userFrameForOne.get(key);

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
                        Map<String, BigDecimal> rightPosition = (Map<String, BigDecimal>) rightAttrs.get(j).get("position");
                        Map<String, BigDecimal> userPosition = (Map<String, BigDecimal>) userAttrs.get(j).get("position");
                        //将两方的xyz进行误差计算，由于其中某个值可能为负数，所以需要判断是否为负数，如果是，则取绝对值
                        double xDiff = calculatePosition(rightPosition.get("x").toString(),userPosition.get("x").toString());
                        double yDiff = calculatePosition(rightPosition.get("y").toString(),userPosition.get("y").toString());
                        double zDiff = calculatePosition(rightPosition.get("z").toString(),userPosition.get("z").toString());

                        if (xDiff > 3 || yDiff > 3 || zDiff > 3) {
                            return 0;
                        }
                    }
                }
            }
        }
        // 如果以上条件都满足，则返回满分
        return examPageUserAnswer.getScore();
    }

    //首先将两个值进行保留1位小数转换，计算两个坐标点的位置误差，如果同为正，则计算差值，如果同为负，则计算差值后取绝对值，如果一正一负，则直接相加
    public double calculatePosition(String x1, String x2) {
        double calculate = 0.0;
        //将两个值进行保留1位小数,将第一个小数点后数据进行去除
        x1 = x1.substring(0, x1.indexOf(".") + 2);
        x2 = x2.substring(0, x2.indexOf(".") + 2);
        //将两个值进行比较，如果相同，则计算差值，如果不同，则直接相加
        Double x1Double = Double.parseDouble(x1);
        Double x2Double = Double.parseDouble(x2);
        if(x1Double > 0 && x2Double > 0){
            calculate = Math.abs(x1Double - x2Double);
        }else if(x1Double < 0 && x2Double < 0){
            calculate = Math.abs(x1Double - x2Double);
        }else{
            calculate = Math.abs(x1Double) + Math.abs(x2Double);
        }
        return calculate;
    }

    public Map<String,List<Map<String, List<Map<String, Object>>>>> getAnswerFor3dJson(String answer){
        Map<String,List<Map<String, List<Map<String, Object>>>>> answerMap = new HashMap<>();
        //解析JSON
        JSONArray jsonArray = JSON.parseArray(answer);
        for (int i = 0; i < jsonArray.size(); i++) {
            List<Map<String, List<Map<String, Object>>>> answerList = new ArrayList<>();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            for (String key : jsonObject.keySet()) {
                JSONArray jsonArray1 = jsonObject.getJSONArray(key);
                List<Map<String, Object>> list = new ArrayList<>();
                for (int j = 0; j < jsonArray1.size(); j++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                    Map<String, Object> map = new HashMap<>();
                    map.put("attr", jsonObject1.getJSONArray("attr"));
                    map.put("position", jsonObject1.getJSONObject("position"));
                    list.add(map);
                }
                Map<String, List<Map<String, Object>>> map = new HashMap<>();
                map.put(key, list);
                answerList.add(map);
            }
            answerMap.put(String.valueOf(i+1), answerList);
        }
        return answerMap;
    }
}

package com.xinkao.erp.common.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.exam.entity.Cuboid;
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
                //每一个的对象
                //{
                //                "attr": [
                //                    [
                //                        "24"
                //                    ],
                //                    [
                //                        "29"
                //                    ],
                //                    [
                //                        "33"
                //                    ]
                //                ],
                //                "position": {
                //                    "x": -1.7730334997177124,
                //                    "y": 1.118543565273284,
                //                    "z": 3.496040105819702
                //                },
                //                "size": {
                //                    "x": 4,
                //                    "y": 2,
                //                    "z": 2
                //                }
                //            }
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
                        //循环判断每一个userAttr中的标记、误差
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
                        Map<String, Integer> rightSize = (Map<String, Integer>) rightAttrs.get(j).get("size");
                        Map<String, BigDecimal> userPosition = (Map<String, BigDecimal>) userAttrs.get(j).get("position");
                        Map<String, Integer> userSize = (Map<String, Integer>) userAttrs.get(j).get("size");
                        //组成Cuboid进行比对
                        Cuboid cuboid1 = new Cuboid(get3DDouble(rightPosition.get("x")), get3DDouble(rightPosition.get("y")), get3DDouble(rightPosition.get("z")), rightSize.get("x"), rightSize.get("y"), rightSize.get("z"));
                        Cuboid cuboid2 = new Cuboid(get3DDouble(userPosition.get("x")), get3DDouble(userPosition.get("y")), get3DDouble(userPosition.get("z")), userSize.get("x"), userSize.get("y"), userSize.get("z"));

                        double iou = calculateIoU(cuboid1, cuboid2);
                        System.out.println("重叠率: " + iou); // 输出约为0.0667（6.67%）
                        if (iou < 0.95 || iou > 1.05) {
                            return 0;
                        }
                    }
                }
            }
        }
        // 如果以上条件都满足，则返回满分
        return examPageUserAnswer.getScore();
    }

    public double get3DDouble(BigDecimal bigStr){
        if (StrUtil.isBlank(bigStr.toString())){
            return 0;
        }
        String str = bigStr.toString();
        return Double.parseDouble(str.substring(0, str.indexOf(".") + 2));
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
                    map.put("size", jsonObject1.getJSONObject("size"));
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

    public static double calculateIoU(Cuboid cuboid1, Cuboid cuboid2) {
        // 计算各轴投影是否有重叠
        double overlapX = calculateAxisOverlap(
                cuboid1.getMinX(), cuboid1.getMaxX(),
                cuboid2.getMinX(), cuboid2.getMaxX()
        );
        double overlapY = calculateAxisOverlap(
                cuboid1.getMinY(), cuboid1.getMaxY(),
                cuboid2.getMinY(), cuboid2.getMaxY()
        );
        double overlapZ = calculateAxisOverlap(
                cuboid1.getMinZ(), cuboid1.getMaxZ(),
                cuboid2.getMinZ(), cuboid2.getMaxZ()
        );

        // 若任一轴无重叠，直接返回0
        if (overlapX <= 0 || overlapY <= 0 || overlapZ <= 0) {
            return 0.0;
        }

        // 计算重叠体积
        double overlapVolume = overlapX * overlapY * overlapZ;

        // 计算交并比
        double volume1 = cuboid1.getVolume();
        double volume2 = cuboid2.getVolume();
        return overlapVolume / (volume1 + volume2 - overlapVolume);
    }

    // 单轴重叠长度计算
    private static double calculateAxisOverlap(double min1, double max1, double min2, double max2) {
        double start = Math.max(min1, min2);
        double end = Math.min(max1, max2);
        return end - start;
    }
}

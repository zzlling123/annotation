package com.xinkao.erp.common.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.exam.entity.Cuboid;
import com.xinkao.erp.exam.entity.ExamPageUserAnswer;
import com.xinkao.erp.exam.service.ExamPageUserAnswerService;
import com.xinkao.erp.exercise.param.PanJuanParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PointSubmitUtil {
    public PanJuanParam get3DPointScore(ExamPageUserAnswer examPageUserAnswer) {
        // 解析标准答案和学生作答答案
        Map<String,List<Map<String, List<Map<String, Object>>>>> rightAnswer = getAnswerFor3dJson(examPageUserAnswer.getRightAnswer());
        Map<String,List<Map<String, List<Map<String, Object>>>>> userAnswer = getAnswerFor3dJson(examPageUserAnswer.getUserAnswer());
        PanJuanParam dto = new PanJuanParam();
        int biao = 0;//正确标注个数
        int cuo = 0;//应该标注未标注个数
        int wu = 0;//错误标注个数
        int shu = 0;//属性个数
        int zong = 0;//总共需要标注个数
        int da = 0;//学生标注个数
        boolean is_error  = false;
        if (rightAnswer.size() == 0 || userAnswer.size() == 0){
            dto.setIsCorrect(1);
            dto.setBiao(biao);
            dto.setCuo(cuo);
            dto.setWu(wu);
            dto.setShu(shu);
            dto.setZong(zong);
            dto.setDa(da);
            dto.setAccuracyRate(new BigDecimal(0));
            dto.setCoverageRate(new BigDecimal(0));
            dto.setScore(new BigDecimal(0));
            return dto;
        }
        // 比对帧的个数
        if (rightAnswer.size() != userAnswer.size()) {
            is_error = true;
        }
        //计算学生标注数量，避免直接出错导致没有计算答题数
        for (String s : userAnswer.keySet()) {
            List<Map<String, List<Map<String, Object>>>> userFrame = userAnswer.get(s);
            for (int i = 0; i < userFrame.size(); i++) {
                Map<String, List<Map<String, Object>>> userFrameForOne = userFrame.get(i);
                for (String key : userFrameForOne.keySet()) {
                    List<Map<String, Object>> userAttrs = userFrameForOne.get(key);
                    da += userAttrs.size();
                }
            }
        }
        //按照每帧进行循环
        for (String s : rightAnswer.keySet()) {
            List<Map<String, List<Map<String, Object>>>> rightFrame = new ArrayList<>();
            List<Map<String, List<Map<String, Object>>>> userFrame = new ArrayList<>();
            try{
                rightFrame = rightAnswer.get(s);
                userFrame = userAnswer.get(s);
            }catch (Exception e){
                //跳过
                is_error = true;
                continue;
            }
            if (userFrame == null || userFrame.isEmpty()){
                is_error = true;
                continue;
            }
            if (rightFrame.size() != userFrame.size()) {
                is_error = true;
            }
            // 将该帧内每个数组key进行比对
            for (int i = 0; i < rightFrame.size(); i++) {
                Map<String, List<Map<String, Object>>> rightFrameForOne = new HashMap<>();
                Map<String, List<Map<String, Object>>> userFrameForOne = new HashMap<>();
                try{
                    rightFrameForOne = rightFrame.get(i);
                    userFrameForOne = userFrame.get(i);
                }catch (Exception e){
                    //跳过
                    is_error = true;
                    continue;
                }

                // 比对每个数组的key
                if (!rightFrameForOne.keySet().equals(userFrameForOne.keySet())) {
                    is_error = true;
                }

                // 比对每个数组的attr的第三个数组
                for (String key : rightFrameForOne.keySet()) {
                    List<Map<String, Object>> rightAttrs = rightFrameForOne.get(key);
                    List<Map<String, Object>> userAttrs = userFrameForOne.get(key);
                    if (userAttrs == null){
                        zong += rightAttrs.size();
                        continue;
                    }
                    if (rightAttrs.size() != userAttrs.size()){
                        is_error = true;
                    }
                    zong += rightAttrs.size();
                    for (int j = 0; j < rightAttrs.size(); j++) {
                        //循环判断每一个userAttr中的标记、误差
                        JSONArray rightAttr = (JSONArray) rightAttrs.get(j).get("attr");
                        //获取rightAttr的第三个数组
                        List<String> rightThirdAttr = (List<String>) rightAttr.get(2);
                        shu++;
                        // 计算position的误差
                        Map<String, BigDecimal> rightPosition = (Map<String, BigDecimal>) rightAttrs.get(j).get("position");
                        Map<String, Object> rightSize = (Map<String, Object>) rightAttrs.get(j).get("size");
                        for (int k = 0; k < userAttrs.size(); k++){
                            //循环遍历userAttrs，判断是否有attr一致且误差值小于0.95或不大于1.05，如果循环完毕没有符合的则返回0
                            JSONArray userAttr = (JSONArray) userAttrs.get(k).get("attr");
                            List<String> userThirdAttr = (List<String>) userAttr.get(2);
                            if (!rightThirdAttr.equals(userThirdAttr)) {
                                //跳过，继续验证下一条
                                // 如果循环完毕没有符合的则返回0
                                if (k == userAttrs.size() - 1) {
                                    is_error = true;
                                    cuo++;
                                    wu++;
                                }
                                continue;
                            }
                            Map<String, BigDecimal> userPosition = (Map<String, BigDecimal>) userAttrs.get(k).get("position");
                            Map<String, Object> userSize = (Map<String, Object>) userAttrs.get(k).get("size");
                            //组成Cuboid进行比对
                            Cuboid cuboid1 = new Cuboid(get3DDouble(rightPosition.get("x")), get3DDouble(rightPosition.get("y")), get3DDouble(rightPosition.get("z")), get3DDoubleStr(rightSize.get("x")), get3DDoubleStr(rightSize.get("y")), get3DDoubleStr(rightSize.get("z")));
                            Cuboid cuboid2 = new Cuboid(get3DDouble(userPosition.get("x")), get3DDouble(userPosition.get("y")), get3DDouble(userPosition.get("z")), get3DDoubleStr(userSize.get("x")), get3DDoubleStr(userSize.get("y")), get3DDoubleStr(userSize.get("z")));

                            double iou = calculateIoU(cuboid1, cuboid2);
                            System.out.println("重叠率: " + iou); // 输出约为0.0667（6.67%）
                            if (iou < 0.8 || iou > 1.2) {
                                //跳过，继续验证下一条
                                // 如果循环完毕没有符合的则返回0
                                if (k == userAttrs.size() - 1) {
                                    is_error = true;
                                }
                            }else{
                                biao++;
                                //如果有符合的则删除该元素，终结该for循环，继续向下循环
                                //删除userAttrs中的该元素
                                userAttrs.remove(k);
                                break;
                            }
                        }
                    }
                }
            }
        }
        BigDecimal score = examPageUserAnswer.getScore().multiply(new BigDecimal(biao  / (float)zong));
        String str = String.valueOf(score);
        str = str.substring(0, str.indexOf("."));

        dto.setIsCorrect(is_error ? 0 : 1);
        dto.setBiao(biao);
        dto.setCuo(cuo);
        dto.setWu(wu);
        dto.setShu(shu);
        dto.setZong(zong);
        dto.setDa(da);
        dto.setAccuracyRate(zong == 0 ? new BigDecimal(0) :new BigDecimal(biao).divide(new BigDecimal(zong), 2, RoundingMode.HALF_UP));
        dto.setCoverageRate(da == 0 ? new BigDecimal(0) :new BigDecimal(biao).divide(new BigDecimal(da), 2, RoundingMode.HALF_UP));
        dto.setScore(new BigDecimal(str));
        return dto;
    }

    public double get3DDouble(BigDecimal bigStr){
        if (StrUtil.isBlank(bigStr.toString())){
            return 0;
        }
        String str = bigStr.toString();
        return Double.parseDouble(str.substring(0, str.indexOf(".") + 2));
    }

    public double get3DDoubleStr(Object str){
        String newStr = String.valueOf(str);
        return Double.parseDouble(newStr.substring(0, newStr.indexOf(".") + 2));
    }

    public Map<String,List<Map<String, List<Map<String, Object>>>>> getAnswerFor3dJson(String answer){
        Map<String,List<Map<String, List<Map<String, Object>>>>> answerMap = new HashMap<>();
        if (StrUtil.isBlank(answer)){
            return answerMap;
        }
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

package com.xinkao.erp.exercise.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinkao.erp.common.util.PointSubmitUtil;
import com.xinkao.erp.exam.entity.ExamPageUserAnswer;
import com.xinkao.erp.exercise.param.PanJuanParam;
import com.xinkao.erp.exercise.utils.jx2d.AttrData;
import com.xinkao.erp.exercise.utils.jx2d.Segment;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MarkQuestionUtils {
    @Resource
    private PointSubmitUtil pointSubmitUtil;
    /**
     * 判断输入的答案是否正确，如果正确，则设置用户得分为题目的得分，否则设置为0
     * */
    public int checkAnswer(String userAnswer, String correctAnswer, int shape, Integer score, int moduleId) {
        //shape题目类型:100-单选 200-多选 300-填空 400-主观题 500-操作题
        if (shape == 100) {
            if (userAnswer.equals(correctAnswer)) {
                return score;
            }
        }else if (shape == 200) {
            //选题少答的一半分数，错答得0分
            String [] userAnswers = userAnswer.split("");
            String [] correctAnswers = correctAnswer.split("");

            if (userAnswer.length() == correctAnswer.length()) {
                for (int i = 0; i < userAnswers.length; i++) {
                    //判断用户答案是否存在与正确答案的数组中，如果存在则继续，如果不存在则返回0
                    if(correctAnswer.contains(userAnswers[i])){
                        continue;
                    }else {
                        return 0;
                    }
                }
                return score;
            }else if (userAnswer.length() < correctAnswer.length()) {
                for (int i = 0; i < userAnswers.length; i++) {
                    if (userAnswers[i].equals(correctAnswers[i])) {
                        continue;
                    }else {
                        return 0;
                    }
                }
                return score/2;
            }else if (userAnswer.length() > correctAnswer.length()) {
                return 0;
            }
        }else if (shape == 300) {
            if (userAnswer.equals(correctAnswer)) {
                return score;
            }
        }else if (shape == 400) {
            if (userAnswer.equals(correctAnswer)) {
                return score;
            }
        }
        return 0;
    }

    public PanJuanParam checkAnswerCaoZuo(String userAnswer, String correctAnswer, int shape, Integer score, int moduleId) {
        if (moduleId==1){//1// 	图像标注
            return check_answer_2D_xyq(userAnswer,correctAnswer);
            //return panJuanParam.getCoverageRate()*score;
            //return panJuanParam.getCoverageRate().multiply(new BigDecimal(score)).setScale(0, RoundingMode.HALF_UP).intValueExact();
        }else if (moduleId==2||moduleId==7){//2	3D点云标注
            ExamPageUserAnswer examPageUserAnswer = new ExamPageUserAnswer();
            examPageUserAnswer.setUserAnswer(userAnswer);
            examPageUserAnswer.setRightAnswer(correctAnswer);
            examPageUserAnswer.setShape(shape);
            examPageUserAnswer.setScore(new BigDecimal(score));
            return pointSubmitUtil.get3DPointScore(examPageUserAnswer);
        }else if (moduleId==3){//3	OCR标注
            return check_answer_2D_xyq(userAnswer,correctAnswer);
        }else if (moduleId==4){//4	语音标注
            PanJuanParam panJuanParam = new PanJuanParam();
            if (check_answer_voice(userAnswer,correctAnswer)){
                panJuanParam.setIsCorrect(1);
                panJuanParam.setCoverageRate(new BigDecimal(1));
                panJuanParam.setAccuracyRate(new BigDecimal(1));
            }else {
                panJuanParam.setIsCorrect(0);
                panJuanParam.setCoverageRate(new BigDecimal(0));
                panJuanParam.setAccuracyRate(new BigDecimal(0));
            }
            return panJuanParam;
        }else if (moduleId==5){//5	2D标注
            return check_answer_2D_xyq(userAnswer,correctAnswer);
        }else if (moduleId==6){//6	人脸关键点标注
            return check_answer_2D_xyq(userAnswer,correctAnswer);
        }
        return null;
    }

    public static Map<String, List<AttrData>> parseJson(String jsonData) {
        //判断两边是否有[]，有就去掉
        if (jsonData.startsWith("[") && jsonData.endsWith("]")) {
            jsonData = jsonData.substring(1, jsonData.length() - 1);
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        Map<String, List<AttrData>> result = new HashMap<>();

        for (String key : jsonObject.keySet()) {
            JSONArray dataArray = jsonObject.getJSONArray(key);
            List<AttrData> attrDataList = new ArrayList<>();
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject dataObj = dataArray.getJSONObject(i);
                attrDataList.add(new AttrData(dataObj));
            }
            result.put(key, attrDataList);
        }
        return result;
    }

    public static List<Segment> parseSegments(String jsonData) {
        return JSON.parseArray(jsonData, Segment.class);
    }

    public PanJuanParam check_answer_2D_xyq(String userAnswer, String correctAnswer){
        PanJuanParam panJuanParam = new PanJuanParam();
        int zong = 0; //需要标注的个数
        int biao = 0;  //标注个数
        int cuo = 0;//应该标注未标注个数
        int wu = 0;//错误标注个数
        int shu = 0;//属性个数
        int da = 0; //学生标注个数
        long operationDuration = 0;
        double accuracyRate = 0;
        double coverageRate = 0;
        //标准答案
        Map<String, List<AttrData>> correctAnswerMap = parseJson(correctAnswer);
        //学生作答
        Map<String, List<AttrData>> userAnswerMap = parseJson(userAnswer);
        //循环标准答案，先记录正确答案的总标记数量
        for (Map.Entry<String, List<AttrData>> entry : correctAnswerMap.entrySet()) {
            for (AttrData data : entry.getValue()) {
                zong++;
            }
        }
        panJuanParam.setZong(zong);
        //循环学生答案，对应正确答案中是否存在
        for (Map.Entry<String, List<AttrData>> entry : userAnswerMap.entrySet()) {
            //获取标注类别，比如小轿车为9
            String key_type_name = entry.getKey();
            //判断值是否存在与正确答案中
            //判断entry.getValue()是否是空
            if (entry.getValue() != null ||  entry.getValue().size() > 0 ) {
                correctAnswerMap = parseJson(correctAnswer);
                for (AttrData data : entry.getValue()) {
                    da++;
                    if (correctAnswerMap.containsKey(key_type_name)) {
                        AttrData closest_attrData = AttrDataUtils.findClosestWithSameAttr(data, correctAnswerMap.get(key_type_name));
                        if (closest_attrData != null) {
                            //暂时去掉正确答案中的这个对象
                            correctAnswerMap.get(key_type_name).remove(closest_attrData);
                            System.out.println("找到最接近且 attr 相同的对象: " + closest_attrData);
                            //判断两个对象中数值的误差值小于5%，则认为该对象是正确的
                            if (AttrDataUtils.isNumericalClose(data, closest_attrData)) {
                                biao++;
                            } else {
                                wu++;
                            }
                        } else {
                            wu ++;
                        }
                    }
                    if (data.attr.size()>0){
                        shu = shu+1;
                    }
                }
            }
        }
        if(da==0){
            panJuanParam.setIsCorrect(3);
        }else {
            if (biao>0&&biao==zong){
                panJuanParam.setIsCorrect(1);
            }else if (biao>0&&biao<zong){
                panJuanParam.setIsCorrect(2);
            }else {
                panJuanParam.setIsCorrect(0);
            }
        }
        cuo = zong - biao;
        panJuanParam.setBiao(biao);
        panJuanParam.setCuo(cuo);
        panJuanParam.setWu(wu);
        panJuanParam.setShu(shu);
        panJuanParam.setZong(zong);
        panJuanParam.setDa(da);
        if (zong!=0){
            panJuanParam.setCoverageRate(new BigDecimal(biao).divide(new BigDecimal(zong), 2, RoundingMode.HALF_UP));
        }else {
            panJuanParam.setCoverageRate(new BigDecimal(0));
        }
        if (da!=0){
            panJuanParam.setAccuracyRate(new BigDecimal(biao).divide(new BigDecimal(da), 2, RoundingMode.HALF_UP));
        }else {
            panJuanParam.setAccuracyRate(new BigDecimal(0));
        }
        panJuanParam.setOperationDuration(operationDuration);
        return panJuanParam;
    }

    public static PanJuanParam check_answer_2D_xyq_ceshi(String userAnswer, String correctAnswer){
        PanJuanParam panJuanParam = new PanJuanParam();
        int zong = 0; //需要标注的个数
        int biao = 0;  //标注个数
        int cuo = 0;//应该标注未标注个数
        int wu = 0;//错误标注个数
        int shu = 0;//属性个数
        int da = 0; //学生标注个数
        long operationDuration = 0;
        double accuracyRate = 0;
        double coverageRate = 0;
        //标准答案
        Map<String, List<AttrData>> correctAnswerMap = parseJson(correctAnswer);
        //学生作答
        Map<String, List<AttrData>> userAnswerMap = parseJson(userAnswer);
        //循环标准答案，先记录正确答案的总标记数量
        for (Map.Entry<String, List<AttrData>> entry : correctAnswerMap.entrySet()) {
            for (AttrData data : entry.getValue()) {
                zong++;
            }
        }
        panJuanParam.setZong(zong);
        //循环学生答案，对应正确答案中是否存在
        for (Map.Entry<String, List<AttrData>> entry : userAnswerMap.entrySet()) {
            //获取标注类别，比如小轿车为9
            String key_type_name = entry.getKey();
            //判断值是否存在与正确答案中
            //判断entry.getValue()是否是空
            if (entry.getValue() != null ||  entry.getValue().size() > 0 ) {
                correctAnswerMap = parseJson(correctAnswer);
                for (AttrData data : entry.getValue()) {
                    da++;
                    if (correctAnswerMap.containsKey(key_type_name)) {
                        AttrData closest_attrData = AttrDataUtils.findClosestWithSameAttr(data, correctAnswerMap.get(key_type_name));
                        if (closest_attrData != null) {
                            //暂时去掉正确答案中的这个对象
                            correctAnswerMap.get(key_type_name).remove(closest_attrData);
                            System.out.println("找到最接近且 attr 相同的对象: " + closest_attrData);
                            //判断两个对象中数值的误差值小于5%，则认为该对象是正确的
                            if (AttrDataUtils.isNumericalClose(data, closest_attrData)) {
                                biao++;
                            } else {
                                wu++;
                            }
                        } else {
                            wu ++;
                        }
                    }
                    if (data.attr.size()>0){
                        shu = shu+1;
                    }
                }
            }
        }
        if(da==0){
            panJuanParam.setIsCorrect(3);
        }else {
            if (biao>0&&biao==zong){
                panJuanParam.setIsCorrect(1);
            }else if (biao>0&&biao<zong){
                panJuanParam.setIsCorrect(2);
            }else {
                panJuanParam.setIsCorrect(0);
            }
        }
        cuo = zong - biao;
        panJuanParam.setBiao(biao);
        panJuanParam.setCuo(cuo);
        panJuanParam.setWu(wu);
        panJuanParam.setShu(shu);
        panJuanParam.setZong(zong);
        panJuanParam.setDa(da);

        panJuanParam.setAccuracyRate(new BigDecimal(biao).divide(new BigDecimal(zong), 2, RoundingMode.HALF_UP));
        panJuanParam.setCoverageRate(new BigDecimal(biao).divide(new BigDecimal(zong), 2, RoundingMode.HALF_UP));
        panJuanParam.setOperationDuration(operationDuration);
        return panJuanParam;
    }

    public boolean check_answer(String userAnswer, String correctAnswer){
        ObjectMapper objectMapper = new ObjectMapper();
        boolean is_correct = true;
        try {
            Map<String, Object> userAnswerMap = objectMapper.readValue(userAnswer, Map.class);
            Map<String, Object> correctAnswerMap = objectMapper.readValue(correctAnswer, Map.class);
            if (userAnswerMap.size() == correctAnswerMap.size()) {
                for (Map.Entry<String, Object> entry : userAnswerMap.entrySet()) {
                    String key = entry.getKey();
                    Object userValue = entry.getValue();
                    Object correctValue = correctAnswerMap.get(key);
                    if (userValue instanceof Map && correctValue instanceof Map) {
                        check_answer(userValue.toString(), correctValue.toString());
                    }else if (userValue instanceof String && correctValue instanceof String) {
                        if (!userValue.equals(correctValue)) {
                            is_correct = false;
                            break;
                        }
                    }
                }
            }else {
                is_correct = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is_correct;
    }

    /**
     * 2D标注判断对错
     * */
    public boolean check_answer_2D(String userAnswer, String correctAnswer){
        ObjectMapper objectMapper = new ObjectMapper();
        boolean is_correct = true;
        try {
            if (userAnswer.toString().startsWith("[") && userAnswer.toString().endsWith("]")) {
                List<Map<String, Object>> userAnswerList = objectMapper.readValue(userAnswer, List.class);
                List<Map<String, Object>> correctAnswerList = objectMapper.readValue(correctAnswer, List.class);
                if (userAnswerList.size() == correctAnswerList.size()) {
                    for (int i = 0; i < userAnswerList.size(); i++) {
                        Map<String, Object> userAnswerMap = userAnswerList.get(i);
                        Map<String, Object> correctAnswerMap = correctAnswerList.get(i);
                        if (userAnswerMap.size() == correctAnswerMap.size()) {
                            for (Map.Entry<String, Object> entry : userAnswerMap.entrySet()) {
                                String key = entry.getKey();
                                Object userValue = entry.getValue();
                                Object correctValue = correctAnswerMap.get(key);
                                if ("attr".equals(key)){
                                    if(userValue.toString().equals(correctValue.toString())){
                                        continue;
                                    }else {
                                        is_correct = false;
                                        break;
                                    }
                                }
                                if ("x".equals(key)||"y".equals(key)||"width".equals(key)||"height".equals(key)){
                                    if (Math.abs(Double.parseDouble(userValue.toString()) - Double.parseDouble(correctValue.toString())) >3) {
                                        is_correct = false;
                                        break;
                                    }
                                }
                                if (userValue instanceof List && correctValue instanceof List) {
                                    boolean is_correct_2D = check_answer_2D(objectMapper.writeValueAsString(userValue), objectMapper.writeValueAsString(correctValue));
                                    if (!is_correct_2D){
                                        is_correct = false;
                                    }
                                }else if (userValue instanceof Map && correctValue instanceof Map) {
                                    boolean is_correct_2D = check_answer_2D(objectMapper.writeValueAsString(userValue), objectMapper.writeValueAsString(correctValue));
                                    if (!is_correct_2D){
                                        is_correct = false;
                                    }
                                }else if (userValue instanceof String && correctValue instanceof String) {
                                    if (!userValue.equals(correctValue)) {
                                        is_correct = false;
                                        break;
                                    }
                                }
                            }
                        }else {
                            is_correct = false;
                        }
                    }
                }else {
                    is_correct = false;
                }
            }else if (userAnswer.toString().startsWith("{") && userAnswer.toString().endsWith("}")) {
                Map<String, Object> userAnswerMap = objectMapper.readValue(userAnswer, Map.class);
                Map<String, Object> correctAnswerMap = objectMapper.readValue(correctAnswer, Map.class);
                if (userAnswerMap.size() == correctAnswerMap.size()) {
                    for (Map.Entry<String, Object> entry : userAnswerMap.entrySet()) {
                        String key = entry.getKey();
                        Object userValue = entry.getValue();
                        Object correctValue = correctAnswerMap.get(key);
                        if ("attr".equals(key)){
                            if(userValue.toString().equals(correctValue.toString())){
                                continue;
                            }else {
                                is_correct = false;
                                break;
                            }
                        }
                        if ("x".equals(key)||"y".equals(key)||"width".equals(key)||"height".equals(key)){
                            if (Math.abs(Double.parseDouble(userValue.toString()) - Double.parseDouble(correctValue.toString())) >3) {
                                is_correct = false;
                                break;
                            }
                        }
                        if (userValue instanceof List && correctValue instanceof List) {
                            boolean is_correct_2D = check_answer_2D(objectMapper.writeValueAsString(userValue), objectMapper.writeValueAsString(correctValue));
                            if (!is_correct_2D){
                                is_correct = false;
                            }
                        }else if (userValue instanceof Map && correctValue instanceof Map) {
                            boolean is_correct_2D = check_answer_2D(objectMapper.writeValueAsString(userValue), objectMapper.writeValueAsString(correctValue));
                            if (!is_correct_2D){
                                is_correct = false;
                            }
                        }else if (userValue instanceof String && correctValue instanceof String) {
                            if (!userValue.equals(correctValue)) {
                                is_correct = false;
                                break;
                            }
                        }
                    }
                }else {
                    is_correct = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is_correct;
    }

    /**
     * 语音标注判断对错
     * */
    public boolean check_answer_voice(String userAnswer, String correctAnswer){
        ObjectMapper objectMapper = new ObjectMapper();
        boolean is_correct = true;
        try {
            if (userAnswer.toString().startsWith("[") && userAnswer.toString().endsWith("]")) {
                List<Map<String, Object>> userAnswerList = objectMapper.readValue(userAnswer, List.class);
                List<Map<String, Object>> correctAnswerList = objectMapper.readValue(correctAnswer, List.class);
                if (userAnswerList.size() == correctAnswerList.size()) {
                    for (int i = 0; i < userAnswerList.size(); i++) {
                        Map<String, Object> userAnswerMap = userAnswerList.get(i);
                        Map<String, Object> correctAnswerMap = correctAnswerList.get(i);
                        if (userAnswerMap.size() == correctAnswerMap.size()) {
                            for (Map.Entry<String, Object> entry : userAnswerMap.entrySet()) {
                                String key = entry.getKey();
                                Object userValue = entry.getValue();
                                Object correctValue = correctAnswerMap.get(key);
                                if ("attr".equals(key)){
                                    if(userValue.toString().equals(correctValue.toString())){
                                        continue;
                                    }else {
                                        is_correct = false;
                                        break;
                                    }
                                }
                                if ("end".equals(key)||"start".equals(key)){
                                    if (Math.abs(Double.parseDouble(userValue.toString()) - Double.parseDouble(correctValue.toString())) >3) {
                                        is_correct = false;
                                        break;
                                    }
                                }
                                if (userValue instanceof List && correctValue instanceof List) {
                                    boolean is_correct_2D = check_answer_voice(objectMapper.writeValueAsString(userValue), objectMapper.writeValueAsString(correctValue));
                                    if (!is_correct_2D){
                                        is_correct = false;
                                    }
                                }else if (userValue instanceof Map && correctValue instanceof Map) {
                                    boolean is_correct_2D = check_answer_voice(objectMapper.writeValueAsString(userValue), objectMapper.writeValueAsString(correctValue));
                                    if (!is_correct_2D){
                                        is_correct = false;
                                    }
                                }else if (userValue instanceof String && correctValue instanceof String) {
                                    if (!userValue.equals(correctValue)) {
                                        is_correct = false;
                                        break;
                                    }
                                }
                            }
                        }else {
                            is_correct = false;
                        }
                    }
                }else {
                    is_correct = false;
                }
            }else if (userAnswer.toString().startsWith("{") && userAnswer.toString().endsWith("}")) {
                Map<String, Object> userAnswerMap = objectMapper.readValue(userAnswer, Map.class);
                Map<String, Object> correctAnswerMap = objectMapper.readValue(correctAnswer, Map.class);
                if (userAnswerMap.size() == correctAnswerMap.size()) {
                    for (Map.Entry<String, Object> entry : userAnswerMap.entrySet()) {
                        String key = entry.getKey();
                        Object userValue = entry.getValue();
                        Object correctValue = correctAnswerMap.get(key);
                        if ("attr".equals(key)){
                            if(userValue.toString().equals(correctValue.toString())){
                                continue;
                            }else {
                                is_correct = false;
                                break;
                            }
                        }
                        if ("end".equals(key)||"start".equals(key)){
                            if (Math.abs(Double.parseDouble(userValue.toString()) - Double.parseDouble(correctValue.toString())) >3) {
                                is_correct = false;
                                break;
                            }
                        }
                        if (userValue instanceof List && correctValue instanceof List) {
                            boolean is_correct_2D = check_answer_voice(objectMapper.writeValueAsString(userValue), objectMapper.writeValueAsString(correctValue));
                            if (!is_correct_2D){
                                is_correct = false;
                            }
                        }else if (userValue instanceof Map && correctValue instanceof Map) {
                            boolean is_correct_2D = check_answer_voice(objectMapper.writeValueAsString(userValue), objectMapper.writeValueAsString(correctValue));
                            if (!is_correct_2D){
                                is_correct = false;
                            }
                        }else if (userValue instanceof String && correctValue instanceof String) {
                            if (!userValue.equals(correctValue)) {
                                is_correct = false;
                                break;
                            }
                        }
                    }
                }else {
                    is_correct = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is_correct;
    }

    public static void main(String[] args) {
//        String userAnswer = "{\"9\":[{\"attr\":[[\"24\"],[\"29\"],[\"33\"]],\"position\":{\"x\":74.17498779296875,\"y\":102.60000610351562},\"size\":{\"width\":70,\"height\":102.60000610351562}}],\"10\":[{\"attr\":[[\"51\"],[\"113\"],[\"186\"]],\"position\":{\"x\":275.17498779296875,\"y\":152.60000610351562},\"size\":{\"width\":100,\"height\":152.60000610351562}}]}";
//        String correctAnswer = "{\"9\":[{\"attr\":[[\"24\"],[\"29\"],[\"33\"]],\"position\":{\"x\":73.17498779296875,\"y\":100.60000610351562},\"size\":{\"width\":70,\"height\":102.60000610351562}}],\"10\":[{\"attr\":[[\"51\"],[\"113\"],[\"186\"]],\"position\":{\"x\":275.17498779296875,\"y\":152.60000610351562},\"size\":{\"width\":100,\"height\":152.60000610351562}}]}";
//        System.out.println("userAnswer:"+check_answer_2D(userAnswer,correctAnswer));

//        String userAnswer1 = "[{\"key\":\"wavesurfer_9ds7rp7qkj8\",\"id\":\"wavesurfer_9ds7rp7qkj8\",\"start\":9.299782779947916,\"end\":12.772280557725695,\"label\":\"未命名片段\",\"content\":\"无内容\",\"role\":\"AI\",\"text\":\"你好\"},{\"key\":\"wavesurfer_s0li512f2vo\",\"id\":\"wavesurfer_s0li512f2vo\",\"start\":30.94863611328125,\"end\":36.32015611328124,\"label\":\"未命名片段\",\"content\":\"无内容\",\"role\":\"真人\",\"text\":\"世界\"}]";
//        String correctAnswer1 = "[{\"key\":\"wavesurfer_9ds7rp7qkj8\",\"id\":\"wavesurfer_9ds7rp7qkj8\",\"start\":8.299782779947916,\"end\":10.772280557725695,\"label\":\"未命名片段\",\"content\":\"无内容\",\"role\":\"AI\",\"text\":\"你好\"},{\"key\":\"wavesurfer_s0li512f2vo\",\"id\":\"wavesurfer_s0li512f2vo\",\"start\":30.94863611328125,\"end\":36.32015611328124,\"label\":\"未命名片段\",\"content\":\"无内容\",\"role\":\"真人\",\"text\":\"世界\"}]";
//        System.out.println("userAnswer:"+check_answer_voice(userAnswer1,correctAnswer1));
//        List<Segment> segments = parseSegments(userAnswer1);

//        for (Segment seg : segments) {
//            System.out.println(seg);
 //       }
        String correctAnswer="{\"width\":35,\"height\":100},\"canvasW\":861,\"canvasH\":646}]}";
        String userAnswer= "{\"width\":51,\"height\":137}}]}";
        String str1 = "{\"306\":[{\"attr\":[\"359\"],\"position\":{\"x\":456.19999980926514,\"y\":87.19999694824219},\"size\":{\"width\":177,\"height\":87}}]}";
        String str2 = "{\"321\":[{\"attr\":[\"341\"],\"position\":{\"x\":475.8000030517578,\"y\":183.1999969482422},\"size\":{\"width\":197,\"height\":183}}]}";
        //        String correctAnswer = "{\"258\":[{\"attr\":[\"\"],\"position\":{\"x\":42,\"y\":30},\"size\":{\"width\":33,\"height\":30}}]}";
 //       String userAnswer = "{\"258\":[{\"attr\":[\"\"],\"position\":{\"x\":42,\"y\":30},\"size\":{\"width\":33,\"height\":30}}]}";

        PanJuanParam panJuanParam = MarkQuestionUtils. check_answer_2D_xyq_ceshi(userAnswer,correctAnswer);
        //输出PanJuanParam各个值，名字换成注释
        System.out.println("isCorrect:"+panJuanParam.getIsCorrect());
        System.out.println("biao:"+panJuanParam.getBiao());
        System.out.println("cuo:"+panJuanParam.getCuo());
        System.out.println("wu:"+panJuanParam.getWu());
        System.out.println("shu:"+panJuanParam.getShu());
        System.out.println("zong:"+panJuanParam.getZong());
        System.out.println("da:"+panJuanParam.getDa());
        System.out.println("accuracyRate:"+panJuanParam.getAccuracyRate());
        System.out.println("coverageRate:"+panJuanParam.getCoverageRate());
        System.out.println("operationDuration:"+panJuanParam.getOperationDuration());
        System.out.println("isCorrect:"+panJuanParam.getIsCorrect());
    }
}
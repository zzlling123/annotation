package com.xinkao.erp.exercise.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinkao.erp.common.util.PointSubmitUtil;
import com.xinkao.erp.exam.entity.ExamPageUserAnswer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
            }else if (userAnswer.length() > correctAnswer.length()) {
                for (int i = 0; i < userAnswers.length; i++) {
                    if (userAnswers[i].equals(correctAnswers[i])) {
                        continue;
                    }else {
                        return 0;
                    }
                }
                return score/2;
            }else if (userAnswer.length() < correctAnswer.length()) {
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
        }else if (shape == 500) {
            if (moduleId==1){//1	图像标注
                return check_answer(userAnswer,correctAnswer)?score:0;
            }else if (moduleId==2){//2	3D点云标注
                ExamPageUserAnswer examPageUserAnswer = new ExamPageUserAnswer();
                examPageUserAnswer.setUserAnswer(userAnswer);
                examPageUserAnswer.setRightAnswer(correctAnswer);
                examPageUserAnswer.setShape(shape);
                examPageUserAnswer.setScore(score);
                return pointSubmitUtil.get3DPointScore(examPageUserAnswer);
            }else if (moduleId==3){//3	OCR标注
                return check_answer(userAnswer,correctAnswer)?score:0;
            }else if (moduleId==4){//4	语音标注
                return check_answer_voice(userAnswer,correctAnswer)?score:0;
            }else if (moduleId==5){//5	2D标注
                return check_answer_2D(userAnswer,correctAnswer)?score:0;
            }else if (moduleId==6){//6	人脸关键点标注
                return check_answer(userAnswer,correctAnswer)?score:0;
            }
        }
        return 0;
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

    }
}
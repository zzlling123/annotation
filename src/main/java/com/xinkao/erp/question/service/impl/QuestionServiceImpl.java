package com.xinkao.erp.question.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.manage.service.MarkService;
import com.xinkao.erp.question.entity.*;
import com.xinkao.erp.question.mapper.LabelMapper;
import com.xinkao.erp.question.mapper.QuestionMapper;
import com.xinkao.erp.question.param.QuestionChildParam;
import com.xinkao.erp.question.param.QuestionFormTitleParam;
import com.xinkao.erp.question.service.*;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.question.param.QuestionParam;
import com.xinkao.erp.question.query.QuestionQuery;
import com.xinkao.erp.question.vo.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends BaseServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private QuestionLabelService questionLabelService;
    @Autowired
    private QuestionMarkService questionMarkService;
    @Autowired
    private QuestionFormTitleService questionFormTitleService;
    @Autowired
    private QuestionChildService questionChildService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private MarkService markService;

    @Override
    public Page<QuestionPageVo> page(QuestionQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return questionMapper.page(page, query);
    }

    public Page<QuestionExercisePageVo> page1(QuestionQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return questionMapper.page1(page, query);
    }

    @Override
    public BaseResponse<?> save(QuestionParam param) {
        // 校验题目是否已存在
        if (lambdaQuery().eq(Question::getQuestion, param.getQuestion()).count() > 0) {
            return BaseResponse.fail("题目已存在！");
        }
        //验证主题数据格式
        BaseResponse<?> taskVerState = verificationTaskTeaSaveParam(param);
        if (!"ok".equals(taskVerState.getState())){
            return taskVerState;
        }
        Question question = BeanUtil.copyProperties(param, Question.class);
        question.setOptions(getRealOptions(param.getOptions()));
        question.setQuestionText(StripHT(question.getQuestion()));
        save(question);
        //保存标签关联关系
//        List<QuestionLabel> questionLabels = param.getLabels().stream().map(item -> {
//            QuestionLabel questionLabel = new QuestionLabel();
//            questionLabel.setQid(question.getId());
//            questionLabel.setLid(item);
//            return questionLabel;
//        }).collect(Collectors.toList());
//        questionLabelService.saveBatch(questionLabels);
        //保存标记关联关系
        if (param.getMarkIds() != null && "500".equals(param.getShape())){
            List<QuestionMark> questionMarkList = param.getMarkIds().stream().map(item -> {
                QuestionMark questionMark = new QuestionMark();
                questionMark.setQid(question.getId());
                questionMark.setMid(item);
                return questionMark;
            }).collect(Collectors.toList());
            questionMarkService.saveBatch(questionMarkList);
        }
        return BaseResponse.ok("新增成功！");
    }

    @Override
    public BaseResponse<?> saveQuestionFormTitle(QuestionFormTitleParam param) {
        QuestionFormTitle questionFormTitle = BeanUtil.copyProperties(param, QuestionFormTitle.class);
        return questionFormTitleService.save(questionFormTitle) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> updateQuestionFormTitle(QuestionFormTitleParam param) {
        QuestionFormTitle questionFormTitle = BeanUtil.copyProperties(param, QuestionFormTitle.class);
        return questionFormTitleService.updateById(questionFormTitle) ? BaseResponse.ok("修改成功！") : BaseResponse.fail("修改失败！");
    }

    @Override
    public BaseResponse<?> saveQuestionChild(QuestionChildParam param) {
        QuestionChild questionChild = BeanUtil.copyProperties(param, QuestionChild.class);
        return questionChildService.save(questionChild) ? BaseResponse.ok("新增成功！") : BaseResponse.fail("新增失败！");
    }

    @Override
    public BaseResponse<?> updateQuestionChild(QuestionChildParam param) {
        QuestionChild questionChild = BeanUtil.copyProperties(param, QuestionChild.class);
        return questionChildService.updateById(questionChild) ? BaseResponse.ok("修改成功！") : BaseResponse.fail("修改失败！");
    }

    @Override
    public BaseResponse<?> update(QuestionParam param) {
        //验证主题数据格式
        BaseResponse<?> taskVerState = verificationTaskTeaSaveParam(param);
        if (!"ok".equals(taskVerState.getState())){
            return taskVerState;
        }
        Question question = BeanUtil.copyProperties(param, Question.class);
        question.setOptions(getRealOptions(param.getOptions()));
        question.setQuestionText(StripHT(question.getQuestion()));
        //删除原有标签关联关系
//        questionLabelService.lambdaUpdate().eq(QuestionLabel::getQid, question.getId()).remove();
//        List<QuestionLabel> questionLabels = param.getLabels().stream().map(item -> {
//            QuestionLabel questionLabel = new QuestionLabel();
//            questionLabel.setQid(question.getId());
//            questionLabel.setLid(item);
//            return questionLabel;
//        }).collect(Collectors.toList());
//        questionLabelService.saveBatch(questionLabels);
        questionMarkService.lambdaUpdate().eq(QuestionMark::getQid, question.getId()).remove();
        //保存标记关联关系
        if (param.getMarkIds() != null && "500".equals(param.getShape())){
            List<QuestionMark> questionMarkList = param.getMarkIds().stream().map(item -> {
                QuestionMark questionMark = new QuestionMark();
                questionMark.setQid(question.getId());
                questionMark.setMid(item);
                return questionMark;
            }).collect(Collectors.toList());
            questionMarkService.saveBatch(questionMarkList);
        }
        return updateById(question) ? BaseResponse.ok("编辑成功！") : BaseResponse.fail("编辑失败！");
    }

    public BaseResponse<?> verificationTaskTeaSaveParam(QuestionParam param){
        List<String> optionList = param.getOptions();
        if (optionList.isEmpty()){
            if ("100".equals(param.getShape()) || "200".equals(param.getShape())){
                return BaseResponse.fail("选项不可为空！");
            }
        }
        if ("100".equals(param.getShape())){
            //如果正确答案不在选项中
            if (!ObjectUtil.contains(optionList,param.getAnswer())){
                return BaseResponse.fail("答案必须在选项中！");
            }
            //如果单选题选了多个答案则报错
            if (param.getAnswer().length() > 1){
                return BaseResponse.fail("单选题选项必须唯一！");
            }
        }
        if ("200".equals(param.getType())){
            //循环判断答案是否在选项中
            for (String s : param.getAnswer().split("")) {
                //如果正确答案不在选项中
                if (!ObjectUtil.contains(optionList,s)){
                    return BaseResponse.fail("答案必须在选项中！");
                }
            }
        }
        return BaseResponse.ok();
    }

    //从html中提取纯文本
    public String StripHT(String strHtml) {
        String txtcontent = strHtml.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
        txtcontent = txtcontent.replaceAll("<a>\\s*|\t|\r|\n</a>", "");//去除字符串中的空格,回车,换行符,制表符
        //去除字符串中的空格,回车,换行符,制表符
        txtcontent = txtcontent.replace(" ", "").replace("\t", "")
                .replace("&nbsp", "");
        return txtcontent;
    }

    public String getRealOptions(List<String> Options) {
        //处理选项格式为[A, B, C, D]字符串
        StringBuilder options = new StringBuilder("[");
        for(int i = 0; i < Options.size(); i++) {
            options.append(Options.get(i));
            //最后一个不拼接逗号
            if (i < Options.size() - 1) {
                options.append(",");
            }
        }
        options.append("]");
        return options.toString();
    }

    @Override
    public QuestionInfoVo getQuestionDetail(Integer id) {
        Question question = getById(id);
        QuestionInfoVo questionInfoVo = BeanUtil.copyProperties(question, QuestionInfoVo.class);
        //查询并赋值该题目关联的自定义标签
        List<LabelVo> labelList = labelMapper.getLabelListByQid(id);
        questionInfoVo.setLabelList(labelList);
        //如果为操作题则插入标注
        if ("500".equals(question.getShape().toString())){
            List<Mark> markList = markService.getListByQuestionId(id);
            questionInfoVo.setMarkList(markList);
        }
        return questionInfoVo;
    }

    @Override
    public BaseResponse<?> del(DeleteParam param) {
        return lambdaUpdate().in(Question::getId, param.getIds())
                .set(Question::getIsDel, CommonEnum.IS_DEL.YES.getCode())
                .update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }

    @Override
    public void selfSave() {
        // 定义题目分类和题型
        List<QuestionType> types = questionTypeService.lambdaQuery().list();
        List<Integer> shapes = Arrays.asList(100, 200, 300, 400, 500);
        List<Question> questions = new ArrayList<>();
        // 遍历每个分类和题型
        for (QuestionType type : types) {
            for (Integer shape : shapes) {
                for (int i = 1; i <= 100; i++) {
                    // 生成随机题目
                    Question question = new Question();
                    question.setQuestion("随机题目 " + i + " --- " + type.getTypeName() + ",题型为： --- " + shape);
                    question.setQuestionText("随机题目 " + i + " --- " + type.getTypeName() + ",题型为： --- " + shape);
                    question.setShape(shape);
                    question.setAnswerTip("这里是答案讲解");
                    question.setType(type.getId());
                    question.setDifficultyLevel(RandomUtil.randomInt(1, 4)); // 随机生成难易度 (1: 简单, 2: 中等, 3: 困难)
                    if (shape == 100 || shape == 200){
                        question.setOptions("[A,B,C,D]");
                        List<String> options = Arrays.asList("A", "B", "C", "D");
                        question.setAnswer(generateRandomAnswer(shape, options));
                    }else if (shape == 300){
                        question.setAnswer("填空1答案&%&填空2答案&%&填空3答案");
                    }else if (shape == 400){
                        question.setAnswer("主观题答案！！！！！！！！");
                    }
                    questions.add(question);
                }
            }
        }
        saveBatch(questions);
    }


    private String generateRandomAnswer(Integer shape, List<String> options) {
        if (shape == 100) {
            // 单选题随机选择一个选项
            return options.get(RandomUtil.randomInt(0, options.size()));
        } else if (shape == 200) {
            // 多选题随机选择多个选项
            int count = RandomUtil.randomInt(1, options.size() + 1);
            StringBuilder answer = new StringBuilder();
            for (int i = 0; i < count; i++) {
                answer.append(options.get(RandomUtil.randomInt(0, options.size())));
            }
            return answer.toString();
        }
        return "";
    }

    @Override
    public BaseResponse<List<QuestionFormVo>> getQuestionFormInfo(Integer questionId){
        List<QuestionFormTitle> questionFormTitleList = questionFormTitleService.lambdaQuery()
                .eq(QuestionFormTitle::getPid, questionId)
                .orderByAsc(QuestionFormTitle::getSort)
                .list();
        Map<Integer,List<QuestionChild>> questionChildList = questionChildService.lambdaQuery()
                .eq(QuestionChild::getQuestionId, questionId)
                .orderByAsc(QuestionChild::getSort)
                .list()
                .stream()
                .collect(Collectors.groupingBy(QuestionChild::getPid));
        List<QuestionFormVo> voList = new ArrayList<>();
        // 遍历题干插入
        for (QuestionFormTitle questionFormTitle : questionFormTitleList) {
            QuestionFormVo questionFormVo = new QuestionFormVo();
            questionFormVo.setId(questionFormTitle.getId());
            questionFormVo.setQuestion(questionFormTitle.getQuestion());
            questionFormVo.setSort(questionFormTitle.getSort());
            questionFormVo.setQuestionChildList(BeanUtil.copyToList(questionChildList.get(questionFormTitle.getId()), QuestionChildVo.class));
            voList.add(questionFormVo);
        }
        return BaseResponse.ok(voList);
    }
}
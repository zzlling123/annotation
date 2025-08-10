package com.xinkao.erp.question.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.enums.busi.*;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.manage.service.MarkService;
import com.xinkao.erp.question.entity.*;
import com.xinkao.erp.question.excel.QuestionFormZipImportModel;
import com.xinkao.erp.question.excel.QuestionImportModel;
import com.xinkao.erp.question.excel.QfHeadV2;
import com.xinkao.erp.question.excel.QfTitleV2;
import com.xinkao.erp.question.excel.QfTextAnsV2;
import com.xinkao.erp.question.excel.QfFileAnsV2;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${path.fileUrl}")
    private String fileUrlDir;
    @Value("${ipurl.url}")
    private String ipurlPrefix;

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
        // 校验题目是否已存在（同时检查题目内容和题目类型）
        if (lambdaQuery().eq(Question::getQuestion, param.getQuestion())
                         .eq(Question::getShape, param.getShape()).count() > 0) {
            return BaseResponse.fail("相同类型的题目已存在！");
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
        return BaseResponse.ok("新增成功！",question.getId());
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



    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionImportResultVO importQuestions(MultipartFile file) {
        QuestionImportResultVO result = new QuestionImportResultVO();
        List<String> errorMessages = new ArrayList<>();

        try {
            List<QuestionImportModel> importList = EasyExcel.read(file.getInputStream())
                    .head(QuestionImportModel.class)
                    .sheet()
                    .doReadSync();

            if (importList.isEmpty()) {
                result.setTotalCount(0);
                result.setSuccessCount(0);
                result.setFailCount(0);
                result.setErrorMessages(Arrays.asList("Excel文件为空或格式不正确"));
                return result;
            }

            result.setTotalCount(importList.size());
            int successCount = 0;
            List<QuestionImportModel> errorList = new ArrayList<>();

            for (int i = 0; i < importList.size(); i++) {
                QuestionImportModel model = importList.get(i);
                try {
                    // 数据验证
                    String validationError = validateQuestionData(model, i + 2);
                    if (StrUtil.isNotBlank(validationError)) {
                        errorMessages.add(validationError);
                        errorList.add(model);
                        continue;
                    }

                    // 使用与单个新增相同的逻辑保存题目
                    BaseResponse<?> saveResult = saveQuestionFromImport(model);
                    if (!"ok".equals(saveResult.getState())) {
                        errorMessages.add("第" + (i + 2) + "行：" + saveResult.getMsg());
                        errorList.add(model);
                    } else {
                        successCount++;
                    }

                } catch (Exception e) {
                    errorMessages.add("第" + (i + 2) + "行：" + e.getMessage());
                    errorList.add(model);
                }
            }

            result.setSuccessCount(successCount);
            result.setFailCount(importList.size() - successCount);
            result.setErrorMessages(errorMessages);

        } catch (Exception e) {
            result.setTotalCount(0);
            result.setSuccessCount(0);
            result.setFailCount(0);
            result.setErrorMessages(Arrays.asList("读取Excel文件失败：" + e.getMessage()));
        }

        return result;
    }

    /**
     * 使用与单个新增相同的逻辑保存题目
     */
    private BaseResponse<?> saveQuestionFromImport(QuestionImportModel model) {


        // 验证主题对应内容，并返回数据供保存
        Question question = VerifyerificationTopicRetrieveData(model);
        if(question==null){
            return BaseResponse.fail("内容不全");
        }
        // 校验题目是否已存在（同时检查题目内容和题目类型）
        if (lambdaQuery().eq(Question::getQuestion, question.getQuestion())
                         .eq(Question::getShape, question.getShape()).count() > 0) {
            return BaseResponse.fail("相同类型的题目已存在！");
        }
        // 保存题目
        save(question);

        return BaseResponse.ok("新增成功！");
    }

    /**
     * 验证主题对应内容，并返回数据供保存
     * @param model
     * @return
     */
    private Question VerifyerificationTopicRetrieveData(QuestionImportModel model) {
        //创建对象
        Question question = new Question();
        String shape = model.getShape();
        try {
            if (QuestionTypesEnum.DANXUAN.getName().equals(shape)) {
                question.setShape(QuestionTypesEnum.DANXUAN.getCode());
                question.setTitle(model.getTitle());
                // 选项：仅按%$%分隔，提取编号
                List<String> optionTokens = splitByDelim(model.getOptions());
                List<String> optionCodes = parseOptionCodesFromTokens(optionTokens);
                question.setOptions("[" + String.join(",", optionCodes) + "]");
                // 答案：必须一个编号
                List<String> ans = splitByDelim(model.getAnswer());
                question.setAnswer(ans.isEmpty() ? "" : ans.get(0));
                question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(model.getDifficultyLevel()));
                question.setQuestion(concatenateOptionsHtmlByTokens(optionTokens, model.getQuestion()));
                question.setQuestionText(concatenateOptionsPlainByTokens(optionTokens, model.getQuestion()));
            }
            if (QuestionTypesEnum.DUOXUAN.getName().equals(shape)) {
                question.setShape(QuestionTypesEnum.DUOXUAN.getCode());
                question.setTitle(model.getTitle());
                List<String> optionTokens = splitByDelim(model.getOptions());
                List<String> optionCodes = parseOptionCodesFromTokens(optionTokens);
                question.setOptions("[" + String.join(",", optionCodes) + "]");
                // 多选答案：按%$%，存为拼接串（如 ACD）
                List<String> ans = splitByDelim(model.getAnswer());
                question.setAnswer(StringUtils.collectionToDelimitedString(ans, ""));
                question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(model.getDifficultyLevel()));
                question.setQuestion(concatenateOptionsHtmlByTokens(optionTokens, model.getQuestion()));
                question.setQuestionText(concatenateOptionsPlainByTokens(optionTokens, model.getQuestion()));
            }
            if (QuestionTypesEnum.TIANKONG.getName().equals(shape)) {
                question.setShape(QuestionTypesEnum.TIANKONG.getCode());
                question.setTitle(model.getTitle());
                question.setQuestion("<p>" + model.getQuestion() + "</p>");
                question.setQuestionText(StripHT(model.getQuestion()));
                // 填空：仅按%$%分隔
                FillBlankAnswer fillBlankAnswer = parseFillBlankAnswerByDelim(model.getAnswer());
                question.setOptions(fillBlankAnswer.getOptionsFormat());
                question.setAnswer(fillBlankAnswer.getAnswerFormat());
                question.setAnswerCount(fillBlankAnswer.getAnswerCount());
                question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(model.getDifficultyLevel()));
            }
            if (QuestionTypesEnum.ZHUGUAN.getName().equals(shape)) {
                question.setShape(QuestionTypesEnum.ZHUGUAN.getCode());
                question.setTitle(model.getTitle());
                question.setQuestion("<p>" + model.getQuestion() + "</p>");
                question.setQuestionText(StripHT(model.getQuestion()));
                question.setAnswer(model.getAnswer());
                question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(model.getDifficultyLevel()));
                if (StrUtil.isNotBlank(model.getNeedCorrect())) {
                    question.setNeedCorrect("是".equals(model.getNeedCorrect()) ? 1 : 0);
                }
            }
            // 解析、范围、状态、预计用时、备注
            question.setAnswerTip(model.getAnswerTip());
            question.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(model.getSymbol())));
            question.setState(StatusEnum.getCodeByName(model.getState()));
            question.setEstimatedTime(1);
            question.setRemark(model.getRemark());
            if (StrUtil.isNotBlank(model.getType())) {
                question.setType(QuestionCategoryEnum.getCodeByName(model.getType()));
            }
        } catch (IllegalArgumentException e) {
            System.err.println("枚举转换失败: " + e.getMessage());
            return null;
        }
        return question;
    }

    // 使用token渲染HTML：每个token只取第一个"、"分隔编号与正文
    private String concatenateOptionsHtmlByTokens(List<String> tokens, String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>").append(prefix).append("</p>").append("<span id='tag'></span>");
        for (String tok : tokens) {
            String code = tok;
            String text = "";
            int idx = tok.indexOf('、');
            if (idx > 0) {
                code = tok.substring(0, idx);
                text = tok.substring(idx + 1);
            }
            sb.append("<p>").append(code).append(".").append(text).append("</p>");
        }
        return sb.toString();
    }
    private String concatenateOptionsPlainByTokens(List<String> tokens, String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (String tok : tokens) {
            String code = tok;
            String text = "";
            int idx = tok.indexOf('、');
            if (idx > 0) {
                code = tok.substring(0, idx);
                text = tok.substring(idx + 1);
            }
            sb.append(code).append(".").append(text);
        }
        return sb.toString();
    }

    // 仅按%$%解析填空答案
    private FillBlankAnswer parseFillBlankAnswerByDelim(String answerText) {
        if (StrUtil.isBlank(answerText)) {
            return new FillBlankAnswer("[]", "", 0);
        }
        List<String> list = splitByDelim(answerText);
        if (list.isEmpty()) return new FillBlankAnswer("[]", "", 0);
        String optionsFormat = "[" + String.join(",", list) + "]";
        String answerFormat = String.join("&%&", list);
        return new FillBlankAnswer(optionsFormat, answerFormat, list.size());
    }

    private String parseShape(String shapeStr) {
        if (StrUtil.isBlank(shapeStr)) return null;

        switch (shapeStr.trim()) {
            case "单选题": return "100";
            case "多选题": return "200";
            case "填空题": return "300";
            case "主观题": return "400";
            default: return null;
        }
    }


    private String validateQuestionData(QuestionImportModel model, int rowNum) {
        StringBuilder errors = new StringBuilder();
        // 验证题目类型
        if (StrUtil.isBlank(model.getShape())) {
            errors.append("题目类型不能为空；");
        } else if (parseShape(model.getShape()) == null) {
            errors.append("题目类型不正确，支持：单选题、多选题、填空题、主观题；");
        }

        // 验证题目分类
        if (StrUtil.isBlank(model.getType())) {
            errors.append("题目分类不能为空；");
        } else {
            // 验证分类是否在枚举中存在
            try {
                QuestionCategoryEnum.getCodeByName(model.getType());
            } catch (IllegalArgumentException e) {
                errors.append("题目分类不正确，请检查分类名称；");
            }
        }
        
        // 验证标题
        if (StrUtil.isBlank(model.getTitle())) {
            errors.append("题干标题不能为空；");
        }

        // 验证题干
        if (StrUtil.isBlank(model.getQuestion())) {
            errors.append("题干内容不能为空；");
        }

        // 验证难度
        if (StrUtil.isBlank(model.getDifficultyLevel())) {
            errors.append("题干难度不能为空；");
        } else {
            // 验证难度是否在枚举中存在
            try {
                QuestionDifficultyEnum.getCodeByName(model.getDifficultyLevel());
            } catch (IllegalArgumentException e) {
                errors.append("题干难度不正确，支持：一级、二级、三级、四级、五级；");
            }
        }
        
        // 验证实体所属范围
        if (StrUtil.isBlank(model.getSymbol())) {
            errors.append("所属范围内容不能为空；");
        } else {
            // 验证所属范围是否在枚举中存在
            try {
                EntitySystemEnum.getCodeByName(model.getSymbol());
            } catch (IllegalArgumentException e) {
                errors.append("所属范围不正确，支持：人社局、学校；");
            }
        }
        
        // 验证状态
        if (StrUtil.isBlank(model.getState())) {
            errors.append("状态内容不能为空；");
        } else {
            // 验证状态是否在枚举中存在
            try {
                StatusEnum.getCodeByName(model.getState());
            } catch (IllegalArgumentException e) {
                errors.append("状态不正确，支持：可用、不可用；");
            }
        }

        // 验证答案
        if (StrUtil.isBlank(model.getAnswer())) {
            errors.append("答案不能为空；");
        }

        // 仅按"%$%"分隔进行选项/答案校验
        List<String> optionTokens = splitByDelim(model.getOptions());
        List<String> optionCodes = parseOptionCodesFromTokens(optionTokens);

        if (QuestionTypesEnum.DANXUAN.getName().equals(model.getShape()) || QuestionTypesEnum.DUOXUAN.getName().equals(model.getShape())) {
            if (optionTokens.isEmpty()) {
                errors.append("单选题和多选题必须设置选项（使用%$%分隔）；");
            }
            List<String> answerTokens = splitByDelim(model.getAnswer());
            if (QuestionTypesEnum.DANXUAN.getName().equals(model.getShape())) {
                if (answerTokens.size() != 1) {
                    errors.append("单选题答案必须且仅能选择一个（使用%$%分隔，不要并列多个）；");
                } else if (!optionCodes.contains(answerTokens.get(0))) {
                    errors.append("单选题答案不在所提供的选项中；");
                }
            }
            if (QuestionTypesEnum.DUOXUAN.getName().equals(model.getShape())) {
                if (answerTokens.size() < 2) {
                    errors.append("多选题至少选择两个答案（使用%$%分隔）；");
                }
                if (!answerTokens.isEmpty()) {
                    boolean allValid = true;
                    for (String a : answerTokens) {
                        if (!optionCodes.contains(a)) { allValid = false; break; }
                    }
                    if (!allValid) {
                        errors.append("多选题存在不在选项中的答案；");
                    }
                }
            }
        }
        // 主观题校验
        if (QuestionTypesEnum.ZHUGUAN.getName().equals(model.getShape())) {
            if (StrUtil.isBlank(model.getNeedCorrect())) {
                errors.append("主观题必须设置是否批改；");
            } else if (!"是".equals(model.getNeedCorrect()) && !"否".equals(model.getNeedCorrect())) {
                errors.append("主观题批改设置只能为：是、否；");
            }
        }

        if (errors.length() > 0) {
            return "第" + rowNum + "行：" + errors.toString();
        }
        return null;
    }

    // 仅按"%$%"切分
    private List<String> splitByDelim(String text) {
        if (StrUtil.isBlank(text)) return Collections.emptyList();
        String[] arr = text.split("%\\$%");
        List<String> list = new ArrayList<>();
        for (String a : arr) {
            String t = a == null ? null : a.trim();
            if (StrUtil.isNotBlank(t)) list.add(t);
        }
        return list;
    }

    // 从每个token的开头抽取选项编号（A、B、A1…）
    private List<String> parseOptionCodesFromTokens(List<String> tokens) {
        List<String> codes = new ArrayList<>();
        Pattern p = Pattern.compile("^([A-Z][0-9]*)");
        for (String tok : tokens) {
            Matcher m = p.matcher(tok);
            if (m.find()) {
                String code = m.group(1);
                if (!codes.contains(code)) codes.add(code);
            }
        }
        return codes;
    }

    //拿取选项加值
    public static List<String> extractKeyValuePairs(String content) {
        List<String> result = new ArrayList<>();
        // 正则表达式，用于匹配格式为"字母+数字、任意内容"的模式
        String regex = "([A-Z][1-9]?)、([^，]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String key = matcher.group(1); // 获取选项（字母+数字）
            String value = matcher.group(2); // 获取内容
            result.add(key + "、" + value);
        }

        return result;
    }
    //在标题和选项上加<P>标签
    public static String concatenateOptionsHtml(String content, String prefix) {
        List<String> strings = extractKeyValuePairs(content);
        String result = "<p>" + prefix + "</p>"+"<span id='tag'></span>"; // 将前缀用<p>标签包裹

        for (String option : strings) {
            String key = option.split("、")[0];
            String value = option.split("、")[1];
            result += "<p>" + key + "." + value + "</p>";
        }

        return result;
    }
    //
    public static String concatenateOptionsPlain(String content, String prefix) {
        List<String> strings = extractKeyValuePairs(content);
        String result = prefix; // 直接添加前缀

        for (String option : strings) {
            String key = option.split("、")[0];
            String value = option.split("、")[1];
            result += key + "." + value + ""; // 拼接选项，选项之间用空格分隔
        }

        return result.trim(); // 去掉末尾多余的空格
    }

    //拿取所有选项
    public static List<String> extractOptions(String content) {
        return Collections.emptyList();
    }

    /**
     * 解析填空题答案

     * @return 解析结果对象
     */
    private static class FillBlankAnswer {
        private String optionsFormat;  // [填空答案1,填空答案2]
        private String answerFormat;   // 填空答案1&%&填空答案2
        private Integer answerCount;   // 答案数量
        
        public FillBlankAnswer(String optionsFormat, String answerFormat, Integer answerCount) {
            this.optionsFormat = optionsFormat;
            this.answerFormat = answerFormat;
            this.answerCount = answerCount;
        }
        
        public String getOptionsFormat() { return optionsFormat; }
        public String getAnswerFormat() { return answerFormat; }
        public Integer getAnswerCount() { return answerCount; }
    }
    
    /**
     * 解析填空题答案
     */
    private FillBlankAnswer parseFillBlankAnswer(String answerText) {
        return parseFillBlankAnswerByDelim(answerText);
    }


//    /**
//     * 题目单的批量导入
//     * @param file
//     * @return
//     */
//    @Override
//    public QuestionImportResultVO importQuestionFormZip(MultipartFile file) throws IOException {
//        QuestionImportResultVO result = new QuestionImportResultVO();
//        List<String> errors = new ArrayList<>();
//        Path tempDir = Files.createTempDirectory("qform_zip_");
//        try {
//            unzipTo(tempDir, file);
//            // 1) 定位 Excel（递归找第一个 .xlsx/.xls）
//            File excelFile = findFirstExcel(tempDir);
//            if (excelFile == null) {
//                throw new IllegalArgumentException("zip 内未找到 Excel 文件（.xlsx/.xls）");
//            }
//
//            // 2) 读取为模型行（首个工作表）
//            List<QuestionFormZipImportModel> rows = EasyExcel
//                    .read(excelFile)
//                    .head(QuestionFormZipImportModel.class)
//                    .sheet()           // 默认第一个 sheet
//                    .doReadSync();
//            System.out.println("excel里面的数据："+rows.get(0).getType()+" "+rows.get(0).getQuestionFileRelPath()+" "+rows.get(0).getSymbol()+"子标题："+rows.get(1).getFormTitleItemsRaw());
//            if (rows == null || rows.isEmpty()) {
//                throw new IllegalArgumentException("Excel 内容为空");
//            }
//            // excelFile 已找到；rows 已读取
//            ValidationResult vr = validateRows(rows, tempDir, excelFile.getParentFile());
//            result.setTotalCount(vr.total);
//            result.setSuccessCount(vr.success);
//            result.setFailCount(vr.total - vr.success);
//            result.setErrorMessages(vr.errors);
//            // 校验通过则进行分组（一个题目单 + 其子题 为一组）
//            if (vr.errors.isEmpty()) {
//                List<QFormGroup> groups = groupQuestionForms(rows);
//
//                // 构建大小写不敏感索引，便于根据相对路径找文件
//                Map<String, File> lowerIndex = buildLowercaseIndex(tempDir);
//                for (QFormGroup g : groups) {
//                    try {
//                        // 1) 先上传题干文件/素材，得到访问URL
//                        Head h = g.head;
//                        if (h != null) {
//                            String qFileUrl = null;
//                            String qMatUrl = null;
//                            if (StrUtil.isNotBlank(h.questionFileRelPath)) {
//                                File src = findFileByRelPath(normalizeRelPath(h.questionFileRelPath), excelFile.getParentFile(), tempDir, lowerIndex);
//                                if (src == null) {
//                                    result.getErrorMessages().add("第" + g.headRowNum + "行[题目单]：题干文件未找到：" + h.questionFileRelPath);
//                                    result.setFailCount(result.getFailCount() + 1);
//                                    continue;
//                                }
//                                qFileUrl = saveToFileUrlAndGetAccessUrl(src);
//                            }
//                            if (StrUtil.isNotBlank(h.questionMaterialRelPath)) {
//                                File src2 = findFileByRelPath(normalizeRelPath(h.questionMaterialRelPath), excelFile.getParentFile(), tempDir, lowerIndex);
//                                if (src2 == null) {
//                                    result.getErrorMessages().add("第" + g.headRowNum + "行[题目单]：题干素材未找到：" + h.questionMaterialRelPath);
//                                    result.setFailCount(result.getFailCount() + 1);
//                                    continue;
//                                }
//                                qMatUrl = saveToFileUrlAndGetAccessUrl(src2);
//                            }
//                            // 用上传后的访问URL替换原相对路径
//                            h.questionFileRelPath = qFileUrl;
//                            h.questionMaterialRelPath = qMatUrl;
//                        }
//
//                        // 2) 保存题目单
//                        Integer questionId = persistQuestionHead(h, result.getErrorMessages(), g.headRowNum);
//                        if (questionId == null) {
//                            result.setFailCount(result.getFailCount() + 1);
//                            continue;
//                        }
//                        System.out.println("保存题目单成功，questionId=" + questionId);
//                        // 保存该组的二级标题，并同时保存对应的子题答案
//                        persistFormTitlesAndChildren(questionId, g, excelFile.getParentFile(), tempDir, lowerIndex, result.getErrorMessages());
//                    } catch (Exception e) {
//                        result.getErrorMessages().add("第" + g.headRowNum + "行[题目单]：保存失败 - " + e.getMessage());
//                        result.setFailCount(result.getFailCount() + 1);
//                        // 不中断，继续下一组
//                    }
//                }
//            }
//            return result;
//
//        } finally {
//            deleteDirectoryQuietly(tempDir);
//        }
//    }

//    // 逐条保存子标题，并将同一行解析出的答案保存到 q_question_child
//    private void persistFormTitlesAndChildren(Integer questionId,
//                                              QFormGroup group,
//                                              File excelBaseDir,
//                                              Path zipRoot,
//                                              Map<String, File> lowerIndex,
//                                              List<String> errors) {
//        if (group == null || group.rows == null || group.rows.isEmpty()) return;
//        for (RowItems ri : group.rows) {
//            if (ri.titles == null || ri.titles.isEmpty()) continue;
//            for (TitleItem t : ri.titles) {
//                if (t == null || StrUtil.isBlank(t.title)) continue;
//                // 1) 保存子标题
//                QuestionFormTitle title = new QuestionFormTitle();
//                title.setPid(questionId);
//                title.setQuestion(t.title);
//                if (t.sort != null) title.setSort(t.sort);
//                boolean ok = questionFormTitleService.save(title);
//                if (!ok || title.getId() == null) {
//                    if (errors != null) errors.add("第" + group.headRowNum + "行[题目单]：二级标题保存失败 - " + t.title);
//                    continue; // 跳过该标题的子题
//                }
//                Integer titleId = title.getId();
//                System.out.println("子标题已保存：id=" + titleId + ", question=" + title.getQuestion() + ", sort=" + title.getSort());
//
//                // 2) 保存该行的文本答案到 q_question_child（非文件）
//                if (ri.textAnswers != null && !ri.textAnswers.isEmpty()) {
//                    for (TextAnswerItem ta : ri.textAnswers) {
//                        if (ta == null || StrUtil.isBlank(ta.label)) continue;
//                        QuestionChild child = new QuestionChild();
//                        child.setQuestionId(questionId);
//                        child.setPid(titleId);
//                        child.setQuestion(ta.label);
//                        child.setDefaultText(ta.tip);
//                        child.setIsFile(0);
//                        child.setAnswer(ta.answer);
//                        if (ta.sort != null) child.setSort(ta.sort);
//                        // 可选：状态/可用
//                        child.setState(1);
//                        boolean saved = questionChildService.save(child);
//                        if (!saved) {
//                            if (errors != null) errors.add("第" + ri.rowNum + "行[子题-文本答案]：保存失败 - " + ta.label);
//                        }
//                    }
//                }
//
//                // 3) 保存该行的文件答案：先上传再存URL
//                if (ri.fileAnswers != null && !ri.fileAnswers.isEmpty()) {
//                    for (FileAnswerItem fa : ri.fileAnswers) {
//                        if (fa == null || StrUtil.isBlank(fa.label) || StrUtil.isBlank(fa.fileRelPath)) continue;
//                        try {
//                            File src = findFileByRelPath(normalizeRelPath(fa.fileRelPath), excelBaseDir, zipRoot, lowerIndex);
//                            if (src == null) {
//                                if (errors != null) errors.add("第" + ri.rowNum + "行[子题-文件答案]：文件未找到 - " + fa.fileRelPath);
//                                continue;
//                            }
//                            String url = saveToFileUrlAndGetAccessUrl(src);
//                            QuestionChild child = new QuestionChild();
//                            child.setQuestionId(questionId);
//                            child.setPid(titleId);
//                            child.setQuestion(fa.label);
//                            child.setIsFile(1);
//                            child.setFileType(fa.fileType);
//                            child.setAnswer(url);
//                            if (fa.sort != null) child.setSort(fa.sort);
//                            child.setState(1);
//                            boolean saved = questionChildService.save(child);
//                            if (!saved) {
//                                if (errors != null) errors.add("第" + ri.rowNum + "行[子题-文件答案]：保存失败 - " + fa.label);
//                            }
//                        } catch (Exception ex) {
//                            if (errors != null) errors.add("第" + ri.rowNum + "行[子题-文件答案]：上传或保存失败 - " + ex.getMessage());
//                        }
//                    }
//                }
//            }
//        }
//    }

//    // 仅保存组内的二级标题（逐条保存，返回子标题ID；不保存答案）
//    private void persistFormTitlesOnly(Integer questionId, QFormGroup group, List<String> errors) {
//        if (group == null || group.rows == null || group.rows.isEmpty()) return;
//        for (RowItems ri : group.rows) {
//            if (ri.titles == null || ri.titles.isEmpty()) continue;
//            for (TitleItem t : ri.titles) {
//                if (t == null || StrUtil.isBlank(t.title)) continue;
//                QuestionFormTitle e = new QuestionFormTitle();
//                e.setPid(questionId);
//                e.setQuestion(t.title);
//                if (t.sort != null) e.setSort(t.sort);
//                boolean ok = questionFormTitleService.save(e);
//                if (ok && e.getId() != null) {
//                    System.out.println("子标题已保存：id=" + e.getId() + ", question=" + e.getQuestion() + ", sort=" + e.getSort());
//                } else {
//                    if (errors != null) errors.add("第" + group.headRowNum + "行[题目单]：二级标题保存失败 - " + t.title);
//                }
//            }
//        }
//    }

    // 防 Zip Slip 的相对路径规范化
    private String normalizeRelPath(String rel) {
        String s = rel.trim().replace("\\", "/");
        while (s.startsWith("/")) s = s.substring(1);
        return s;
    }

    // 解压 MultipartFile 到指定目录
    private void unzipTo(Path targetDir, MultipartFile zipFile) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                String rel = normalizeRelPath(entry.getName());
                Path outPath = targetDir.resolve(rel).normalize();
                // Zip Slip 防护
                if (!outPath.startsWith(targetDir)) {
                    throw new IOException("非法zip条目路径: " + entry.getName());
                }
                Files.createDirectories(outPath.getParent());
                try (OutputStream os = Files.newOutputStream(outPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = zis.read(buf)) > 0) {
                        os.write(buf, 0, len);
                    }
                }
            }
        }
    }

//    // 校验结果
//    private static class ValidationResult {
//        int total;
//        int success;
//        List<String> errors = new ArrayList<>();
//    }

//    // 入口：校验并生成逐行错误（含行号与位置标识）
//    private ValidationResult validateRows(List<QuestionFormZipImportModel> rows,
//                                          Path zipRoot,
//                                          File excelBaseDir) throws IOException {
//        ValidationResult vr = new ValidationResult();
//        Map<String, File> lowerIndex = buildLowercaseIndex(zipRoot);
//
//        boolean hasCurrentHead = false;
//        for (int i = 0; i < rows.size(); i++) {
//            QuestionFormZipImportModel r = rows.get(i);
//            int rowNum = i + 2; // 数据从第2行
//            if (r == null) continue;
//
//            boolean allBlank = StrUtil.isBlank(r.getType()) && StrUtil.isBlank(r.getTitle())
//                    && StrUtil.isBlank(r.getQuestionFileRelPath()) && StrUtil.isBlank(r.getQuestionMaterialRelPath())
//                    && StrUtil.isBlank(r.getDifficultyLevel()) && StrUtil.isBlank(r.getSymbol()) && StrUtil.isBlank(r.getState())
//                    && StrUtil.isBlank(r.getFormTitleItemsRaw()) && StrUtil.isBlank(r.getFormAnswerItemsRaw()) && StrUtil.isBlank(r.getFormFileAnswerItemsRaw());
//            if (allBlank) continue;
//
//            vr.total++;
//
//            boolean isHead = StrUtil.isNotBlank(r.getTitle());
//            List<String> errs = new ArrayList<>();
//            if (isHead) {
//                hasCurrentHead = true;
//
//                // 头部必填
//                if (StrUtil.isBlank(r.getType())) errs.add("题目分类不能为空；");
//                if (StrUtil.isBlank(r.getTitle())) errs.add("题目标题不能为空；");
//                if (StrUtil.isBlank(r.getDifficultyLevel())) errs.add("难度不能为空；");
//                if (StrUtil.isBlank(r.getSymbol())) errs.add("试题标签不能为空；");
//                if (StrUtil.isBlank(r.getState())) errs.add("状态不能为空；");
//
//                // 题干文件/素材（可选），若填则校验存在性
//                if (StrUtil.isNotBlank(r.getQuestionFileRelPath())) {
//                    String rel = normalizeRelPath(r.getQuestionFileRelPath());
//                    if (findFileByRelPath(rel, excelBaseDir, zipRoot, lowerIndex) == null)
//                        errs.add("题干文件未找到：" + rel + "；");
//                }
//                if (StrUtil.isNotBlank(r.getQuestionMaterialRelPath())) {
//                    String rel = normalizeRelPath(r.getQuestionMaterialRelPath());
//                    if (findFileByRelPath(rel, excelBaseDir, zipRoot, lowerIndex) == null)
//                        errs.add("题干素材未找到：" + rel + "；");
//                }
//
//                // 头部的"新增二级标题"改为可选；若填写则校验格式
//                if (StrUtil.isNotBlank(r.getFormTitleItemsRaw())) {
//                    List<String> groups = splitGroups(r.getFormTitleItemsRaw());
//                    for (int gi = 0; gi < groups.size(); gi++) {
//                        List<String> fs = splitFields(groups.get(gi));
//                        if (fs.size() < 2) { errs.add("题目单-二级标题 第" + (gi+1) + "项格式应为"标题, 排序"；"); continue; }
//                        if (StrUtil.isBlank(fs.get(0))) errs.add("题目单-二级标题 第" + (gi+1) + "项：标题不能为空；");
//                        if (!isInteger(fs.get(1))) errs.add("题目单-二级标题 第" + (gi+1) + "项：排序必须为整数；");
//                    }
//                }
//
//                // 若同行填写了答案列也校验
//                if (StrUtil.isNotBlank(r.getFormAnswerItemsRaw())) {
//                    List<String> groups = splitGroups(r.getFormAnswerItemsRaw());
//                    for (int gi = 0; gi < groups.size(); gi++) {
//                        List<String> fs = splitFields(groups.get(gi));
//                        if (fs.size() < 4) { errs.add("子题-文本答案 第" + (gi+1) + "项格式应为"标签, 提示, 答案, 排序"；"); continue; }
//                        if (StrUtil.isBlank(fs.get(0))) errs.add("子题-文本答案 第" + (gi+1) + "项：标签不能为空；");
//                        if (StrUtil.isBlank(fs.get(2))) errs.add("子题-文本答案 第" + (gi+1) + "项：答案不能为空；");
//                        if (!isInteger(fs.get(3))) errs.add("子题-文本答案 第" + (gi+1) + "项：排序必须为整数；");
//                    }
//                }
//                if (StrUtil.isNotBlank(r.getFormFileAnswerItemsRaw())) {
//                    List<String> groups = splitGroups(r.getFormFileAnswerItemsRaw());
//                    for (int gi = 0; gi < groups.size(); gi++) {
//                        List<String> fs = splitFields(groups.get(gi));
//                        if (fs.size() < 4) { errs.add("子题-文件答案 第" + (gi+1) + "项格式应为"标签, 类型, 路径, 排序"；"); continue; }
//                        String rel = normalizeRelPath(fs.get(2));
//                        if (StrUtil.isBlank(fs.get(0))) errs.add("子题-文件答案 第" + (gi+1) + "项：标签不能为空；");
//                        if (StrUtil.isBlank(fs.get(1))) errs.add("子题-文件答案 第" + (gi+1) + "项：类型不能为空；");
//                        if (!isInteger(fs.get(3))) errs.add("子题-文件答案 第" + (gi+1) + "项：排序必须为整数；");
//                        if (findFileByRelPath(rel, excelBaseDir, zipRoot, lowerIndex) == null)
//                            errs.add("子题-文件答案 第" + (gi+1) + "项文件未找到：" + rel + "；");
//                    }
//                }
//
//                if (!errs.isEmpty()) vr.errors.add("第" + rowNum + "行[题目单]：" + String.join("", errs));
//                else vr.success++;
//
//            } else {
//                // 子内容行
//                if (!hasCurrentHead) {
//                    vr.errors.add("第" + rowNum + "行：检测到子内容，但未发现上方题目单头部行");
//                    continue;
//                }
//                boolean hasAny = StrUtil.isNotBlank(r.getFormTitleItemsRaw())
//                        || StrUtil.isNotBlank(r.getFormAnswerItemsRaw())
//                        || StrUtil.isNotBlank(r.getFormFileAnswerItemsRaw());
//                if (!hasAny) {
//                    vr.errors.add("第" + rowNum + "行：子内容行为空，请填写"新增二级标题/答案"列");
//                    continue;
//                }
//
//                boolean hasRowError = false;
//                // 子行：有内容则"新增二级标题"必填
//                if (StrUtil.isBlank(r.getFormTitleItemsRaw())) {
//                    vr.errors.add("第" + rowNum + "行[题目单-二级标题]：新增二级标题不能为空；");
//                    hasRowError = true;
//                } else {
//                    List<String> groups = splitGroups(r.getFormTitleItemsRaw());
//                    for (int gi = 0; gi < groups.size(); gi++) {
//                        List<String> fs = splitFields(groups.get(gi));
//                        if (fs.size() < 2) { vr.errors.add("第" + rowNum + "行[题目单-二级标题 第" + (gi+1) + "项]：格式应为"标题, 排序"；"); hasRowError = true; continue; }
//                        if (StrUtil.isBlank(fs.get(0))) { vr.errors.add("第" + rowNum + "行[题目单-二级标题 第" + (gi+1) + "项]：标题不能为空；"); hasRowError = true; }
//                        if (!isInteger(fs.get(1))) { vr.errors.add("第" + rowNum + "行[题目单-二级标题 第" + (gi+1) + "项]：排序必须为整数；"); hasRowError = true; }
//                    }
//                }
//                if (StrUtil.isNotBlank(r.getFormAnswerItemsRaw())) {
//                    List<String> groups = splitGroups(r.getFormAnswerItemsRaw());
//                    for (int gi = 0; gi < groups.size(); gi++) {
//                        List<String> fs = splitFields(groups.get(gi));
//                        if (fs.size() < 4) { vr.errors.add("第" + rowNum + "行[子题-文本答案 第" + (gi+1) + "项]：格式应为"标签, 提示, 答案, 排序"；"); hasRowError = true; continue; }
//                        if (StrUtil.isBlank(fs.get(0))) { vr.errors.add("第" + rowNum + "行[子题-文本答案 第" + (gi+1) + "项]：标签不能为空；"); hasRowError = true; }
//                        if (StrUtil.isBlank(fs.get(2))) { vr.errors.add("第" + rowNum + "行[子题-文本答案 第" + (gi+1) + "项]：答案不能为空；"); hasRowError = true; }
//                        if (!isInteger(fs.get(3))) { vr.errors.add("第" + rowNum + "行[子题-文本答案 第" + (gi+1) + "项]：排序必须为整数；"); hasRowError = true; }
//                    }
//                }
//                if (StrUtil.isNotBlank(r.getFormFileAnswerItemsRaw())) {
//                    List<String> groups = splitGroups(r.getFormFileAnswerItemsRaw());
//                    for (int gi = 0; gi < groups.size(); gi++) {
//                        List<String> fs = splitFields(groups.get(gi));
//                        if (fs.size() < 4) { vr.errors.add("第" + rowNum + "行[子题-文件答案 第" + (gi+1) + "项]：格式应为"标签, 类型, 路径, 排序"；"); hasRowError = true; continue; }
//                        String rel = normalizeRelPath(fs.get(2));
//                        if (StrUtil.isBlank(fs.get(0))) { vr.errors.add("第" + rowNum + "行[子题-文件答案 第" + (gi+1) + "项]：标签不能为空；"); hasRowError = true; }
//                        if (StrUtil.isBlank(fs.get(1))) { vr.errors.add("第" + rowNum + "行[子题-文件答案 第" + (gi+1) + "项]：类型不能为空；"); hasRowError = true; }
//                        if (!isInteger(fs.get(3))) { vr.errors.add("第" + rowNum + "行[子题-文件答案 第" + (gi+1) + "项]：排序必须为整数；"); hasRowError = true; }
//                        System.out.println("子标题文件路径："+fs.get(2));
//                        if (findFileByRelPath(rel, excelBaseDir, zipRoot, lowerIndex) == null) {
//                            vr.errors.add("第" + rowNum + "行[子题-文件答案 第" + (gi+1) + "项]：文件未找到：" + rel + "；");
//                            hasRowError = true;
//                        }
//                    }
//                }
//                if (!hasRowError) vr.success++;
//            }
//        }
//        return vr;
//    }

// ========= 工具 =========

//    // 多组用"、"分隔
//    private List<String> splitGroups(String raw) {
//        if (StrUtil.isBlank(raw)) return Collections.emptyList();
//        String[] arr = raw.split("、");
//        List<String> list = new ArrayList<>();
//        for (String a : arr) {
//            String t = a.trim();
//            if (!t.isEmpty()) {
//                list.add(t);
//            }
//        }
//        return list;
//    }

//    // 组内字段用"，"或","分隔
//    private List<String> splitFields(String group) {
//        String[] arr = group.split("，|,");
//        List<String> list = new ArrayList<>();
//        for (String a : arr) list.add(a.trim());
//        return list;
//    }

//    private boolean isInteger(String s) {
//        if (StrUtil.isBlank(s)) return false;
//        try { Integer.parseInt(s.trim()); return true; } catch (Exception e) { return false; }
//    }



//    // ================= 分组：一个题目单(头部行) + 其后连续子内容行 =================
//    private static class QFormGroup {
//        int headRowNum;
//        Head head;
//        List<RowItems> rows = new ArrayList<>();
//    }
//    private static class Head {
//        String type, title, questionFileRelPath, questionMaterialRelPath, difficultyLevel, symbol, state;
//    }
//    private static class RowItems {
//        int rowNum;
//        List<TitleItem> titles = new ArrayList<>();
//        List<TextAnswerItem> textAnswers = new ArrayList<>();
//        List<FileAnswerItem> fileAnswers = new ArrayList<>();
//    }
//    private static class TitleItem { String title; Integer sort; }
//    private static class TextAnswerItem { String label, tip, answer; Integer sort; }
//    private static class FileAnswerItem { String label, fileType, fileRelPath; Integer sort; }

//    private List<QFormGroup> groupQuestionForms(List<QuestionFormZipImportModel> rows) {
//        List<QFormGroup> groups = new ArrayList<>();
//        QFormGroup current = null;
//        for (int i = 0; i < rows.size(); i++) {
//            QuestionFormZipImportModel r = rows.get(i);
//            if (r == null) continue;
//            boolean allBlank = StrUtil.isBlank(r.getType()) && StrUtil.isBlank(r.getTitle())
//                    && StrUtil.isBlank(r.getQuestionFileRelPath()) && StrUtil.isBlank(r.getQuestionMaterialRelPath())
//                    && StrUtil.isBlank(r.getDifficultyLevel()) && StrUtil.isBlank(r.getSymbol()) && StrUtil.isBlank(r.getState())
//                    && StrUtil.isBlank(r.getFormTitleItemsRaw()) && StrUtil.isBlank(r.getFormAnswerItemsRaw()) && StrUtil.isBlank(r.getFormFileAnswerItemsRaw());
//            if (allBlank) continue;
//
//            boolean isHead = StrUtil.isNotBlank(r.getTitle());
//            int rowNum = i + 2;
//            if (isHead) {
//                if (current != null) groups.add(current);
//                current = new QFormGroup();
//                current.headRowNum = rowNum;
//                Head h = new Head();
//                h.type = r.getType();
//                h.title = r.getTitle();
//                h.questionFileRelPath = normalizeRelPath(StrUtil.nullToEmpty(r.getQuestionFileRelPath()));
//                h.questionMaterialRelPath = normalizeRelPath(StrUtil.nullToEmpty(r.getQuestionMaterialRelPath()));
//                h.difficultyLevel = r.getDifficultyLevel();
//                h.symbol = r.getSymbol();
//                h.state = r.getState();
//                current.head = h;
//
//                RowItems first = new RowItems();
//                first.rowNum = rowNum;
//                first.titles = parseTitles(r.getFormTitleItemsRaw());
//                first.textAnswers = parseTextAnswers(r.getFormAnswerItemsRaw());
//                first.fileAnswers = parseFileAnswers(r.getFormFileAnswerItemsRaw());
//                if (!first.titles.isEmpty() || !first.textAnswers.isEmpty() || !first.fileAnswers.isEmpty()) {
//                    current.rows.add(first);
//                }
//            } else {
//                if (current == null) continue; // 无头部行的子行，忽略（校验阶段已有错误提示）
//                RowItems ri = new RowItems();
//                ri.rowNum = rowNum;
//                ri.titles = parseTitles(r.getFormTitleItemsRaw());
//                ri.textAnswers = parseTextAnswers(r.getFormAnswerItemsRaw());
//                ri.fileAnswers = parseFileAnswers(r.getFormFileAnswerItemsRaw());
//                current.rows.add(ri);
//            }
//        }
//        if (current != null) groups.add(current);
//        return groups;
//    }

//    // 简单解析器：仅拆分，不做校验
//    private List<TitleItem> parseTitles(String raw) {
//        List<TitleItem> list = new ArrayList<>();
//        if (StrUtil.isBlank(raw)) return list;
//        for (String g : splitGroups(raw)) {
//            List<String> fs = splitFields(g);
//            if (fs.size() < 2) continue;
//            TitleItem t = new TitleItem();
//            t.title = fs.get(0);
//            try { t.sort = Integer.valueOf(fs.get(1)); } catch (Exception ignore) {}
//            list.add(t);
//        }
//        return list;
//    }
//    private List<TextAnswerItem> parseTextAnswers(String raw) {
//        List<TextAnswerItem> list = new ArrayList<>();
//        if (StrUtil.isBlank(raw)) return list;
//        for (String g : splitGroups(raw)) {
//            List<String> fs = splitFields(g);
//            if (fs.size() < 4) continue;
//            TextAnswerItem t = new TextAnswerItem();
//            t.label = fs.get(0);
//            t.tip = fs.get(1);
//            t.answer = fs.get(2);
//            try { t.sort = Integer.valueOf(fs.get(3)); } catch (Exception ignore) {}
//            list.add(t);
//        }
//        return list;
//    }
//    private List<FileAnswerItem> parseFileAnswers(String raw) {
//        List<FileAnswerItem> list = new ArrayList<>();
//        if (StrUtil.isBlank(raw)) return list;
//        for (String g : splitGroups(raw)) {
//            List<String> fs = splitFields(g);
//            if (fs.size() < 4) continue;
//            FileAnswerItem t = new FileAnswerItem();
//            t.label = fs.get(0);
//            t.fileType = fs.get(1);
//            t.fileRelPath = normalizeRelPath(fs.get(2));
//            try { t.sort = Integer.valueOf(fs.get(3)); } catch (Exception ignore) {}
//            list.add(t);
//        }
//        return list;
//    }

//    // ================= 仅保存题目单头部(q_question)并返回ID =================
//    // 说明：不处理任何子项/文件；在后续步骤调用此方法拿到 questionId
//    private Integer persistQuestionHead(Head h, List<String> errors, int rowNum) {
//        if (h == null) { errors.add("第"+rowNum+"行[题目单]：head 为空"); return null; }
//        Question q = new Question();
//        q.setShape(QuestionTypesEnum.TIMUDAN.getCode());
//        // 标题
//        q.setTitle(h.title);
//        // 题干内容与纯文本：优先使用题干文件/素材相对路径
//        if (StrUtil.isNotBlank(h.questionFileRelPath)) {
//            String content = h.questionFileRelPath;
//            if (StrUtil.isNotBlank(h.questionMaterialRelPath)) {
//                content = content + "," + h.questionMaterialRelPath;
//            }
//            q.setQuestion(content);
//            q.setQuestionText(content);
//        } else if (StrUtil.isNotBlank(h.questionMaterialRelPath)) {
//            q.setQuestion(h.questionMaterialRelPath);
//            q.setQuestionText(h.questionMaterialRelPath);
//        }
//        // 分类/难度/范围/状态
//        try { if (StrUtil.isNotBlank(h.type)) q.setType(QuestionCategoryEnum.getCodeByName(h.type)); } catch (Exception ignore) {}
//        try { if (StrUtil.isNotBlank(h.difficultyLevel)) q.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(h.difficultyLevel)); } catch (Exception ignore) {}
//        try { if (StrUtil.isNotBlank(h.symbol)) q.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(h.symbol))); } catch (Exception ignore) {}
//        try { if (StrUtil.isNotBlank(h.state)) q.setState(StatusEnum.getCodeByName(h.state)); } catch (Exception ignore) {}
//
//        // 作答时间
//         q.setEstimatedTime(1);
//        //是题目单
//        q.setIsForm(1);
//        //答案
//        q.setAnswer("试题单");
//        //选项
//        q.setOptions("[A,B]");
//        boolean ok = this.save(q);
//        if (!ok || q.getId() == null) {
//            errors.add("第"+rowNum+"行[题目单]：保存失败");
//            return null;
//        }
//        return q.getId();
//    }
 


    @Override
    public QuestionImportResultVO importQuestionFormZipV2(MultipartFile file) throws IOException {
        QuestionImportResultVO result = new QuestionImportResultVO();
        Path tempDir = Files.createTempDirectory("qform_zip_v2_");
        try {
            unzipTo(tempDir, file);
            File excelFile = findFirstExcel(tempDir);
            if (excelFile == null) {
                throw new IllegalArgumentException("zip 内未找到 Excel 文件（.xlsx/.xls）");
            }
            // 读取4个Sheet
            List<QfHeadV2> heads = EasyExcel.read(excelFile).head(QfHeadV2.class).sheet("题目单").doReadSync();
            List<QfTitleV2> titles = EasyExcel.read(excelFile).head(QfTitleV2.class).sheet("二级标题").doReadSync();
            List<QfTextAnsV2> textAnswers = EasyExcel.read(excelFile).head(QfTextAnsV2.class).sheet("文字答案").doReadSync();
            List<QfFileAnsV2> fileAnswers = EasyExcel.read(excelFile).head(QfFileAnsV2.class).sheet("文件答案").doReadSync();
            // 校验
            V2Validation vr = validateV2(heads, titles, textAnswers, fileAnswers, tempDir, excelFile.getParentFile());
            result.setTotalCount(vr.totalGroups);
            result.setSuccessCount(vr.successGroups);
            result.setFailCount(vr.totalGroups - vr.successGroups);
            result.setErrorMessages(vr.errors);
            if (!vr.errors.isEmpty()) {
                return result;
            }
            // 分组
            Map<String, List<QfTitleV2>> g2Titles = titles.stream().collect(Collectors.groupingBy(QfTitleV2::getGroupCode));
            Map<String, List<QfTextAnsV2>> g2Texts = textAnswers.stream().collect(Collectors.groupingBy(QfTextAnsV2::getGroupCode));
            Map<String, List<QfFileAnsV2>> g2Files = fileAnswers.stream().collect(Collectors.groupingBy(QfFileAnsV2::getGroupCode));
            Map<String, File> lowerIndex = buildLowercaseIndex(tempDir);

            int success = 0;
            for (QfHeadV2 h : heads) {
                try {
                    // 上传题干文件/素材
                    String qFileUrl = null, qMatUrl = null;
                    if (StrUtil.isNotBlank(h.getQuestionFileRelPath())) {
                        File src = findFileByRelPath(normalizeRelPath(h.getQuestionFileRelPath()), excelFile.getParentFile(), tempDir, lowerIndex);
                        if (src == null) {
                            result.getErrorMessages().add("[" + h.getGroupCode() + "] 题干文件未找到：" + h.getQuestionFileRelPath());
                            continue;
                        }
                        qFileUrl = saveToFileUrlAndGetAccessUrl(src);
                    }
                    if (StrUtil.isNotBlank(h.getQuestionMaterialRelPath())) {
                        File src2 = findFileByRelPath(normalizeRelPath(h.getQuestionMaterialRelPath()), excelFile.getParentFile(), tempDir, lowerIndex);
                        if (src2 == null) {
                            result.getErrorMessages().add("[" + h.getGroupCode() + "] 题干素材未找到：" + h.getQuestionMaterialRelPath());
                            continue;
                        }
                        qMatUrl = saveToFileUrlAndGetAccessUrl(src2);
                    }
                    // 保存题目头
                    Integer questionId = persistQuestionHeadV2(h, qFileUrl, qMatUrl);
                    if (questionId == null) {
                        result.getErrorMessages().add("[" + h.getGroupCode() + "] 题目单保存失败");
                    continue;
                }
                    // 保存二级标题
                    Map<Integer, Integer> titleNo2Id = new HashMap<>();
                    List<QfTitleV2> tList = g2Titles.getOrDefault(h.getGroupCode(), Collections.emptyList());
                    for (QfTitleV2 t : tList) {
                        QuestionFormTitle e = new QuestionFormTitle();
                        e.setPid(questionId);
                        e.setQuestion(t.getTitle());
                        if (t.getSort() != null) e.setSort(t.getSort());
                        boolean ok = questionFormTitleService.save(e);
                        if (!ok || e.getId() == null) {
                            result.getErrorMessages().add("[" + h.getGroupCode() + "] 二级标题保存失败：" + t.getTitle());
                    continue;
                }
                        titleNo2Id.put(t.getTitleNo(), e.getId());
                    }
                    // 保存文字答案
                    List<QfTextAnsV2> taList = g2Texts.getOrDefault(h.getGroupCode(), Collections.emptyList());
                    for (QfTextAnsV2 ta : taList) {
                        Integer pid = titleNo2Id.get(ta.getTitleNo());
                        if (pid == null) {
                            result.getErrorMessages().add("[" + h.getGroupCode() + "] 找不到文字答案对应的二级标题 title_no=" + ta.getTitleNo());
                            continue;
                        }
                        QuestionChild child = new QuestionChild();
                        child.setQuestionId(questionId);
                        child.setPid(pid);
                        child.setQuestion(ta.getLabel());
                        child.setDefaultText(ta.getTip());
                        child.setIsFile(0);
                        child.setAnswer(ta.getAnswer());
                        if (ta.getSort() != null) child.setSort(ta.getSort());
                        child.setState(1);
                        boolean saved = questionChildService.save(child);
                        if (!saved) {
                            result.getErrorMessages().add("[" + h.getGroupCode() + "] 文字答案保存失败：" + ta.getLabel());
                        }
                    }
                    // 保存文件答案
                    List<QfFileAnsV2> faList = g2Files.getOrDefault(h.getGroupCode(), Collections.emptyList());
                    for (QfFileAnsV2 fa : faList) {
                        Integer pid = titleNo2Id.get(fa.getTitleNo());
                        if (pid == null) {
                            result.getErrorMessages().add("[" + h.getGroupCode() + "] 找不到文件答案对应的二级标题 title_no=" + fa.getTitleNo());
                            continue;
                        }
                        File src = findFileByRelPath(normalizeRelPath(fa.getFileRelPath()), excelFile.getParentFile(), tempDir, lowerIndex);
                        if (src == null) {
                            result.getErrorMessages().add("[" + h.getGroupCode() + "] 文件答案未找到：" + fa.getFileRelPath());
                            continue;
                        }
                        String url = saveToFileUrlAndGetAccessUrl(src);
                        QuestionChild child = new QuestionChild();
                        child.setQuestionId(questionId);
                        child.setPid(pid);
                        child.setQuestion(fa.getLabel());
                        child.setIsFile(1);
                        child.setFileType(fa.getFileType());
                        child.setAnswer(url);
                        if (fa.getSort() != null) child.setSort(fa.getSort());
                        child.setState(1);
                        boolean saved = questionChildService.save(child);
                        if (!saved) {
                            result.getErrorMessages().add("[" + h.getGroupCode() + "] 文件答案保存失败：" + fa.getLabel());
                        }
                    }
                    success++;
                } catch (Exception ex) {
                    result.getErrorMessages().add("[" + h.getGroupCode() + "] 保存失败：" + ex.getMessage());
                }
            }
            result.setSuccessCount(success);
            result.setFailCount(result.getTotalCount() - success);
            return result;
        } finally {
            deleteDirectoryQuietly(tempDir);
        }
    }

    private static class V2Validation {
        int totalGroups;
        int successGroups;
        List<String> errors = new ArrayList<>();
    }

    private V2Validation validateV2(List<QfHeadV2> heads,
                                    List<QfTitleV2> titles,
                                    List<QfTextAnsV2> textAnswers,
                                    List<QfFileAnsV2> fileAnswers,
                                    Path zipRoot,
                                    File excelBaseDir) throws IOException {
        V2Validation vr = new V2Validation();
        if (heads == null) heads = Collections.emptyList();
        if (titles == null) titles = Collections.emptyList();
        if (textAnswers == null) textAnswers = Collections.emptyList();
        if (fileAnswers == null) fileAnswers = Collections.emptyList();

        Map<String, QfHeadV2> groupHead = new LinkedHashMap<>();
        int row = 2;
        for (QfHeadV2 h : heads) {
            if (h == null) { row++; continue; }
            List<String> errs = new ArrayList<>();
            if (StrUtil.isBlank(h.getGroupCode())) errs.add("Sheet[题目单] 第"+row+"行：组代码不能为空；");
            if (StrUtil.isBlank(h.getType())) errs.add("Sheet[题目单] 第"+row+"行：题目分类不能为空；");
            if (StrUtil.isBlank(h.getTitle())) errs.add("Sheet[题目单] 第"+row+"行：题目标题不能为空；");
            if (StrUtil.isBlank(h.getDifficultyLevel())) errs.add("Sheet[题目单] 第"+row+"行：难度不能为空；");
            if (StrUtil.isBlank(h.getSymbol())) errs.add("Sheet[题目单] 第"+row+"行：试题标签不能为空；");
            if (StrUtil.isBlank(h.getState())) errs.add("Sheet[题目单] 第"+row+"行：状态不能为空；");
            if (groupHead.containsKey(h.getGroupCode())) errs.add("Sheet[题目单] 第"+row+"行：组代码重复；");
            if (!errs.isEmpty()) vr.errors.addAll(errs); else groupHead.put(h.getGroupCode(), h);
            row++;
        }
        vr.totalGroups = groupHead.size();

        Map<String, Map<Integer, QfTitleV2>> g2TitleNo = new HashMap<>();
        row = 2;
        for (QfTitleV2 t : titles) {
            if (t == null) { row++; continue; }
            List<String> errs = new ArrayList<>();
            if (StrUtil.isBlank(t.getGroupCode())) errs.add("Sheet[二级标题] 第"+row+"行：组代码不能为空；");
            if (t.getTitleNo() == null) errs.add("Sheet[二级标题] 第"+row+"行：标题编号不能为空；");
            if (StrUtil.isBlank(t.getTitle())) errs.add("Sheet[二级标题] 第"+row+"行：标题不能为空；");
            if (t.getSort() == null) errs.add("Sheet[二级标题] 第"+row+"行：排序不能为空；");
            if (!groupHead.containsKey(t.getGroupCode())) errs.add("Sheet[二级标题] 第"+row+"行：找不到对应题目单 groupCode；");
            Map<Integer, QfTitleV2> idx = g2TitleNo.computeIfAbsent(t.getGroupCode(), k -> new HashMap<>());
            if (t.getTitleNo() != null && idx.containsKey(t.getTitleNo())) errs.add("Sheet[二级标题] 第"+row+"行：同组标题编号重复；");
            if (!errs.isEmpty()) vr.errors.addAll(errs); else idx.put(t.getTitleNo(), t);
            row++;
        }

        row = 2;
        for (QfTextAnsV2 a : textAnswers) {
            if (a == null) { row++; continue; }
            List<String> errs = new ArrayList<>();
            if (StrUtil.isBlank(a.getGroupCode())) errs.add("Sheet[文字答案] 第"+row+"行：组代码不能为空；");
            if (a.getTitleNo() == null) errs.add("Sheet[文字答案] 第"+row+"行：标题编号不能为空；");
            if (StrUtil.isBlank(a.getLabel())) errs.add("Sheet[文字答案] 第"+row+"行：标签不能为空；");
            if (StrUtil.isBlank(a.getAnswer())) errs.add("Sheet[文字答案] 第"+row+"行：答案不能为空；");
            if (a.getSort() == null) errs.add("Sheet[文字答案] 第"+row+"行：排序不能为空；");
            if (!groupHead.containsKey(a.getGroupCode())) errs.add("Sheet[文字答案] 第"+row+"行：找不到对应题目单 groupCode；");
            Map<Integer, QfTitleV2> idx = g2TitleNo.getOrDefault(a.getGroupCode(), Collections.emptyMap());
            if (a.getTitleNo() != null && !idx.containsKey(a.getTitleNo())) errs.add("Sheet[文字答案] 第"+row+"行：找不到对应二级标题 title_no；");
            if (!errs.isEmpty()) vr.errors.addAll(errs);
            row++;
        }

        row = 2;
        for (QfFileAnsV2 f : fileAnswers) {
            if (f == null) { row++; continue; }
            List<String> errs = new ArrayList<>();
            if (StrUtil.isBlank(f.getGroupCode())) errs.add("Sheet[文件答案] 第"+row+"行：组代码不能为空；");
            if (f.getTitleNo() == null) errs.add("Sheet[文件答案] 第"+row+"行：标题编号不能为空；");
            if (StrUtil.isBlank(f.getLabel())) errs.add("Sheet[文件答案] 第"+row+"行：标签不能为空；");
            if (StrUtil.isBlank(f.getFileType())) errs.add("Sheet[文件答案] 第"+row+"行：类型不能为空；");
            if (StrUtil.isBlank(f.getFileRelPath())) errs.add("Sheet[文件答案] 第"+row+"行：文件相对路径不能为空；");
            if (f.getSort() == null) errs.add("Sheet[文件答案] 第"+row+"行：排序不能为空；");
            if (!groupHead.containsKey(f.getGroupCode())) errs.add("Sheet[文件答案] 第"+row+"行：找不到对应题目单 groupCode；");
            Map<Integer, QfTitleV2> idx = g2TitleNo.getOrDefault(f.getGroupCode(), Collections.emptyMap());
            if (f.getTitleNo() != null && !idx.containsKey(f.getTitleNo())) errs.add("Sheet[文件答案] 第"+row+"行：找不到对应二级标题 title_no；");
            // 路径存在性（zip内）
            if (StrUtil.isNotBlank(f.getFileRelPath())) {
                String rel = normalizeRelPath(f.getFileRelPath());
                if (findFileByRelPath(rel, excelBaseDir, zipRoot, buildLowercaseIndex(zipRoot)) == null) {
                    // 为避免性能影响，可在持久化时统一查找并上传，这里仅做基本提示可选
                }
            }
            if (!errs.isEmpty()) vr.errors.addAll(errs);
            row++;
        }

        // 若无错误，以有 head 的组为成功校验
        if (vr.errors.isEmpty()) vr.successGroups = groupHead.size();
        return vr;
    }

    private Integer persistQuestionHeadV2(QfHeadV2 h, String qFileUrl, String qMatUrl) {
        Question q = new Question();
        q.setShape(QuestionTypesEnum.TIMUDAN.getCode());
        q.setTitle(h.getTitle());
        if (StrUtil.isNotBlank(qFileUrl) || StrUtil.isNotBlank(qMatUrl)) {
            String content = StrUtil.nullToEmpty(qFileUrl);
            if (StrUtil.isNotBlank(qMatUrl)) content = StrUtil.isBlank(content) ? qMatUrl : content + "," + qMatUrl;
            q.setQuestion(content);
            q.setQuestionText(content);
        }
        try { if (StrUtil.isNotBlank(h.getType())) q.setType(QuestionCategoryEnum.getCodeByName(h.getType())); } catch (Exception ignore) {}
        try { if (StrUtil.isNotBlank(h.getDifficultyLevel())) q.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(h.getDifficultyLevel())); } catch (Exception ignore) {}
        try { if (StrUtil.isNotBlank(h.getSymbol())) q.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(h.getSymbol()))); } catch (Exception ignore) {}
        try { if (StrUtil.isNotBlank(h.getState())) q.setState(StatusEnum.getCodeByName(h.getState())); } catch (Exception ignore) {}
         q.setEstimatedTime(1);
        q.setIsForm(1);
        q.setAnswer("试题单");
        q.setOptions("[A,B]");
        boolean ok = this.save(q);
        return ok ? q.getId() : null;
    }
 
    // 相对 Excel 目录 → zip 根目录 → 不区分大小写索引 依次查找
    private File findFileByRelPath(String normalizedRel, File excelBaseDir, Path zipRoot, Map<String, File> lowerIndex) {
        File f = new File(excelBaseDir, normalizedRel);
        if (f.exists() && f.isFile()) return f;
        f = new File(zipRoot.toFile(), normalizedRel);
        if (f.exists() && f.isFile()) return f;
        return lowerIndex.getOrDefault(normalizedRel.toLowerCase(), null);
    }

    // 将源文件复制到配置的 fileUrlDir 下，并返回可访问URL（与通用上传保持一致：UUID + 原文件名）
    private String saveToFileUrlAndGetAccessUrl(File src) throws IOException {
        if (src == null || !src.exists() || !src.isFile()) {
            throw new IOException("源文件不存在或不可读");
        }
        // 确保目录存在
        Path targetDir = Paths.get(fileUrlDir);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        String originalName = src.getName();
        String newName = UUID.randomUUID().toString().replace("-", "") + originalName;
        Path targetPath = targetDir.resolve(newName);
        Files.copy(src.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        // 返回外网访问URL前缀 + /annotation/fileUrl/ + 文件名（参考现有通用上传接口）
        return ipurlPrefix + "/annotation/fileUrl/" + newName;
    }

    // 递归安静删除目录
    private void deleteDirectoryQuietly(Path dir) throws IOException {
        if (dir == null) return;
        if (!Files.exists(dir)) return;
        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (IOException ignore) {}
                });
    }

    // 建立不区分大小写的路径索引
    private Map<String, File> buildLowercaseIndex(Path root) throws IOException {
        Map<String, File> map = new HashMap<>();
        try (java.util.stream.Stream<Path> s = Files.walk(root)) {
            s.filter(Files::isRegularFile).forEach(p -> {
                String rel = root.relativize(p).toString().replace("\\", "/");
                while (rel.startsWith("/")) rel = rel.substring(1);
                map.put(rel.toLowerCase(), p.toFile());
            });
        }
        return map;
    }

    //递归查找excel
    private File findFirstExcel(Path root) throws IOException {
        try (java.util.stream.Stream<Path> s = Files.walk(root)) {
            return s.filter(p -> !Files.isDirectory(p))
                    .filter(p -> {
                        String n = p.getFileName().toString().toLowerCase();
                        return n.endsWith(".xlsx") || n.endsWith(".xls");
                    })
                    .sorted()
                    .map(Path::toFile)
                    .findFirst()
                    .orElse(null);
        }
    }

}
package com.xinkao.erp.question.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.enums.busi.*;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.manage.entity.Mark;
import com.xinkao.erp.manage.service.MarkService;
import com.xinkao.erp.question.entity.*;
import com.xinkao.erp.question.excel.QuestionImportModel;
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
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
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
        //获取数据解析出对应的题目类型，并根据不同类型解析数据返回数据
        //创建对象
        Question question = new Question();
        //拿出题目类型准判断
        String shape = model.getShape();
        
        try {
            //单选题
            if(QuestionTypesEnum.DANXUAN.getName().equals(shape)){
                question.setShape(QuestionTypesEnum.DANXUAN.getCode());

                question.setTitle(model.getTitle());
                //选项处理
                List<String> strings = extractOptions(model.getOptions());
                String options = "[" + String.join(",", strings) + "]";
                question.setOptions(options);
                //答案处理：使用extractOptions将"A，B"转换为"AB"
                List<String> answerOptions = extractOptions(model.getAnswer());
                question.setAnswer(StringUtils.collectionToDelimitedString(answerOptions, ""));

                question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(model.getDifficultyLevel()));
                //进行拼接题目选项存储Question字段
                question.setQuestion(concatenateOptionsHtml(model.getOptions(),model.getQuestion()));
                //存入questionTest
                question.setQuestionText(concatenateOptionsPlain(model.getOptions(),model.getQuestion()));

            }
            //多选题
            if(QuestionTypesEnum.DUOXUAN.getName().equals(shape)){
                question.setShape(QuestionTypesEnum.DUOXUAN.getCode());
                question.setTitle(model.getTitle());
                //选项处理
                List<String> strings = extractOptions(model.getOptions());
                String options = "[" + String.join(",", strings) + "]";
                question.setOptions(options);
                //答案处理：使用extractOptions将"A，B，C"转换为"ABC"
                List<String> answerOptions = extractOptions(model.getAnswer());
                question.setAnswer(StringUtils.collectionToDelimitedString(answerOptions, ""));
                
                question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(model.getDifficultyLevel()));
                //进行拼接题目选项存储Question字段
                question.setQuestion(concatenateOptionsHtml(model.getOptions(),model.getQuestion()));
                //存入questionTest
                question.setQuestionText(concatenateOptionsPlain(model.getOptions(),model.getQuestion()));
                
            }
            //填空题
            if(QuestionTypesEnum.TIANKONG.getName().equals(shape)){
                question.setShape(QuestionTypesEnum.TIANKONG.getCode());
                question.setTitle(model.getTitle());
                // 为填空题题目内容添加<p>标签格式化
                question.setQuestion("<p>" + model.getQuestion() + "</p>");
                question.setQuestionText(StripHT(model.getQuestion()));
                
                // 解析填空题答案
                FillBlankAnswer fillBlankAnswer = parseFillBlankAnswer(model.getAnswer());
                question.setOptions(fillBlankAnswer.getOptionsFormat());     // [填空答案1,填空答案2]
                question.setAnswer(fillBlankAnswer.getAnswerFormat());       // 填空答案1&%&填空答案2
                question.setAnswerCount(fillBlankAnswer.getAnswerCount());   // 答案数量
                
                question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(model.getDifficultyLevel()));
                
            }
            //主观题
            if(QuestionTypesEnum.ZHUGUAN.getName().equals(shape)){
                question.setShape(QuestionTypesEnum.ZHUGUAN.getCode());
                question.setTitle(model.getTitle());
                // 为主观题题目内容添加<p>标签格式化
                question.setQuestion("<p>" + model.getQuestion() + "</p>");
                question.setQuestionText(StripHT(model.getQuestion()));
                question.setAnswer(model.getAnswer());
                question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(model.getDifficultyLevel()));
                // 主观题需要设置是否需要批改
                if(StrUtil.isNotBlank(model.getNeedCorrect())) {
                    question.setNeedCorrect("是".equals(model.getNeedCorrect()) ? 1 : 0);
                }
                
            }

            //存入解析
            question.setAnswerTip(model.getAnswerTip());
            //所属范围
            question.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(model.getSymbol())));
            //状态
            question.setState(StatusEnum.getCodeByName(model.getState()));
            //预计用时
            question.setEstimatedTime(1);
            //备注
            question.setRemark(model.getRemark());


            // 设置题目分类 - 这个之前缺失了
            if(StrUtil.isNotBlank(model.getType())) {
                // 假设type字段存储的是分类名称，需要根据名称获取对应的ID
                // 这里可能需要根据实际的分类枚举来处理
                question.setType(QuestionCategoryEnum.getCodeByName(model.getType()));
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("枚举转换失败: " + e.getMessage());
            return null; // 返回null表示数据有问题
        }

        return question;
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

        // 根据题型验证选项
        //获取选项列表
        List<String> strings = extractOptions(model.getOptions());

        if (QuestionTypesEnum.DANXUAN.getName().equals(model.getShape()) || QuestionTypesEnum.DUOXUAN.getName().equals(model.getShape())) {

            if (StrUtil.isBlank(model.getOptions())) {
                errors.append("单选题和多选题必须设置选项；");
            }
            //判断单选题必须是一个选项并且选项是在所提供的选项当中
            if(QuestionTypesEnum.DANXUAN.getName().equals(model.getShape())){
                //单选题选项只能为一个 - 修复空指针异常
                if(StrUtil.isNotBlank(model.getAnswer()) && model.getAnswer().length() > 1){
                    errors.append("单选题选项必须唯一！");
                }

                //确保答案选项在提供的选项当中
                if (StrUtil.isNotBlank(model.getAnswer()) && !strings.contains(model.getAnswer())) {
                    // 如果选项不在列表中，进行记录错误
                    errors.append("单选题选项不在所提供的选项中！");
                }
            }
            //判断多选题选项并且选项是在所提供的选项当中
            if(QuestionTypesEnum.DUOXUAN.getName().equals(model.getShape())){
                //多选题选项必须有多个 - 修复空指针异常
                if(StrUtil.isBlank(model.getAnswer()) || model.getAnswer().length() <= 1){
                    errors.append("多选题选项须有多个");
                }

                // 确保答案选项在提供的选项当中
                if(StrUtil.isNotBlank(model.getAnswer())) {
                    List<String> strings1 = extractOptions(model.getAnswer());
                    boolean allOptionsValid = true;

                    for (String option : strings1) {
                        if (!strings.contains(option)) {
                            allOptionsValid = false;
                            break; // 如果有一个选项不在列表中，就标记为无效并退出循环
                        }
                    }

                    if (!allOptionsValid) {
                        // 如果选项不在列表中，进行记录错误
                        errors.append("多选题选项不在所提供的选项中！");
                    }
                }
            }

        }
        // 根据题型验证是否想需要批改
        if (QuestionTypesEnum.ZHUGUAN.getName().equals(model.getShape())) {
            // 修复空指针异常
            if (StrUtil.isBlank(model.getNeedCorrect())) {
                errors.append("主观题必须设置是否批改；");
            } else if (!"是".equals(model.getNeedCorrect()) && !"否".equals(model.getNeedCorrect())) {
                errors.append("主观题批改设置只能为：是、否；");
            }
        }

        if (errors.length() > 0) {
            System.out.println("来到这里并且给出错误："+errors.toString());
            return "第" + rowNum + "行：" + errors.toString();
        }

        return null;
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
        List<String> options = new ArrayList<>();
        
        // 如果内容为空，返回空列表
        if (StrUtil.isBlank(content)) {
            return options;
        }
        
        // 智能识别格式：如果包含"、"则按选项格式处理，否则按答案格式处理
        if (content.contains("、")) {
            // 选项格式：A、你好A，B、你好B  -> 提取 A, B
            String regex = "([A-Z][0-9]*)、";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String option = matcher.group(1); // 获取选项部分（不包括"、"）
                if (!options.contains(option)) { // 避免重复添加
                    options.add(option);
                }
            }
        } else {
            // 答案格式：AB 或 A -> 提取每个字母作为单独选项
            String regex = "([A-Z][0-9]*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String option = matcher.group(1);
                if (!options.contains(option)) { // 避免重复添加
                    options.add(option);
                }
            }
        }

        return options;
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
        if (StrUtil.isBlank(answerText)) {
            return new FillBlankAnswer("[]", "", 0);
        }
        
        // 按中文逗号分割答案
        String[] answers = answerText.split("，");
        List<String> answerList = new ArrayList<>();
        
        // 去除空白并添加到列表
        for (String answer : answers) {
            String trimmedAnswer = answer.trim();
            if (StrUtil.isNotBlank(trimmedAnswer)) {
                answerList.add(trimmedAnswer);
            }
        }
        
        if (answerList.isEmpty()) {
            return new FillBlankAnswer("[]", "", 0);
        }
        
        // 生成options格式：[填空答案1,填空答案2]
        String optionsFormat = "[" + String.join(",", answerList) + "]";
        
        // 生成answer格式：填空答案1&%&填空答案2
        String answerFormat = String.join("&%&", answerList);
        
        // 答案数量
        Integer answerCount = answerList.size();
        
        return new FillBlankAnswer(optionsFormat, answerFormat, answerCount);
    }

}
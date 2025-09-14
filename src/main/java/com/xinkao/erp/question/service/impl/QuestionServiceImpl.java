package com.xinkao.erp.question.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

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

@Slf4j
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
    @Autowired
    private KnowledgePointCacheManager knowledgePointCacheManager;

    @Value("${path.fileUrl}")
    private String fileUrlDir;
    @Value("${ipurl.url}")
    private String ipurlPrefix;

    // 当前导入会话的知识点缓存
    private Map<Integer, List<DifficultyPoint>> currentImportKnowledgeCache;
    
    // 当前导入会话的错误收集器
    private List<QuestionImportResultVO.RowError> currentImportErrors;

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
    public BaseResponse<?> delTitle(DeleteParam param) {
        for (String id : param.getIds()) {
            questionFormTitleService.lambdaUpdate().eq(QuestionFormTitle::getId, id)
                    .set(QuestionFormTitle::getIsDel, CommonEnum.IS_DEL.YES.getCode())
                    .update();
            //同步删除child题目列表
            questionChildService.lambdaUpdate().eq(QuestionChild::getPid, id)
                    .set(QuestionChild::getIsDel, CommonEnum.IS_DEL.YES.getCode())
                    .update();
        }
        return BaseResponse.ok("删除成功！");
    }

    @Override
    public BaseResponse<?> delChild(DeleteParam param) {
        return questionChildService.lambdaUpdate().in(QuestionChild::getId, param.getIds())
                .set(QuestionChild::getIsDel, CommonEnum.IS_DEL.YES.getCode())
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
                .eq(QuestionFormTitle::getIsDel, CommonEnum.IS_DEL.NO.getCode())
                .orderByAsc(QuestionFormTitle::getSort)
                .list();
        Map<Integer,List<QuestionChild>> questionChildList = questionChildService.lambdaQuery()
                .eq(QuestionChild::getQuestionId, questionId)
                .eq(QuestionChild::getIsDel, CommonEnum.IS_DEL.NO.getCode())
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
    public QuestionImportResultVO importQuestions(MultipartFile file) {
        try {
            // 🔥 关键优化：导入开始时构建知识点缓存
            currentImportKnowledgeCache = knowledgePointCacheManager.buildKnowledgePointCache();
            currentImportErrors = new ArrayList<>();
            
            byte[] bytes = file.getBytes();
            ReadResult data = readExcelForImport(bytes);

            QuestionImportResultVO res = validateReadResult(data);
            // 按题型分别记录错误行，避免不同Sheet相同行号相互误伤
            java.util.Set<Integer> invalidSingleRows = new java.util.HashSet<>();
            java.util.Set<Integer> invalidMultipleRows = new java.util.HashSet<>();
            java.util.Set<Integer> invalidJudgeRows = new java.util.HashSet<>();
            if (res.getRowErrors() != null) {
                for (QuestionImportResultVO.RowError re : res.getRowErrors()) {
                    if (re == null || re.getRowNum() == null || re.getMessage() == null) continue;
                    String msg = re.getMessage();
                    if (msg.startsWith("[单选题]")) invalidSingleRows.add(re.getRowNum());
                    else if (msg.startsWith("[多选题]")) invalidMultipleRows.add(re.getRowNum());
                    else if (msg.startsWith("[判断题]")) invalidJudgeRows.add(re.getRowNum());
                }
            }
            // 仅对各自校验通过的数据调用对应的存库接口
            for (SingleChoiceRec r : data.singles) {
                if (!invalidSingleRows.contains(r.excelRow)) {
                    try {
                        saveSingleChoiceRecordWithKnowledgePoint(r);
                    } catch (Exception ex) {
                        QuestionImportResultVO.RowError re = new QuestionImportResultVO.RowError();
                        re.setRowNum(r.excelRow);
                        re.setMessage("[单选题] 第" + r.showIndex + "条：入库失败 - " + ex.getMessage());
                        res.getRowErrors().add(re);
                        res.setFailCount(res.getFailCount() + 1);
                        res.setSuccessCount(Math.max(0, res.getSuccessCount() - 1));
                    }
                }
            }
            for (MultipleChoiceRec r : data.multiples) {
                if (!invalidMultipleRows.contains(r.excelRow)) {
                    try {
                        saveMultipleChoiceRecordWithKnowledgePoint(r);
                    } catch (Exception ex) {
                        QuestionImportResultVO.RowError re = new QuestionImportResultVO.RowError();
                        re.setRowNum(r.excelRow);
                        re.setMessage("[多选题] 第" + r.showIndex + "条：入库失败 - " + ex.getMessage());
                        res.getRowErrors().add(re);
                        res.setFailCount(res.getFailCount() + 1);
                        res.setSuccessCount(Math.max(0, res.getSuccessCount() - 1));
                    }
                }
            }
            for (TrueFalseRec r : data.judges) {
                if (!invalidJudgeRows.contains(r.excelRow)) {
                    try {
                        saveTrueFalseRecordWithKnowledgePoint(r);
                    } catch (Exception ex) {
                        QuestionImportResultVO.RowError re = new QuestionImportResultVO.RowError();
                        re.setRowNum(r.excelRow);
                        re.setMessage("[判断题] 第" + r.showIndex + "条：入库失败 - " + ex.getMessage());
                        res.getRowErrors().add(re);
                        res.setFailCount(res.getFailCount() + 1);
                        res.setSuccessCount(Math.max(0, res.getSuccessCount() - 1));
                    }
                }
            }
            
            // 合并知识点匹配产生的错误信息
            if (!currentImportErrors.isEmpty()) {
                if (res.getRowErrors() == null) {
                    res.setRowErrors(new ArrayList<>());
                }
                res.getRowErrors().addAll(currentImportErrors);
            }
            
            return res;
        } catch (Exception e) {
            QuestionImportResultVO result = new QuestionImportResultVO();
            result.setTotalCount(0);
            result.setSuccessCount(0);
            result.setFailCount(0);
            result.setErrorMessages(java.util.Collections.singletonList("读取Excel文件失败：" + e.getMessage()));
            QuestionImportResultVO.RowError re = new QuestionImportResultVO.RowError();
            re.setRowNum(null);
            re.setMessage("读取Excel文件失败：" + e.getMessage());
            result.setRowErrors(java.util.Collections.singletonList(re));
            return result;
        } finally {
            // 清理缓存，避免内存泄漏
            currentImportKnowledgeCache = null;
            currentImportErrors = null;
        }
    }

    // ===== 读取Excel，抽取为内存结构 =====
    private ReadResult readExcelForImport(byte[] bytes) throws Exception {
        ReadResult data = new ReadResult();
        try (org.apache.poi.ss.usermodel.Workbook wb = org.apache.poi.ss.usermodel.WorkbookFactory.create(new java.io.ByteArrayInputStream(bytes))) {
            // 单选
            org.apache.poi.ss.usermodel.Sheet shSingle = findSheet(wb, new String[]{"单选", "单选题"}, 0);
            if (shSingle != null && shSingle.getRow(0) != null) {
                java.util.Map<String, Integer> idx = headerIndex(shSingle.getRow(0));
                int last = shSingle.getLastRowNum();
                int show = 0;
                for (int r = 1; r <= last; r++) {
                    org.apache.poi.ss.usermodel.Row row = shSingle.getRow(r);
                    if (row == null) continue;
                    SingleChoiceRec rec = new SingleChoiceRec();
                    rec.excelRow = r + 1;
                    com.xinkao.erp.question.excel.SingleChoiceSheetModel m = new com.xinkao.erp.question.excel.SingleChoiceSheetModel();
                    m.setType(getCell(row, idx.get("题目分类")));
                    m.setDifficultyLevel(getCell(row, idx.get("题目难度")));
                    m.setSymbol(getCell(row, idx.get("所属范围")));
                    m.setQuestion(getCell(row, idx.get("试题")));
                    m.setOptionA(getCell(row, idx.get("选项A")));
                    m.setOptionB(getCell(row, idx.get("选项B")));
                    m.setOptionC(getCell(row, idx.get("选项C")));
                    m.setOptionD(getCell(row, idx.get("选项D")));
                    m.setAnswer(getCell(row, idx.get("答案")));
                    m.setKnowledgePointName(getCell(row, idx.get("知识点名称")));
                    if (StrUtil.isAllBlank(m.getType(), m.getDifficultyLevel(), m.getSymbol(), m.getQuestion(), m.getOptionA(), m.getOptionB(), m.getOptionC(), m.getOptionD(), m.getAnswer())) continue;
                    rec.row = m;
                    rec.showIndex = ++show;
                    data.singles.add(rec);
                    
                    // 🔥 打印单选题Excel数据
                    log.info("📊 [单选题] Excel第{}行数据：题目分类=[{}], 难度=[{}], 所属范围=[{}], 知识点=[{}], 试题=[{}], 选项A=[{}], 选项B=[{}], 选项C=[{}], 选项D=[{}], 答案=[{}]", 
                        rec.excelRow, m.getType(), m.getDifficultyLevel(), m.getSymbol(), m.getKnowledgePointName(),
                        m.getQuestion() != null ? (m.getQuestion().length() > 30 ? m.getQuestion().substring(0, 30) + "..." : m.getQuestion()) : "无",
                        m.getOptionA(), m.getOptionB(), m.getOptionC(), m.getOptionD(), m.getAnswer());
                }
            }
            // 多选
            org.apache.poi.ss.usermodel.Sheet shMulti = findSheet(wb, new String[]{"多选", "多选题"}, 1);
            if (shMulti != null && shMulti.getRow(0) != null) {
                java.util.Map<String, Integer> idx = headerIndex(shMulti.getRow(0));
                int last = shMulti.getLastRowNum();
                int show = 0;
                for (int r = 1; r <= last; r++) {
                    org.apache.poi.ss.usermodel.Row row = shMulti.getRow(r);
                    if (row == null) continue;
                    MultipleChoiceRec rec = new MultipleChoiceRec();
                    rec.excelRow = r + 1;
                    com.xinkao.erp.question.excel.MultipleChoiceSheetModel m = new com.xinkao.erp.question.excel.MultipleChoiceSheetModel();
                    m.setType(getCell(row, idx.get("题目分类")));
                    m.setDifficultyLevel(getCell(row, idx.get("题目难度")));
                    m.setSymbol(getCell(row, idx.get("所属范围")));
                    m.setQuestion(getCell(row, idx.get("试题")));
                    m.setOptionA(getCell(row, idx.get("选项A")));
                    m.setOptionB(getCell(row, idx.get("选项B")));
                    m.setOptionC(getCell(row, idx.get("选项C")));
                    m.setOptionD(getCell(row, idx.get("选项D")));
                    m.setOptionE(getCell(row, idx.get("选项E")));
                    m.setAnswer(getCell(row, idx.get("答案")));
                    m.setKnowledgePointName(getCell(row, idx.get("知识点名称")));
                    if (StrUtil.isAllBlank(m.getType(), m.getDifficultyLevel(), m.getSymbol(), m.getQuestion(), m.getOptionA(), m.getOptionB(), m.getOptionC(), m.getOptionD(), m.getOptionE(), m.getAnswer())) continue;
                    rec.row = m;
                    rec.showIndex = ++show;
                    data.multiples.add(rec);
                    
                    // 🔥 打印多选题Excel数据
                    log.info("📊 [多选题] Excel第{}行数据：题目分类=[{}], 难度=[{}], 所属范围=[{}], 知识点=[{}], 试题=[{}], 选项A=[{}], 选项B=[{}], 选项C=[{}], 选项D=[{}], 选项E=[{}], 答案=[{}]", 
                        rec.excelRow, m.getType(), m.getDifficultyLevel(), m.getSymbol(), m.getKnowledgePointName(),
                        m.getQuestion() != null ? (m.getQuestion().length() > 30 ? m.getQuestion().substring(0, 30) + "..." : m.getQuestion()) : "无",
                        m.getOptionA(), m.getOptionB(), m.getOptionC(), m.getOptionD(), m.getOptionE(), m.getAnswer());
                }
            }
            // 判断
            org.apache.poi.ss.usermodel.Sheet shJudge = findSheet(wb, new String[]{"判断", "判断题"}, 2);
            if (shJudge != null && shJudge.getRow(0) != null) {
                java.util.Map<String, Integer> idx = headerIndex(shJudge.getRow(0));
                int last = shJudge.getLastRowNum();
                int show = 0;
                for (int r = 1; r <= last; r++) {
                    org.apache.poi.ss.usermodel.Row row = shJudge.getRow(r);
                    if (row == null) continue;
                    TrueFalseRec rec = new TrueFalseRec();
                    rec.excelRow = r + 1;
                    com.xinkao.erp.question.excel.TrueFalseSheetModel m = new com.xinkao.erp.question.excel.TrueFalseSheetModel();
                    m.setType(getCell(row, idx.get("题目分类")));
                    m.setDifficultyLevel(getCell(row, idx.get("题目难度")));
                    m.setSymbol(getCell(row, idx.get("所属范围")));
                    m.setQuestion(getCell(row, idx.get("试题")));
                    m.setAnswer(getCell(row, idx.get("对/错")));
                    m.setKnowledgePointName(getCell(row, idx.get("知识点名称")));
                    if (StrUtil.isAllBlank(m.getType(), m.getDifficultyLevel(), m.getSymbol(), m.getQuestion(), m.getAnswer())) continue;
                    rec.row = m;
                    rec.showIndex = ++show;
                    data.judges.add(rec);
                    
                    // 🔥 打印判断题Excel数据
                    log.info("📊 [判断题] Excel第{}行数据：题目分类=[{}], 难度=[{}], 所属范围=[{}], 知识点=[{}], 试题=[{}], 答案=[{}]", 
                        rec.excelRow, m.getType(), m.getDifficultyLevel(), m.getSymbol(), m.getKnowledgePointName(),
                        m.getQuestion() != null ? (m.getQuestion().length() > 30 ? m.getQuestion().substring(0, 30) + "..." : m.getQuestion()) : "无",
                        m.getAnswer());
                }
            }
        }
        return data;
    }

    // ===== 校验读取结果，产出导入结果 =====
    private QuestionImportResultVO validateReadResult(ReadResult data) {
        QuestionImportResultVO result = new QuestionImportResultVO();
        List<QuestionImportResultVO.RowError> rowErrors = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        // 单选
        for (SingleChoiceRec r : data.singles) {
            List<String> errs = new ArrayList<>();
            if (StrUtil.isBlank(r.getType())) errs.add("题目分类不能为空"); else { try { QuestionCategoryEnum.getCodeByName(r.getType()); } catch (Exception e) { errs.add("题目分类不正确"); } }
            if (StrUtil.isBlank(r.getDiff())) errs.add("题目难度不能为空"); else { try { QuestionDifficultyEnum.getCodeByName(r.getDiff()); } catch (Exception e) { errs.add("题目难度不正确，仅限：一级、二级、三级、四级、五级"); } }
            if (StrUtil.isBlank(r.getSym())) errs.add("所属范围不能为空"); else { try { EntitySystemEnum.getCodeByName(r.getSym()); } catch (Exception e) { errs.add("所属范围不正确"); } }
            if (StrUtil.isBlank(r.getQ())) errs.add("试题不能为空");
            int nonBlank = 0; if (StrUtil.isNotBlank(r.getA())) nonBlank++; if (StrUtil.isNotBlank(r.getB())) nonBlank++; if (StrUtil.isNotBlank(r.getC())) nonBlank++; if (StrUtil.isNotBlank(r.getD())) nonBlank++;
            if (nonBlank < 1) errs.add("选项至少一个不能为空");
            String up = StrUtil.trimToEmpty(r.getAns()).toUpperCase();
            if (StrUtil.isBlank(up)) errs.add("答案不能为空");
            else if (!("A".equals(up) || "B".equals(up) || "C".equals(up) || "D".equals(up))) errs.add("答案必须为A/B/C/D");
            else {
                boolean ok = ("A".equals(up) && StrUtil.isNotBlank(r.getA())) || ("B".equals(up) && StrUtil.isNotBlank(r.getB())) || ("C".equals(up) && StrUtil.isNotBlank(r.getC())) || ("D".equals(up) && StrUtil.isNotBlank(r.getD()));
                if (!ok) errs.add("答案对应的选项内容不能为空");
            }
            if (!errs.isEmpty()) { QuestionImportResultVO.RowError re = new QuestionImportResultVO.RowError(); re.setRowNum(r.excelRow); re.setMessage("[单选题] 第" + r.showIndex + "条：" + String.join("；", errs)); rowErrors.add(re); }
        }
        // 多选
        for (MultipleChoiceRec r : data.multiples) {
            List<String> errs = new ArrayList<>();
            if (StrUtil.isBlank(r.getType())) errs.add("题目分类不能为空"); else { try { QuestionCategoryEnum.getCodeByName(r.getType()); } catch (Exception e) { errs.add("题目分类不正确"); } }
            if (StrUtil.isBlank(r.getDiff())) errs.add("题目难度不能为空"); else { try { QuestionDifficultyEnum.getCodeByName(r.getDiff()); } catch (Exception e) { errs.add("题目难度不正确，仅限：一级、二级、三级、四级、五级"); } }
            if (StrUtil.isBlank(r.getSym())) errs.add("所属范围不能为空"); else { try { EntitySystemEnum.getCodeByName(r.getSym()); } catch (Exception e) { errs.add("所属范围不正确"); } }
            if (StrUtil.isBlank(r.getQ())) errs.add("试题不能为空");
            int nonBlank = 0; if (StrUtil.isNotBlank(r.getA())) nonBlank++; if (StrUtil.isNotBlank(r.getB())) nonBlank++; if (StrUtil.isNotBlank(r.getC())) nonBlank++; if (StrUtil.isNotBlank(r.getD())) nonBlank++; if (StrUtil.isNotBlank(r.getE())) nonBlank++;
            if (nonBlank < 2) errs.add("选项至少有两个不能为空");
            String raw = StrUtil.trimToEmpty(r.getAns()).toUpperCase().replaceAll("[^A-E]", "");
            if (StrUtil.isBlank(raw)) errs.add("答案不能为空");
            else {
                java.util.LinkedHashSet<Character> letters = new java.util.LinkedHashSet<>();
                for (char ch : raw.toCharArray()) letters.add(ch);
                if (letters.size() < 2) errs.add("答案至少选择两个选项");
                java.util.Set<Character> allowed = new java.util.HashSet<>();
                if (StrUtil.isNotBlank(r.getA())) allowed.add('A');
                if (StrUtil.isNotBlank(r.getB())) allowed.add('B');
                if (StrUtil.isNotBlank(r.getC())) allowed.add('C');
                if (StrUtil.isNotBlank(r.getD())) allowed.add('D');
                if (StrUtil.isNotBlank(r.getE())) allowed.add('E');
                for (char ch : letters) { if (!allowed.contains(ch)) { errs.add("答案包含未提供的选项" + ch); break; } }
            }
            if (!errs.isEmpty()) { QuestionImportResultVO.RowError re = new QuestionImportResultVO.RowError(); re.setRowNum(r.excelRow); re.setMessage("[多选题] 第" + r.showIndex + "条：" + String.join("；", errs)); rowErrors.add(re); }
        }
        // 判断
        for (TrueFalseRec r : data.judges) {
            List<String> errs = new ArrayList<>();
            if (StrUtil.isBlank(r.getType())) errs.add("题目分类不能为空"); else { try { QuestionCategoryEnum.getCodeByName(r.getType()); } catch (Exception e) { errs.add("题目分类不正确"); } }
            if (StrUtil.isBlank(r.getDiff())) errs.add("题目难度不能为空"); else { try { QuestionDifficultyEnum.getCodeByName(r.getDiff()); } catch (Exception e) { errs.add("题目难度不正确，仅限：一级、二级、三级、四级、五级"); } }
            if (StrUtil.isBlank(r.getSym())) errs.add("所属范围不能为空"); else { try { EntitySystemEnum.getCodeByName(r.getSym()); } catch (Exception e) { errs.add("所属范围不正确"); } }
            if (StrUtil.isBlank(r.getQ())) errs.add("试题不能为空");
            String up = StrUtil.trimToEmpty(r.getAns()).toUpperCase();
            if (StrUtil.isBlank(up)) errs.add("答案不能为空");
            else if (!("A".equals(up) || "B".equals(up))) errs.add("答案只能为A或B");
            if (!errs.isEmpty()) { QuestionImportResultVO.RowError re = new QuestionImportResultVO.RowError(); re.setRowNum(r.excelRow); re.setMessage("[判断题] 第" + r.showIndex + "条：" + String.join("；", errs)); rowErrors.add(re); }
        }
        int total = data.singles.size() + data.multiples.size() + data.judges.size();
        result.setTotalCount(total);
        result.setFailCount(rowErrors.size());
        result.setSuccessCount(total - rowErrors.size());
        result.setRowErrors(rowErrors);
        result.setErrorMessages(errorMessages);
        return result;
    }

    // ===== 内部数据结构 =====
    // 使用已有的 Excel 实体作为承载体，外加元信息（excelRow/showIndex）
    private static class SingleChoiceRec {
        int excelRow; int showIndex;
        com.xinkao.erp.question.excel.SingleChoiceSheetModel row;
        String getType(){ return row.getType(); }
        String getDiff(){ return row.getDifficultyLevel(); }
        String getSym(){ return row.getSymbol(); }
        String getQ(){ return row.getQuestion(); }
        String getA(){ return row.getOptionA(); }
        String getB(){ return row.getOptionB(); }
        String getC(){ return row.getOptionC(); }
        String getD(){ return row.getOptionD(); }
        String getAns(){ return row.getAnswer(); }
        String getKnowledgePointName(){ return row.getKnowledgePointName(); }
    }
    private static class MultipleChoiceRec {
        int excelRow; int showIndex;
        com.xinkao.erp.question.excel.MultipleChoiceSheetModel row;
        String getType(){ return row.getType(); }
        String getDiff(){ return row.getDifficultyLevel(); }
        String getSym(){ return row.getSymbol(); }
        String getQ(){ return row.getQuestion(); }
        String getA(){ return row.getOptionA(); }
        String getB(){ return row.getOptionB(); }
        String getC(){ return row.getOptionC(); }
        String getD(){ return row.getOptionD(); }
        String getE(){ return row.getOptionE(); }
        String getAns(){ return row.getAnswer(); }
        String getKnowledgePointName(){ return row.getKnowledgePointName(); }
    }
    private static class TrueFalseRec {
        int excelRow; int showIndex;
        com.xinkao.erp.question.excel.TrueFalseSheetModel row;
        String getType(){ return row.getType(); }
        String getDiff(){ return row.getDifficultyLevel(); }
        String getSym(){ return row.getSymbol(); }
        String getQ(){ return row.getQuestion(); }
        String getAns(){ return row.getAnswer(); }
        String getKnowledgePointName(){ return row.getKnowledgePointName(); }
    }
    private static class ReadResult {
        List<SingleChoiceRec> singles = new ArrayList<>();
        List<MultipleChoiceRec> multiples = new ArrayList<>();
        List<TrueFalseRec> judges = new ArrayList<>();
    }

    // ===== 按题型的存库接口
    /**
     * 保存单选题记录（包含知识点匹配）
     */
    private void saveSingleChoiceRecordWithKnowledgePoint(SingleChoiceRec record) {
        // 归一化输入
        String questionText = StrUtil.trimToEmpty(record.getQ());
        String optA = StrUtil.trimToEmpty(record.getA());
        String optB = StrUtil.trimToEmpty(record.getB());
        String optC = StrUtil.trimToEmpty(record.getC());
        String optD = StrUtil.trimToEmpty(record.getD());
        String answer = StrUtil.trimToEmpty(record.getAns()).toUpperCase();

        // 选项保持顺序且仅保留非空
        java.util.LinkedHashMap<String, String> optionMap = new java.util.LinkedHashMap<>();
        if (StrUtil.isNotBlank(optA)) optionMap.put("A", optA);
        if (StrUtil.isNotBlank(optB)) optionMap.put("B", optB);
        if (StrUtil.isNotBlank(optC)) optionMap.put("C", optC);
        if (StrUtil.isNotBlank(optD)) optionMap.put("D", optD);

        // 拼装题干HTML/纯文本与选项编码
        String htmlContent = buildSingleChoiceHtml(questionText, optionMap);
        String plainContent = buildSingleChoicePlain(questionText, optionMap);
        String optionsCodes = "[" + String.join(",", optionMap.keySet()) + "]";

        // 组装实体
        Question question = new Question();
        question.setShape(QuestionTypesEnum.DANXUAN.getCode());
        // 分类/难度/范围
        question.setType(QuestionCategoryEnum.getCodeByName(record.getType()));
        Integer difficultyLevel = QuestionDifficultyEnum.getCodeByName(record.getDiff());
        question.setDifficultyLevel(difficultyLevel);
        question.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(record.getSym())));
        // 标题与题干
        question.setTitle(questionText);
        question.setQuestion(htmlContent);
        question.setQuestionText(plainContent);
        // 选项与答案
        question.setOptions(optionsCodes);
        question.setAnswer(answer);
        // 其他默认
        question.setEstimatedTime(1);

        // 🔥 关键新增：知识点匹配逻辑
        if (StrUtil.isNotBlank(record.getKnowledgePointName())) {
            KnowledgePointCacheManager.KnowledgePointMatchResult matchResult = 
                knowledgePointCacheManager.matchFromCache(
                    currentImportKnowledgeCache, 
                    difficultyLevel, 
                    record.getKnowledgePointName()
                );
            
            handleKnowledgePointMatchResult(record, question, matchResult, "[单选题]");
        }

        // 🔥 调试：打印即将入库的question对象信息
        log.info("📝 准备入库题目：ID={}, 难度等级={}, 知识点ID={}, 标题={}", 
            question.getId(), question.getDifficultyLevel(), question.getDifficultyPointId(), 
            question.getTitle() != null ? question.getTitle().substring(0, Math.min(20, question.getTitle().length())) + "..." : "无");
        
        // 入库
        questionMapper.insert(question);
    }

    /**
     * 处理知识点匹配结果的通用方法
     */
    private void handleKnowledgePointMatchResult(
            Object record, 
            Question question, 
            KnowledgePointCacheManager.KnowledgePointMatchResult matchResult, 
            String questionType) {
        
        if (matchResult.isMatched()) {
            question.setDifficultyPointId(matchResult.getDifficultyPointId());
            
            // 模糊匹配给出提示
            if ("FUZZY".equals(matchResult.getMatchType())) {
                int showIndex = getShowIndex(record);
                addWarningToCurrentImport(getExcelRow(record), 
                    String.format("%s 第%d行：%s", questionType, showIndex, matchResult.getSuggestion()),
                    "KNOWLEDGE_POINT_FUZZY_MATCHED");
            }
        } else {
            // 未匹配到，设置为null但允许保存
            question.setDifficultyPointId(null);
            
            String errorMsg;
            if (StrUtil.isNotBlank(matchResult.getErrorMessage())) {
                errorMsg = matchResult.getErrorMessage();
            } else {
                String knowledgePointName = getKnowledgePointName(record);
                String availablePoints = matchResult.getAvailablePoints().size() > 5 ? 
                    String.join("、", matchResult.getAvailablePoints().subList(0, 5)) + "等" :
                    String.join("、", matchResult.getAvailablePoints());
                errorMsg = String.format("知识点【%s】未匹配成功，数据已导入，可在前台修改。难度等级%d可选知识点：%s", 
                    knowledgePointName, question.getDifficultyLevel(), availablePoints);
            }
            
            int showIndex = getShowIndex(record);
            addWarningToCurrentImport(getExcelRow(record), 
                String.format("%s 第%d行：%s", questionType, showIndex, errorMsg),
                "KNOWLEDGE_POINT_NOT_MATCHED");
        }
    }
    
    /**
     * 获取记录的行号
     */
    private int getExcelRow(Object record) {
        if (record instanceof SingleChoiceRec) return ((SingleChoiceRec) record).excelRow;
        if (record instanceof MultipleChoiceRec) return ((MultipleChoiceRec) record).excelRow;
        if (record instanceof TrueFalseRec) return ((TrueFalseRec) record).excelRow;
        return 0;
    }
    
    /**
     * 获取记录的显示序号
     */
    private int getShowIndex(Object record) {
        if (record instanceof SingleChoiceRec) return ((SingleChoiceRec) record).showIndex;
        if (record instanceof MultipleChoiceRec) return ((MultipleChoiceRec) record).showIndex;
        if (record instanceof TrueFalseRec) return ((TrueFalseRec) record).showIndex;
        return 0;
    }
    
    /**
     * 获取记录的知识点名称
     */
    private String getKnowledgePointName(Object record) {
        if (record instanceof SingleChoiceRec) return ((SingleChoiceRec) record).getKnowledgePointName();
        if (record instanceof MultipleChoiceRec) return ((MultipleChoiceRec) record).getKnowledgePointName();
        if (record instanceof TrueFalseRec) return ((TrueFalseRec) record).getKnowledgePointName();
        return "";
    }
    
    /**
     * 添加警告信息到当前导入会话
     */
    private void addWarningToCurrentImport(Integer rowNum, String message, String warningType) {
        QuestionImportResultVO.RowError warning = new QuestionImportResultVO.RowError();
        warning.setRowNum(rowNum);
        warning.setMessage(message);
        warning.setWarningType(warningType);
        warning.setIsWarning(true);
        currentImportErrors.add(warning);
    }

    private void saveSingleChoiceRecord(SingleChoiceRec record) {
        // 归一化输入
        String questionText = StrUtil.trimToEmpty(record.getQ());
        String optA = StrUtil.trimToEmpty(record.getA());
        String optB = StrUtil.trimToEmpty(record.getB());
        String optC = StrUtil.trimToEmpty(record.getC());
        String optD = StrUtil.trimToEmpty(record.getD());
        String answer = StrUtil.trimToEmpty(record.getAns()).toUpperCase();

        // 选项保持顺序且仅保留非空
        java.util.LinkedHashMap<String, String> optionMap = new java.util.LinkedHashMap<>();
        if (StrUtil.isNotBlank(optA)) optionMap.put("A", optA);
        if (StrUtil.isNotBlank(optB)) optionMap.put("B", optB);
        if (StrUtil.isNotBlank(optC)) optionMap.put("C", optC);
        if (StrUtil.isNotBlank(optD)) optionMap.put("D", optD);

        // 拼装题干HTML/纯文本与选项编码
        String htmlContent = buildSingleChoiceHtml(questionText, optionMap);
        String plainContent = buildSingleChoicePlain(questionText, optionMap);
        String optionsCodes = "[" + String.join(",", optionMap.keySet()) + "]";

        // 组装实体
        Question question = new Question();
        question.setShape(QuestionTypesEnum.DANXUAN.getCode());
        // 分类/难度/范围
        question.setType(QuestionCategoryEnum.getCodeByName(record.getType()));
        question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(record.getDiff()));
        question.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(record.getSym())));
        // 标题与题干
        question.setTitle(questionText);
        question.setQuestion(htmlContent);
        question.setQuestionText(plainContent);
        // 选项与答案
        question.setOptions(optionsCodes);
        question.setAnswer(answer);
        // 其他默认
        question.setEstimatedTime(1);

        // 入库
        questionMapper.insert(question);
    }

    private String buildSingleChoiceHtml(String questionText, java.util.Map<String, String> optionMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>").append(questionText).append("</p>");
        sb.append("<span id='tag'></span>");
        for (java.util.Map.Entry<String, String> e : optionMap.entrySet()) {
            sb.append("<p>").append(e.getKey()).append(".").append(e.getValue()).append("</p>");
        }
        return sb.toString();
    }

    private String buildSingleChoicePlain(String questionText, java.util.Map<String, String> optionMap) {
        StringBuilder sb = new StringBuilder();
        sb.append(questionText);
        for (java.util.Map.Entry<String, String> e : optionMap.entrySet()) {
            sb.append(e.getKey()).append(".").append(e.getValue());
        }
        return sb.toString();
    }

    /**
     * 保存多选题记录（包含知识点匹配）
     */
    private void saveMultipleChoiceRecordWithKnowledgePoint(MultipleChoiceRec record) {
        // 归一化输入
        String questionText = StrUtil.trimToEmpty(record.getQ());
        String optA = StrUtil.trimToEmpty(record.getA());
        String optB = StrUtil.trimToEmpty(record.getB());
        String optC = StrUtil.trimToEmpty(record.getC());
        String optD = StrUtil.trimToEmpty(record.getD());
        String optE = StrUtil.trimToEmpty(record.getE());
        String answer = StrUtil.trimToEmpty(record.getAns()).toUpperCase(); // 如 AB、ACD

        // 选项保持顺序且仅保留非空（A-E）
        java.util.LinkedHashMap<String, String> optionMap = new java.util.LinkedHashMap<>();
        if (StrUtil.isNotBlank(optA)) optionMap.put("A", optA);
        if (StrUtil.isNotBlank(optB)) optionMap.put("B", optB);
        if (StrUtil.isNotBlank(optC)) optionMap.put("C", optC);
        if (StrUtil.isNotBlank(optD)) optionMap.put("D", optD);
        if (StrUtil.isNotBlank(optE)) optionMap.put("E", optE);

        // 拼装题干HTML/纯文本与选项编码
        String htmlContent = buildSingleChoiceHtml(questionText, optionMap);
        String plainContent = buildSingleChoicePlain(questionText, optionMap);
        String optionsCodes = "[" + String.join(",", optionMap.keySet()) + "]";

        // 组装实体
        Question question = new Question();
        question.setShape(QuestionTypesEnum.DUOXUAN.getCode());
        // 分类/难度/范围
        question.setType(QuestionCategoryEnum.getCodeByName(record.getType()));
        Integer difficultyLevel = QuestionDifficultyEnum.getCodeByName(record.getDiff());
        question.setDifficultyLevel(difficultyLevel);
        question.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(record.getSym())));
        // 标题与题干
        question.setTitle(questionText);
        question.setQuestion(htmlContent);
        question.setQuestionText(plainContent);
        // 选项与答案
        question.setOptions(optionsCodes);
        question.setAnswer(answer); // 多选答案直接保存字母组合
        // 其他默认
        question.setEstimatedTime(1);

        // 🔥 关键新增：知识点匹配逻辑
        if (StrUtil.isNotBlank(record.getKnowledgePointName())) {
            KnowledgePointCacheManager.KnowledgePointMatchResult matchResult = 
                knowledgePointCacheManager.matchFromCache(
                    currentImportKnowledgeCache, 
                    difficultyLevel, 
                    record.getKnowledgePointName()
                );
            
            handleKnowledgePointMatchResult(record, question, matchResult, "[多选题]");
        }

        // 🔥 调试：打印即将入库的question对象信息
        log.info("📝 准备入库题目：ID={}, 难度等级={}, 知识点ID={}, 标题={}", 
            question.getId(), question.getDifficultyLevel(), question.getDifficultyPointId(), 
            question.getTitle() != null ? question.getTitle().substring(0, Math.min(20, question.getTitle().length())) + "..." : "无");

        // 入库
        questionMapper.insert(question);
    }

    private void saveMultipleChoiceRecord(MultipleChoiceRec record) {
        // 归一化输入
        String questionText = StrUtil.trimToEmpty(record.getQ());
        String optA = StrUtil.trimToEmpty(record.getA());
        String optB = StrUtil.trimToEmpty(record.getB());
        String optC = StrUtil.trimToEmpty(record.getC());
        String optD = StrUtil.trimToEmpty(record.getD());
        String optE = StrUtil.trimToEmpty(record.getE());
        String answer = StrUtil.trimToEmpty(record.getAns()).toUpperCase(); // 如 AB、ACD

        // 选项保持顺序且仅保留非空（A-E）
        java.util.LinkedHashMap<String, String> optionMap = new java.util.LinkedHashMap<>();
        if (StrUtil.isNotBlank(optA)) optionMap.put("A", optA);
        if (StrUtil.isNotBlank(optB)) optionMap.put("B", optB);
        if (StrUtil.isNotBlank(optC)) optionMap.put("C", optC);
        if (StrUtil.isNotBlank(optD)) optionMap.put("D", optD);
        if (StrUtil.isNotBlank(optE)) optionMap.put("E", optE);

        // 拼装题干HTML/纯文本与选项编码
        String htmlContent = buildSingleChoiceHtml(questionText, optionMap);
        String plainContent = buildSingleChoicePlain(questionText, optionMap);
        String optionsCodes = "[" + String.join(",", optionMap.keySet()) + "]";

        // 组装实体
        Question question = new Question();
        question.setShape(QuestionTypesEnum.DUOXUAN.getCode());
        // 分类/难度/范围
        question.setType(QuestionCategoryEnum.getCodeByName(record.getType()));
        question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(record.getDiff()));
        question.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(record.getSym())));
        // 标题与题干
        question.setTitle(questionText);
        question.setQuestion(htmlContent);
        question.setQuestionText(plainContent);
        // 选项与答案
        question.setOptions(optionsCodes);
        question.setAnswer(answer); // 多选答案直接保存字母组合
        // 其他默认
        question.setEstimatedTime(1);

        // 入库
        questionMapper.insert(question);
    }

    /**
     * 保存判断题记录（包含知识点匹配）
     */
    private void saveTrueFalseRecordWithKnowledgePoint(TrueFalseRec record) {
        // 判断题
        Question question = new Question();

        question.setShape(QuestionTypesEnum.PANDUAN.getCode());
        // 分类/难度/范围
        question.setType(QuestionCategoryEnum.getCodeByName(record.getType()));
        Integer difficultyLevel = QuestionDifficultyEnum.getCodeByName(record.getDiff());
        question.setDifficultyLevel(difficultyLevel);
        question.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(record.getSym())));
        question.setTitle(record.getQ());
        question.setQuestion("<p>"+record.getQ()+"</p>"+"<span id='tag'></span><p>A.正确</p><p>B.错误</p>");
        question.setQuestionText(record.getQ());
        question.setOptions("[A,B]");
        question.setAnswer(record.getAns());
        question.setEstimatedTime(1);

        // 🔥 关键新增：知识点匹配逻辑
        if (StrUtil.isNotBlank(record.getKnowledgePointName())) {
            KnowledgePointCacheManager.KnowledgePointMatchResult matchResult = 
                knowledgePointCacheManager.matchFromCache(
                    currentImportKnowledgeCache, 
                    difficultyLevel, 
                    record.getKnowledgePointName()
                );
            
            handleKnowledgePointMatchResult(record, question, matchResult, "[判断题]");
        }

        // 🔥 调试：打印即将入库的question对象信息
        log.info("📝 准备入库题目：ID={}, 难度等级={}, 知识点ID={}, 标题={}", 
            question.getId(), question.getDifficultyLevel(), question.getDifficultyPointId(), 
            question.getTitle() != null ? question.getTitle().substring(0, Math.min(20, question.getTitle().length())) + "..." : "无");

        // 入库
        questionMapper.insert(question);
    }

    private void saveTrueFalseRecord(TrueFalseRec record) {
        // 判断
        Question question = new Question();

        question.setShape(QuestionTypesEnum.PANDUAN.getCode());
        // 分类/难度/范围
        question.setType(QuestionCategoryEnum.getCodeByName(record.getType()));
        question.setDifficultyLevel(QuestionDifficultyEnum.getCodeByName(record.getDiff()));
        question.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(record.getSym())));
        question.setTitle(record.getQ());
        question.setQuestion("<p>"+record.getQ()+"</p>"+"<span id='tag'></span><p>A.正确</p><p>B.错误</p>");
        question.setQuestionText(record.getQ());
        question.setOptions("[A,B]");
        question.setAnswer(record.getAns());
        question.setEstimatedTime(1);
        // 入库
        questionMapper.insert(question);

    }




//===========以下是题目单批量导入的方法=======
    @Override
    public QuestionImportResultVO importQuestionFormZipV2(MultipartFile file) throws IOException {
        QuestionImportResultVO result = new QuestionImportResultVO();
        Path tempDir = Files.createTempDirectory("qform_zip_v2_");
        
        // 🔥 初始化知识点缓存
        currentImportKnowledgeCache = knowledgePointCacheManager.buildKnowledgePointCache();
        currentImportErrors = new ArrayList<>();
        
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
            
            // 🔥 合并知识点匹配产生的错误信息
            if (!currentImportErrors.isEmpty()) {
                if (result.getRowErrors() == null) {
                    result.setRowErrors(new ArrayList<>());
                }
                result.getRowErrors().addAll(currentImportErrors);
                
                // 同时添加到错误消息列表（向后兼容）
                if (result.getErrorMessages() == null) {
                    result.setErrorMessages(new ArrayList<>());
                }
                currentImportErrors.forEach(error -> 
                    result.getErrorMessages().add(error.getMessage()));
            }
            
            return result;
        } finally {
            deleteDirectoryQuietly(tempDir);
            // 🔥 清理知识点缓存
            currentImportKnowledgeCache = null;
            currentImportErrors = null;
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
        
        Integer difficultyLevel = null;
        try { if (StrUtil.isNotBlank(h.getType())) q.setType(QuestionCategoryEnum.getCodeByName(h.getType())); } catch (Exception ignore) {}
        try { if (StrUtil.isNotBlank(h.getDifficultyLevel())) { 
            difficultyLevel = QuestionDifficultyEnum.getCodeByName(h.getDifficultyLevel()); 
            q.setDifficultyLevel(difficultyLevel); 
        }} catch (Exception ignore) {}
        try { if (StrUtil.isNotBlank(h.getSymbol())) q.setSymbol(String.valueOf(EntitySystemEnum.getCodeByName(h.getSymbol()))); } catch (Exception ignore) {}
        try { if (StrUtil.isNotBlank(h.getState())) q.setState(StatusEnum.getCodeByName(h.getState())); } catch (Exception ignore) {}
        
        // 🔥 关键新增：知识点匹配逻辑
        if (StrUtil.isNotBlank(h.getKnowledgePointName()) && difficultyLevel != null) {
            KnowledgePointCacheManager.KnowledgePointMatchResult matchResult = 
                knowledgePointCacheManager.matchFromCache(
                    currentImportKnowledgeCache, 
                    difficultyLevel, 
                    h.getKnowledgePointName()
                );
            
            handleFormKnowledgePointMatchResult(h, q, matchResult);
        }
        
        // 🔥 打印题目单Excel数据
        log.info("📊 [题目单] 组代码[{}]：题目分类=[{}], 难度=[{}], 标签=[{}], 知识点=[{}], 标题=[{}], 文件URL=[{}]", 
            h.getGroupCode(), h.getType(), h.getDifficultyLevel(), h.getSymbol(), 
            h.getKnowledgePointName(),
            h.getTitle() != null ? (h.getTitle().length() > 30 ? h.getTitle().substring(0, 30) + "..." : h.getTitle()) : "无",
            qFileUrl);
        
        // 🔥 调试：打印即将入库的question对象信息
        log.info("📝 准备入库题目单：ID={}, 难度等级={}, 知识点ID={}, 标题={}", 
            q.getId(), q.getDifficultyLevel(), q.getDifficultyPointId(), 
            q.getTitle() != null ? q.getTitle().substring(0, Math.min(20, q.getTitle().length())) + "..." : "无");
        
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

    // 仅在本类内使用的小工具方法（与批量导入实现直接相关）
    private org.apache.poi.ss.usermodel.Sheet findSheet(org.apache.poi.ss.usermodel.Workbook wb, String[] names, int fallbackIndex) {
        for (String n : names) { if (n == null) continue; org.apache.poi.ss.usermodel.Sheet s = wb.getSheet(n); if (s != null) return s; }
        if (fallbackIndex >= 0 && fallbackIndex < wb.getNumberOfSheets()) return wb.getSheetAt(fallbackIndex);
        return null;
    }
    private java.util.Map<String, Integer> headerIndex(org.apache.poi.ss.usermodel.Row head) {
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        if (head == null) return map;
        org.apache.poi.ss.usermodel.DataFormatter fmt = new org.apache.poi.ss.usermodel.DataFormatter();
        short last = head.getLastCellNum();
        for (int c = 0; c < (last < 0 ? 0 : last); c++) {
            String name = fmt.formatCellValue(head.getCell(c));
            if (StrUtil.isNotBlank(name)) map.put(name.trim(), c);
        }
        return map;
    }
    private String getCell(org.apache.poi.ss.usermodel.Row row, Integer col) {
        if (row == null || col == null) return null;
        org.apache.poi.ss.usermodel.DataFormatter fmt = new org.apache.poi.ss.usermodel.DataFormatter();
        return fmt.formatCellValue(row.getCell(col)).trim();
    }

    
    /**
     * 处理题目单知识点匹配结果
     */
    private void handleFormKnowledgePointMatchResult(QfHeadV2 head, Question question, 
            KnowledgePointCacheManager.KnowledgePointMatchResult matchResult) {
        
        if (matchResult.isMatched()) {
            question.setDifficultyPointId(matchResult.getDifficultyPointId());
            
            // 模糊匹配给出提示
            if ("FUZZY".equals(matchResult.getMatchType())) {
                addFormWarning(head.getGroupCode(), 
                    String.format("[题目单] 组代码[%s]：%s", head.getGroupCode(), matchResult.getSuggestion()),
                    "KNOWLEDGE_POINT_FUZZY_MATCHED");
            }
        } else {
            // 未匹配到，设置为null但允许保存
            question.setDifficultyPointId(null);
            
            String errorMsg;
            if (StrUtil.isNotBlank(matchResult.getErrorMessage())) {
                errorMsg = matchResult.getErrorMessage();
            } else {
                String availablePoints = matchResult.getAvailablePoints().size() > 5 ? 
                    String.join("、", matchResult.getAvailablePoints().subList(0, 5)) + "等" :
                    String.join("、", matchResult.getAvailablePoints());
                errorMsg = String.format("知识点【%s】未匹配成功，数据已导入，可在前台修改。难度等级%d可选知识点：%s", 
                    head.getKnowledgePointName(), question.getDifficultyLevel(), availablePoints);
            }
            
            addFormWarning(head.getGroupCode(), 
                String.format("[题目单] 组代码[%s]：%s", head.getGroupCode(), errorMsg),
                "KNOWLEDGE_POINT_NOT_MATCHED");
        }
    }
    
    /**
     * 添加题目单警告信息到当前导入会话
     */
    private void addFormWarning(String groupCode, String message, String warningType) {
        QuestionImportResultVO.RowError warning = new QuestionImportResultVO.RowError();
        warning.setRowNum(null); // 题目单没有具体行号，使用组代码标识
        warning.setMessage(message);
        warning.setWarningType(warningType);
        warning.setIsWarning(true);
        currentImportErrors.add(warning);
    }
}
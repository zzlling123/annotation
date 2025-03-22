package com.xinkao.erp.question.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.xinkao.erp.common.enums.CommonEnum;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.param.DeleteParam;
import com.xinkao.erp.question.entity.Question;
import com.xinkao.erp.question.entity.QuestionLabel;
import com.xinkao.erp.question.mapper.QuestionMapper;
import com.xinkao.erp.question.service.QuestionService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.question.param.QuestionParam;
import com.xinkao.erp.question.query.QuestionQuery;
import com.xinkao.erp.question.vo.QuestionPageVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.model.support.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends BaseServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionLabelServiceImpl questionLabelService;

    @Override
    public Page<QuestionPageVo> page(QuestionQuery query, Pageable pageable) {
        Page page = pageable.toPage();
        return questionMapper.page(page, query);
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
        Question question = new Question();
        BeanUtils.copyProperties(param, question);
        question.setOptions(getRealOptions(param.getOptions()));
        question.setQuestionText(StripHT(question.getQuestion()));
        save(question);
        //保存标签关联关系
        List<QuestionLabel> questionLabels = param.getLabels().stream().map(item -> {
            QuestionLabel questionLabel = new QuestionLabel();
            questionLabel.setQid(question.getId());
            questionLabel.setLid(item);
            return questionLabel;
        }).collect(Collectors.toList());
        questionLabelService.saveBatch(questionLabels);
        return BaseResponse.ok("新增成功！");
    }

    @Override
    public BaseResponse<?> update(QuestionParam param) {
        Question question = new Question();
        //验证主题数据格式
        BaseResponse<?> taskVerState = verificationTaskTeaSaveParam(param);
        if (!"ok".equals(taskVerState.getState())){
            return taskVerState;
        }
        BeanUtils.copyProperties(param, question);
        question.setOptions(getRealOptions(param.getOptions()));
        question.setQuestionText(StripHT(question.getQuestion()));
        //删除原有标签关联关系
        questionLabelService.lambdaUpdate().eq(QuestionLabel::getQid, question.getId()).remove();
        List<QuestionLabel> questionLabels = param.getLabels().stream().map(item -> {
            QuestionLabel questionLabel = new QuestionLabel();
            questionLabel.setQid(question.getId());
            questionLabel.setLid(item);
            return questionLabel;
        }).collect(Collectors.toList());
        questionLabelService.saveBatch(questionLabels);
        return updateById(question) ? BaseResponse.ok("编辑成功！") : BaseResponse.fail("编辑失败！");
    }

    public BaseResponse<?> verificationTaskTeaSaveParam(QuestionParam param){
        List<String> optionList = param.getOptions();
        if (optionList.isEmpty()){
            return BaseResponse.fail("选项不可为空！");
        }
        if ("100".equals(param.getType()) || "300".equals(param.getType())){
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
    public BaseResponse<?> del(DeleteParam param) {
        return lambdaUpdate().in(Question::getId, param.getIds())
                .set(Question::getIsDel, CommonEnum.IS_DEL.YES.getCode())
                .update() ? BaseResponse.ok("删除成功！") : BaseResponse.fail("删除失败！");
    }
}
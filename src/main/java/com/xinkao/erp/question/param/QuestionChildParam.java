package com.xinkao.erp.question.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinkao.erp.common.model.support.InputConverter;
import com.xinkao.erp.question.entity.Question;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class QuestionChildParam implements InputConverter<Question> {

    @ApiModelProperty("子题ID(编辑时修改)")
    private Integer id;

    @ApiModelProperty("所属题目单二级列表ID")
    private Integer pid;

    @ApiModelProperty("所属题目单ID")
    private Integer questionId;

    @ApiModelProperty("题目")
    @NotBlank(message = "题目不能为空")
    @Size(min = 1, max = 255, message = "题目长度应在1到255个字符之间")
    private String question;

    @ApiModelProperty("文本框默认展示文字")
    private String defaultText;

    @ApiModelProperty("是否上传文件题，0否1是")
    private Integer isFile;

    @ApiModelProperty("文件后缀")
    private String fileType;

    @ApiModelProperty("答案")
    private String answer;

    @ApiModelProperty("解析")
    private String answerTip;

    @ApiModelProperty("题号排序")
    private Integer sort;

    @ApiModelProperty("是否启用0否1是")
    private Integer state;
}
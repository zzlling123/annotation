package com.xinkao.erp.question.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinkao.erp.common.model.entity.DataEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("q_question_type")
public class QuestionType extends DataEntity {

    @TableField("type_name")
    private String typeName;

    @TableField("file_url")
    private String fileUrl;


}

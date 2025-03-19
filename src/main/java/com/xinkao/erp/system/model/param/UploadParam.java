package com.xinkao.erp.system.model.param;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UploadParam {

    @NotNull(message = "附件不能为空")
    private MultipartFile file;

    @NotNull(message = "附件分类不能为空")
    /**目前只有两种,一个是账号头像(avatar),一个是课程封面图(cover)**/
    private String label = "cover";

}

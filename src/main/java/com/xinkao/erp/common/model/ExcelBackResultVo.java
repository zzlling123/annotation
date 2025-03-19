package com.xinkao.erp.common.model;

import lombok.Data;

/**
 * 文件导入后返回json数据
 **/
@Data
public class ExcelBackResultVo {

	//处理结果: 200-成功 其他未错误码
    private String resultStatus;
    //处理的数据条数
    private String count;
    //错误文件下载地址
    private String errorUrl;
}

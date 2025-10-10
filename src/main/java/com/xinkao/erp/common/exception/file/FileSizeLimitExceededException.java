package com.xinkao.erp.common.exception.file;

import cn.hutool.core.util.StrUtil;


public class FileSizeLimitExceededException extends FileException
{
    private static final long serialVersionUID = 1L;

    public FileSizeLimitExceededException(String message) {
        super(message);
    }

    public FileSizeLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileSizeLimitExceededException(long maxSize) {
        super(StrUtil.format("上传的文件超过最大限制：{}m", maxSize));
    }
}

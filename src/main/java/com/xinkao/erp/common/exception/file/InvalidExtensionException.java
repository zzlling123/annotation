package com.xinkao.erp.common.exception.file;

import java.util.List;

/**
 * 文件上传 误异常类
 *
 */
public class InvalidExtensionException extends FileException {
    private static final long serialVersionUID = 1L;

    private List<String> allowedExtension;
    private String extension;
    private String filename;

    public InvalidExtensionException(List<String> allowedExtension, String extension, String filename) {
        super("文件 : 【" + filename + "】, 后缀 : 【" + extension + "】, 不在允许的范围: 【" + allowedExtension.toString() + "】");
        this.allowedExtension = allowedExtension;
        this.extension = extension;
        this.filename = filename;
    }

    public List<String> getAllowedExtension() {
        return allowedExtension;
    }

    public String getExtension()
    {
        return extension;
    }

    public String getFilename()
    {
        return filename;
    }

    public static class InvalidImageExtensionException extends InvalidExtensionException {
        private static final long serialVersionUID = 1L;

        public InvalidImageExtensionException(List<String> allowedExtension, String extension, String filename) {
            super(allowedExtension, extension, filename);
        }
    }

}

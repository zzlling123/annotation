package com.xinkao.erp.common.model.support;

/**
 * 上传结果
 **/
public class UploadResult {

    /**
     * 文件名（不包含后缀）
     */
    private String filename;

    /**
     * 文件名包含后缀
     */
    private String fileFullName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件访问路径
     */
    private String urlPath;

    /**
     * 后缀
     */
    private String suffix;

    /**
     * 文件大小
     */
    private Long size;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileFullName() {
        return fileFullName;
    }

    public void setFileFullName(String fileFullName) {
        this.fileFullName = fileFullName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}

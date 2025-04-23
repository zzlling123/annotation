package com.xinkao.erp.course.utils;

import java.util.HashMap;
import java.util.Map;

public class FileTypeChecker {

    private static final Map<String, String> fileTypeMap = new HashMap<>();

    static {
        // 初始化文件类型与扩展名的映射关系
        fileTypeMap.put("txt", "文本文件");
        fileTypeMap.put("jpg", "图片文件（JPEG）");
        fileTypeMap.put("png", "图片文件（PNG）");
        fileTypeMap.put("pdf", "PDF文档");
        fileTypeMap.put("docx", "Word文档");
        fileTypeMap.put("xlsx", "Excel表格");
        fileTypeMap.put("pptx", "PowerPoint演示文稿");
        fileTypeMap.put("mp4", "视频文件（MP4）");
        fileTypeMap.put("mp3", "音频文件（MP3）");
        fileTypeMap.put("zip", "压缩文件（ZIP）");
        fileTypeMap.put("rar", "压缩文件（RAR）");
        fileTypeMap.put("7z", "压缩文件（7Z）");
        fileTypeMap.put("avi", "视频文件（avi）");
        fileTypeMap.put("wmv", "视频文件（wmv）");
        fileTypeMap.put("mpg", "视频文件（mpg）");
        fileTypeMap.put("wav", "音频文件（wav）");
        fileTypeMap.put("mpeg", "视频文件（mpeg）");
        fileTypeMap.put("mov", "视频文件（mov）");
        fileTypeMap.put("flv", "视频文件（flv）");
        fileTypeMap.put("mkv", "视频文件（mkv）");
    }

    public static String getFileType(String filePath) {
        // 获取文件的扩展名
        String extension = getExtension(filePath);
        // 查找并返回文件类型
        return fileTypeMap.getOrDefault(extension, "未知文件类型");
    }

    public static String getExtension(String filePath) {
        // 获取文件路径中的文件名部分
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        // 获取文件的扩展名
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        }
        return "";
    }

    public static void main(String[] args) {
        String filePath = "example.txt";
        System.out.println("文件类型: " + getFileType(filePath));

        filePath = "image.jpg";
        System.out.println("文件类型: " + getFileType(filePath));

        filePath = "document.docx";
        System.out.println("文件类型: " + getFileType(filePath));
    }
}

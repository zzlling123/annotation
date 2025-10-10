package com.xinkao.erp.common.constant;

import java.util.HashMap;
import java.util.Map;

public class FileTypeConstant {
    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();
    
    private FileTypeConstant() {
    }
    
    public static String getType(String fileTyle) {
        String type = FILE_TYPE_MAP.get(fileTyle.toLowerCase());
        if (type == null) {
            return "other";
        }
        return type;
    }
    
    static {
        FILE_TYPE_MAP.put("bmp", "image");
        FILE_TYPE_MAP.put("jpg", "image");
        FILE_TYPE_MAP.put("jpeg", "image");
        FILE_TYPE_MAP.put("png", "image");
        FILE_TYPE_MAP.put("tiff", "image");
        FILE_TYPE_MAP.put("gif", "image");
        FILE_TYPE_MAP.put("pcx", "image");
        FILE_TYPE_MAP.put("tga", "image");
        FILE_TYPE_MAP.put("exif", "image");
        FILE_TYPE_MAP.put("fpx", "image");
        FILE_TYPE_MAP.put("svg", "image");
        FILE_TYPE_MAP.put("psd", "image");
        FILE_TYPE_MAP.put("cdr", "image");
        FILE_TYPE_MAP.put("pcd", "image");
        FILE_TYPE_MAP.put("dxf", "image");
        FILE_TYPE_MAP.put("ufo", "image");
        FILE_TYPE_MAP.put("eps", "image");
        FILE_TYPE_MAP.put("ai", "image");
        FILE_TYPE_MAP.put("raw", "image");
        FILE_TYPE_MAP.put("wmf", "image");
        FILE_TYPE_MAP.put("zip", "zip");
        FILE_TYPE_MAP.put("tar", "zip");
        FILE_TYPE_MAP.put("txt", "txt");
        FILE_TYPE_MAP.put("rtf", "txt");
        FILE_TYPE_MAP.put("htm", "txt");
        FILE_TYPE_MAP.put("html", "txt");
        FILE_TYPE_MAP.put("md", "txt");
        FILE_TYPE_MAP.put("doc", "doc");
        FILE_TYPE_MAP.put("docx", "doc");
        FILE_TYPE_MAP.put("wpd", "doc");
        FILE_TYPE_MAP.put("xls", "xls");
        FILE_TYPE_MAP.put("xlsx", "xls");
        FILE_TYPE_MAP.put("ppt", "ppt");
        FILE_TYPE_MAP.put("pptx", "ppt");
        FILE_TYPE_MAP.put("jsp", "html");
        FILE_TYPE_MAP.put("pdf", "pdf");
        FILE_TYPE_MAP.put("mp4", "video");
        FILE_TYPE_MAP.put("avi", "video");
        FILE_TYPE_MAP.put("mov", "video");
        FILE_TYPE_MAP.put("wmv", "video");
        FILE_TYPE_MAP.put("asf", "video");
        FILE_TYPE_MAP.put("navi", "video");
        FILE_TYPE_MAP.put("3gp", "video");
        FILE_TYPE_MAP.put("mkv", "video");
        FILE_TYPE_MAP.put("f4v", "video");
        FILE_TYPE_MAP.put("rmvb", "video");
        FILE_TYPE_MAP.put("webm", "video");
        FILE_TYPE_MAP.put("mp3", "radio");
        FILE_TYPE_MAP.put("wma", "radio");
        FILE_TYPE_MAP.put("wav", "radio");
        FILE_TYPE_MAP.put("mod", "radio");
        FILE_TYPE_MAP.put("ra", "radio");
        FILE_TYPE_MAP.put("cd", "radio");
        FILE_TYPE_MAP.put("md", "radio");
        FILE_TYPE_MAP.put("asf", "radio");
        FILE_TYPE_MAP.put("aac", "radio");
        FILE_TYPE_MAP.put("vqf", "radio");
        FILE_TYPE_MAP.put("ape", "radio");
        FILE_TYPE_MAP.put("mid", "radio");
        FILE_TYPE_MAP.put("ogg", "radio");
        FILE_TYPE_MAP.put("m4a", "radio");
        FILE_TYPE_MAP.put("vqf", "radio");
        FILE_TYPE_MAP.put("folder", "folder");
    }
}

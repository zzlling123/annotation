package com.xinkao.erp.common.util.file;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MimeTypeUtil {
    public static final String IMAGE_PNG = "image/png";
    
    public static final String IMAGE_JPG = "image/jpg";
    
    public static final String IMAGE_JPEG = "image/jpeg";
    
    public static final String IMAGE_BMP = "image/bmp";
    
    public static final String IMAGE_GIF = "image/gif";
    
    public static final List<String> IMAGE_EXTENSION = Collections.unmodifiableList(Arrays.asList("bmp", "gif", "jpg", "jpeg", "png"));
    
    public static final List<String> DEFAULT_ALLOWED_EXTENSION = Collections.unmodifiableList(Arrays.asList(

            "bmp", "gif", "jpg", "jpeg", "png",

            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",

            "rar", "zip", "gz", "bz2",

            "pdf"));
    
    public static String getExtension(String prefix) {
        switch (prefix) {
            case IMAGE_PNG:
                return "png";
            case IMAGE_JPG:
                return "jpg";
            case IMAGE_JPEG:
                return "jpeg";
            case IMAGE_BMP:
                return "bmp";
            case IMAGE_GIF:
                return "gif";
            default:
                return "";
        }
    }
}

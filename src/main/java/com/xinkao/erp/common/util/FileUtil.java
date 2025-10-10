package com.xinkao.erp.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import cn.hutool.core.util.StrUtil;


public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    
    public static OutputStream getOutputStream(HttpServletResponse response, String fileName, String fileSuffix) throws IOException {
        try {
            fileName = fileNameEncode(fileName);
            fileName = fileName + fileSuffix;
            response.setCharacterEncoding("utf-8");
            response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            return response.getOutputStream();
        } catch (IOException e) {
            log.error("创建文件失败！");
            throw new IOException("创建文件失败！");
        }
    }

    
    public static String fileNameEncode(String fileName) throws IOException {
        fileName = URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");
        return fileName;
    }

    
    public static MultipartFile getMultipartFile(File file) {
        FileItem item = new DiskFileItemFactory().createItem("file"
            , MediaType.MULTIPART_FORM_DATA_VALUE
            , true
            , file.getName());
        try (InputStream input = new FileInputStream(file);
            OutputStream os = item.getOutputStream()) {

            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }
        return new CommonsMultipartFile(item);
    }

    public static void download(File file, String name, HttpServletResponse response)
        throws IOException {
        String fileName = file.getName();
        if (StrUtil.isNotEmpty(name)) {
            fileName = name;
        }
        String type = cn.hutool.core.io.FileUtil.extName(fileName);
        response.setContentType(type);
        response.setCharacterEncoding("utf-8");
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);

        OutputStream outputStream = response.getOutputStream();
        byte[] buff = new byte[1024];

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
            int i = bis.read(buff);
            while (i != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            log.error("下载文件失败！{}", e.getMessage());
            throw e;
        }
    }
    
    public static void downloadExcel(File file, String name, HttpServletResponse response)
    		throws IOException {
    	String fileName = file.getName();
    	if (StrUtil.isNotEmpty(name)) {
    		fileName = name;
    	}
    	response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    	response.setCharacterEncoding("UTF-8");
    	fileName = URLEncoder.encode(fileName, "UTF-8");
    	response.setHeader("Content-disposition", "attachment;filename=" + fileName);

    	OutputStream outputStream = response.getOutputStream();
    	byte[] buff = new byte[1024];

    	try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
    		int i = bis.read(buff);
    		while (i != -1) {
    			outputStream.write(buff, 0, buff.length);
    			outputStream.flush();
    			i = bis.read(buff);
    		}
    	} catch (IOException e) {
    		log.error("下载文件失败！{}", e.getMessage());
    		throw e;
    	}
    }
}

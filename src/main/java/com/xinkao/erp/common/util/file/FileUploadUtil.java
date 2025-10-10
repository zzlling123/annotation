package com.xinkao.erp.common.util.file;

import static cn.hutool.core.text.CharSequenceUtil.addSuffixIfNot;
import static com.xinkao.erp.common.constant.XinKaoConstant.FILE_SEPARATOR;
import static com.xinkao.erp.common.constant.XinKaoConstant.URL_SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.xinkao.erp.common.config.properties.XinKaoProperties;
import com.xinkao.erp.common.exception.file.FileException;
import com.xinkao.erp.common.exception.file.FileSizeLimitExceededException;
import com.xinkao.erp.common.exception.file.InvalidExtensionException;
import com.xinkao.erp.common.model.support.UploadResult;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

@Component
public class FileUploadUtil {

    @Resource
    private XinKaoProperties xinKaoProperties;

    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    public UploadResult upload(@NonNull String baseDir, @NonNull MultipartFile file) {
       return  upload(baseDir, file,  MimeTypeUtil.DEFAULT_ALLOWED_EXTENSION);
    }

    public UploadResult upload(@NonNull String baseDir, @NonNull MultipartFile file, List<String> allowdExtension) {
        String baseFilename = getOriginalBaseFileName(file);
        String newFileName = baseFilename + "-" + IdUtil.fastSimpleUUID();
        return upload(baseDir, file, allowdExtension, newFileName);
    }

    public UploadResult upload(@NonNull String baseDir, @NonNull MultipartFile file, List<String> allowdExtension, @NonNull String fileName) {
        assertAllowed(file, allowdExtension);
        String extension = getExtension(file);
        Long size = file.getSize();
        String orginalFileName = file.getOriginalFilename();
        String originalBaseFileName = getOriginalBaseFileName(file);
        // 拼接文件后缀
        fileName = fileName + "." + extension;;
        try {
            String subFilePath = getPathFileName(baseDir, fileName);
            // 创建文件
            File desc = createfile(subFilePath);
            file.transferTo(desc);

            UploadResult uploadResult = new UploadResult();
            uploadResult.setFileFullName(orginalFileName);
            uploadResult.setFilename(originalBaseFileName);
            uploadResult.setFilePath(subFilePath);
            uploadResult.setUrlPath(subFilePath.replace(FILE_SEPARATOR, URL_SEPARATOR));
            uploadResult.setSuffix(extension);
            uploadResult.setSize(size);
            return uploadResult;
        } catch (IOException ex) {
            throw new FileException("上传附件失败", ex);
        }
    }


    public void delete(@NonNull String subDir) {
        Path path = Paths.get(addSuffixIfNot(xinKaoProperties.getWorkDir(), FILE_SEPARATOR), subDir);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new FileException("附件 " + subDir + " 删除失败", e);
        }
    }


    private String getOriginalBaseFileName(@NonNull MultipartFile file) {
        // 附件名称
        String originalFilename = file.getOriginalFilename();
        return getOriginalBaseFileName(originalFilename);
    }

    public String getOriginalBaseFileName(@NonNull String fileName) {
        int dotLastIndex = StrUtil.lastIndexOfIgnoreCase(fileName, ".");
        // 将后缀截取
        return fileName.substring(0, dotLastIndex);
    }


    private String getPathFileName(String uploadDir, String fileName) {
        return uploadDir + FILE_SEPARATOR + fileName;
    }

    private String getExtension(@NonNull MultipartFile file) {
        String extension = FileNameUtil.extName(file.getOriginalFilename());
        if (StrUtil.isEmpty(extension)) {
            extension = MimeTypeUtil.getExtension(file.getContentType());
        }
        return extension;
    }

    
    private boolean isAllowedExtension(String extension, List<String> allowedExtensions) {
        return allowedExtensions.stream()
            .anyMatch(allowedExtension -> allowedExtension.equalsIgnoreCase(extension));
    }

    
    private void assertAllowed(@NonNull MultipartFile file, List<String> allowedExtension) {
        long size = file.getSize();
        if (DEFAULT_MAX_SIZE != -1 && size > DEFAULT_MAX_SIZE) {
            throw new FileSizeLimitExceededException(DEFAULT_MAX_SIZE / 1024 / 1024);
        }

        String fileName = file.getOriginalFilename();
        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            if (allowedExtension == MimeTypeUtil.IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension,
                    fileName);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, fileName);
            }
        }
    }

    
    private File createfile(String subFilePath) throws IOException {
        File desc = new File(addSuffixIfNot(xinKaoProperties.getWorkDir(), FILE_SEPARATOR)  + subFilePath);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

}

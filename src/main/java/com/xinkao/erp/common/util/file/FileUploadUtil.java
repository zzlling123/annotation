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

/**
 * @ClassName FileUploadUtil
 * @Description
 * @Author sunkai
 * @Date 2021/6/2 17:59
 **/
@Component
public class FileUploadUtil {

    @Resource
    private XinKaoProperties xinKaoProperties;

    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 上传
     * @param baseDir
     * @param file
     * @return
     */
    public UploadResult upload(@NonNull String baseDir, @NonNull MultipartFile file) {
       return  upload(baseDir, file,  MimeTypeUtil.DEFAULT_ALLOWED_EXTENSION);
    }

    /**
     * 上传
     * @param baseDir
     * @param file
     * @param allowdExtension
     * @return
     */
    public UploadResult upload(@NonNull String baseDir, @NonNull MultipartFile file, List<String> allowdExtension) {
        String baseFilename = getOriginalBaseFileName(file);
        // 获取新文件名称
        String newFileName = baseFilename + "-" + IdUtil.fastSimpleUUID();
        return upload(baseDir, file, allowdExtension, newFileName);
    }

    /**
     * 上传文件
     * @param baseDir 相对路径
     * @param file 文件
     * @param allowdExtension 允许的扩展名
     * @param fileName 指定附件名
     * @return
     * @throws IOException
     */
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


    /**
     * 删除文件
     * @param subDir
     */
    public void delete(@NonNull String subDir) {
        Path path = Paths.get(addSuffixIfNot(xinKaoProperties.getWorkDir(), FILE_SEPARATOR), subDir);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new FileException("附件 " + subDir + " 删除失败", e);
        }
    }


    /**
     * 获取文件不含后缀的文件名
     * @param file
     * @return
     */
    private String getOriginalBaseFileName(@NonNull MultipartFile file) {
        // 附件名称
        String originalFilename = file.getOriginalFilename();
        return getOriginalBaseFileName(originalFilename);
    }

    /**
     * 获取文件不含后缀的文件名
     * @param fileName
     * @return
     */
    public String getOriginalBaseFileName(@NonNull String fileName) {
        int dotLastIndex = StrUtil.lastIndexOfIgnoreCase(fileName, ".");
        // 将后缀截取
        return fileName.substring(0, dotLastIndex);
    }


    /**
     * 获取文件存储相对路径
     * @param uploadDir
     * @param fileName
     * @return
     */
    private String getPathFileName(String uploadDir, String fileName) {
        return uploadDir + FILE_SEPARATOR + fileName;
    }

    /**
     * 获取上传文件的后缀
     * @param file
     * @return
     */
    private String getExtension(@NonNull MultipartFile file) {
        String extension = FileNameUtil.extName(file.getOriginalFilename());
        if (StrUtil.isEmpty(extension)) {
            extension = MimeTypeUtil.getExtension(file.getContentType());
        }
        return extension;
    }

    /**
     * 判断上传的文件是否在允许范围内
     * @param extension
     * @param allowedExtensions
     * @return
     */
    private boolean isAllowedExtension(String extension, List<String> allowedExtensions) {
        return allowedExtensions.stream()
            .anyMatch(allowedExtension -> allowedExtension.equalsIgnoreCase(extension));
    }

    /**
     * 验证文件是否符合要求
     * @param file
     * @param allowedExtension
     * @throws FileSizeLimitExceededException
     * @throws InvalidExtensionException
     */
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

    /**
     * 创建文件
     * @param subFilePath
     * @return
     * @throws IOException
     */
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

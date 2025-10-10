package com.xinkao.erp.core.config;

import static cn.hutool.core.text.CharSequenceUtil.addPrefixIfNot;
import static cn.hutool.core.text.CharSequenceUtil.addSuffixIfNot;
import static com.xinkao.erp.common.constant.XinKaoConstant.FILE_PROTOCOL;
import static com.xinkao.erp.common.constant.XinKaoConstant.FILE_SEPARATOR;
import static com.xinkao.erp.common.constant.XinKaoConstant.URL_SEPARATOR;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.xinkao.erp.common.config.properties.XinKaoProperties;
import com.xinkao.erp.core.aspect.DataAuthCheckInterceptor;
import com.xinkao.erp.core.interceptor.RepeatSubmitInterceptor;

@Configuration
public class XinKaoWebMvcConfig implements WebMvcConfigurer {

    @Resource
    private RepeatSubmitInterceptor repeatSubmitInterceptor;
    @Resource
    private DataAuthCheckInterceptor dataAuthCheckInterceptor;

    private final XinKaoProperties xinKaoProperties;

    private final String uploadUrlPattern;

    @Value("${path.fileUrl}")
    private String QRCODE_PATH;
    @Value("${scene.scan.imgPath}")
    private String IMAGE_PATH;
    @Value("${scene.scan.pcdPath}")
    private String PCD_PATH;
    @Value("${path.cres}")
    private String cres;

    public XinKaoWebMvcConfig(XinKaoProperties xinKaoProperties) {
        this.xinKaoProperties = xinKaoProperties;
        this.uploadUrlPattern = addSuffixIfNot(
                addPrefixIfNot(xinKaoProperties.getUploadDir(),  URL_SEPARATOR),
                URL_SEPARATOR) + "**";
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**");
        registry.addInterceptor(dataAuthCheckInterceptor).addPathPatterns("/**").excludePathPatterns(
                "/ap/**",
                "/attach/**",
                "/manual/page",
                "/manual/view/**",
                "/user/**"
        );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String workDir = FILE_PROTOCOL + addSuffixIfNot(xinKaoProperties.getWorkDir(), FILE_SEPARATOR);
        String uploadDir = workDir + addSuffixIfNot(xinKaoProperties.getUploadDir(), FILE_SEPARATOR);
        registry.addResourceHandler(this.uploadUrlPattern).addResourceLocations(uploadDir);
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//        registry.addResourceHandler("/qrCode/**").addResourceLocations("classpath:/META-INF/resources/static/qrCode/");
        registry.addResourceHandler("/fileUrl/**").addResourceLocations("file:"+QRCODE_PATH);
        registry.addResourceHandler("/image/**").addResourceLocations("file:"+IMAGE_PATH);
        registry.addResourceHandler("/pcd/**").addResourceLocations("file:"+PCD_PATH);
        registry.addResourceHandler("/cres/**").addResourceLocations("file:"+cres);
    }
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new BufferedImageHttpMessageConverter());
    }
}

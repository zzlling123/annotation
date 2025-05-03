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

/**
 *mvc相关的配置
 **/
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

    public XinKaoWebMvcConfig(XinKaoProperties xinKaoProperties) {
        this.xinKaoProperties = xinKaoProperties;
        this.uploadUrlPattern = addSuffixIfNot(
            addPrefixIfNot(xinKaoProperties.getUploadDir(),  URL_SEPARATOR),
            URL_SEPARATOR) + "**";
    }

    /**
     * 自定义拦截规则
     */
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
    }
    /**
     * 增加图片转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new BufferedImageHttpMessageConverter());
    }
}

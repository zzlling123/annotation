package com.xinkao.erp.core.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class DozerMapperConfig {

    @Bean
    public Mapper dozerBeanMapperFactoryBean(@Value("classpath*:dozer/*.xml") Resource[] resources)
        throws IOException {
        List<String> mappingFileUrlList = new ArrayList<>();
        for (Resource resource : resources) {
            mappingFileUrlList.add(String.valueOf(resource.getURL()));
        }
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        dozerBeanMapper.setMappingFiles(mappingFileUrlList);
        return dozerBeanMapper;
    }

}

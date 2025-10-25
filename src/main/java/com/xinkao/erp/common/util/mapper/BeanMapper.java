package com.xinkao.erp.common.util.mapper;

import cn.hutool.core.util.ArrayUtil;
import java.util.ArrayList;
import java.util.List;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BeanMapper {

    private static Mapper mapper = new DozerBeanMapper();

    public BeanMapper(Mapper dozerMapper) {
        mapper = dozerMapper;
    }

    
    public static <S, D> D map(S source, Class<D> destinationClass) {
        return mapper.map(source, destinationClass);
    }

    
    public static <S, D> void map(S source, D destinations) {
        mapper.map(source, destinations);
    }

    
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<D> destinationClass) {
        List<D> destinationList = new ArrayList<D>();
        for (S source : sourceList) {
            if (source != null) {
                destinationList.add(mapper.map(source, destinationClass));
            }
        }
        return destinationList;
    }

    
    public static <S, D> D[] mapArray(final S[] sourceArray, final Class<D> destinationClass) {
        D[] destinationArray = ArrayUtil.newArray(destinationClass, sourceArray.length);

        int i = 0;
        for (S source : sourceArray) {
            if (source != null) {
                destinationArray[i] = mapper.map(sourceArray[i], destinationClass);
                i++;
            }
        }

        return destinationArray;
    }
}

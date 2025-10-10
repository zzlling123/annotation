package com.xinkao.erp.common.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.exception.BusinessException;

public class EntityUtils {
    
    public static void hasAllRequired(final Object Object, String requiredColumns) {
        if (StrUtil.isNotEmpty(requiredColumns) && ObjectUtil.isNotEmpty(Object)) {

            String[] columns = requiredColumns.split(",");
            String missCol = "";
            for (String column : columns) {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(Object));
                Object val = jsonObject.get(column.trim());
                if (ObjectUtil.isEmpty(val)) {
                    missCol += column + "  ";
                }
            }
            if (StrUtil.isNotEmpty(missCol)) {
                throw new BusinessException("缺少必填参数:" + missCol.trim());
            }
        }

    }
}

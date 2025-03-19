package com.xinkao.erp.common.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.exception.BusinessException;

public class EntityUtils {
    /**
     * 验证是否含有全部必填字段
     * @param requiredColumns 必填的参数字段名称 逗号隔开 比如"userId,name,telephone"
     */
    public static void hasAllRequired(final Object Object, String requiredColumns) {
        if (StrUtil.isNotEmpty(requiredColumns) && ObjectUtil.isNotEmpty(Object)) {
            //验证字段非空
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

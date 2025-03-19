package com.xinkao.erp.core.mybatisplus.handler;

import com.xinkao.erp.common.enums.system.TableSplitEnum;
/**
 * 动态表切换
 * @author hys_thanks
 *
 */
public class DynamicTableHolder {

    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    public static String get() {
        return holder.get();
    }
    /**
     * 放置表名前缀与规则
     * @param tableNamePrefix
     * @param year
     */
    public static void set(TableSplitEnum tablePrefix,int year) {
    	holder.set(tablePrefix.getTableName()+"_"+year);
    }
    /**
     * 放置表名前缀与规则
     * @param tableNamePrefix
     * @param year
     */
    public static void setYear(String tableNamePrefix,int year) {
        holder.set(tableNamePrefix+"_"+year);
    }
    /**
     * 放置表名
     * @param tableName
     */
    public static void set(String tableName) {
        holder.set(tableName);
    }

    public static void remove() {
        holder.remove();
    }

}



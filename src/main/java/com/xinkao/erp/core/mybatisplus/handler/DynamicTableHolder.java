package com.xinkao.erp.core.mybatisplus.handler;

import com.xinkao.erp.common.enums.system.TableSplitEnum;
public class DynamicTableHolder {

    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    public static String get() {
        return holder.get();
    }
    public static void set(TableSplitEnum tablePrefix,int year) {
    	holder.set(tablePrefix.getTableName()+"_"+year);
    }
    public static void setYear(String tableNamePrefix,int year) {
        holder.set(tableNamePrefix+"_"+year);
    }
    public static void set(String tableName) {
        holder.set(tableName);
    }
    public static void remove() {
        holder.remove();
    }

}



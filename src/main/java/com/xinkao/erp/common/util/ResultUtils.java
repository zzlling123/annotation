package com.xinkao.erp.common.util;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.HandleResult;

/**
 * 机构模块获取message结果
 */
@Component
public class ResultUtils {

    /**
     * 批量操作结果
     * @param handleResult
     * @param successCount
     * @param errorList
     * @return
     */
    public HandleResult getResult(HandleResult handleResult, Integer successCount, List<String> errorList){
        Map<String, Object> map = handleResult.getResult();
        Map<String, Object> errorMap = getErrorMsg(errorList);
        map.put("total", getTotalMsg("导入", handleResult.getTotalCount()));
        map.put("success", getSuccessMsg(successCount));
        map.put("fail", errorMap);
        handleResult.setSuccessCount(successCount);
        handleResult.setErrorCount(errorList.size());
        handleResult.setErrorList(errorList);
        handleResult.setResult(map);
        return handleResult;
    }

    /**
     * 错误返回
     * @param errorList
     * @return
     */
    public Map<String, Object> getErrorMsg(List<String> errorList){
        if(errorList.size() > 0){}   // 若无错误信息，不返回msg（再优化）
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("failCount", getErrCountMsg(errorList.size()));
        errorMap.put("failMsg", errorList);
        return errorMap;
    }

    public BaseResponse getResult(Integer successCount, List<String> errorList){
        Map<String, Object> map = new HashMap<>();
        String successMsg = StrUtil.format("成功：{}条", successCount);
        map.put("success", successMsg);
        map.put("fail", errorList);
        return BaseResponse.ok(map);
    }


    /**
     * 记录总条数
     * @param args
     * @return
     */
    public String getTotalMsg(Object... args){
        String msgCode = "共{}：{}条记录";
        return getMsg(msgCode, args);
    }

    /**
     * 成功信息
     * @param args
     * @return
     */
    public String getSuccessMsg(Object... args){
        String msgCode = "成功：{}条";
        return getMsg(msgCode, args);
    }

    /**
     * 失败总条数
     * @param args
     * @return
     */
    public String getErrCountMsg(Object... args){
        String msgCode = "失败：{}条";
        return getMsg(msgCode, args);
    }

    /**
     * 带操作类型
     * @param index
     * @param msg
     * @return
     */
    public String getErrMsg(Integer index, String msg){
        if(StrUtil.isBlank(msg)){
            return "";
        }
        return getHandleErrMsg(index, msg);
    }

    /**
     * 错误详细信息
     * @param args
     * @return
     */
    public String getHandleErrMsg(Object... args){
        String msgCode = "第{}行：{}";
        return getMsg(msgCode, args);
    }

    /**
     * message
     * @param msgCode
     * @param args
     * @return
     */
    public String getMsg(String msgCode, Object... args){
        return StrUtil.format(msgCode, args);
    }
}

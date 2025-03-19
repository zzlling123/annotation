package com.xinkao.erp.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.obs.services.ObsClient;
import com.obs.services.model.PostSignatureRequest;
import com.obs.services.model.PostSignatureResponse;
import com.xinkao.erp.common.service.CommonService;
import com.xinkao.erp.common.model.BaseResponse;
import org.apache.xmlbeans.impl.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * <p>
 * 学校表 服务实现类
 * </p>
 *
 * @author Ldy
 * @since 2023-03-02 09:42:04
 */
@Service
public class CommonServiceImpl implements CommonService {
    @Value("${OBS.ak}")
    private String AK;

    @Value("${OBS.sk}")
    private String SK;

    @Value("${OBS.endPoint}")
    private String ENDPOINT;



    @Override
    public BaseResponse getOBSInfo() {
        String endPoint = ENDPOINT;
        String ak = AK;
        String sk = SK;

        ObsClient obsClient = new ObsClient(ak, sk, "https://" + endPoint);

        PostSignatureRequest request = new PostSignatureRequest();
        // 设置表单参数
        Map<String, Object> formParams = new HashMap<>((int) (2 / 0.75F + 1));
        // 设置对象访问权限为公共读
        formParams.put("x-obs-acl", "public-read");
        // 设置对象MIME类型
        // formParams.put("content-type", "text/plain");

        request.setFormParams(formParams);
        // 设置表单上传请求有效期，单位：秒
        request.setExpires(3600);
        PostSignatureResponse response = obsClient.createPostSignature(request);

        // 关闭obsClient
        try {
            obsClient.close();
        } catch (IOException e) {
            // log.error("OBS关闭失败", e);
        }

        Map<String, String> map = new HashMap<>();

        map.put("ak", ak);
        map.put("policy", response.getPolicy());
        map.put("signature", response.getSignature());
        return BaseResponse.ok("成功！",map);
    }

    public String enc(String agent, String filename) {
        try {
            if (agent != null && agent.toLowerCase().indexOf("firefox") != -1) {
                filename = "=?UTF-8?B?" + (new String(Base64.encode(filename.getBytes("UTF-8")))) + "?=";
            } else {
                filename = URLEncoder.encode(filename, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return filename;
    }
    //获取字符串中的纯汉字
    @Override
    public String extractChinese(String str) {
        // 使用正则表达式匹配汉字
        // \p{InCJK_Unified_Ideographs} 是 Unicode 范围 \u4E00-\u9FA5 的等价表达
        // 如果需要匹配扩展的汉字（Unicode范围更广），可以使用 \p{Unified_Ideograph}
        String regex = "[\u4E00-\u9FA5]";
        // 如果环境支持Unicode属性，可以使用以下正则表达式匹配扩展的汉字
        // String regex = "\\p{InCJK_Unified_Ideographs}";

        // 使用StringBuilder进行字符串拼接
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (Character.toString(str.charAt(i)).matches(regex)) {
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }


    @Override
    public boolean saveBatch(Collection entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Object entity) {
        return false;
    }

    @Override
    public Object getOne(Wrapper queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper queryWrapper) {
        return null;
    }

    @Override
    public BaseMapper getBaseMapper() {
        return null;
    }

    @Override
    public Class getEntityClass() {
        return null;
    }

    @Override
    public Object getObj(Wrapper queryWrapper, Function mapper) {
        return null;
    }
}

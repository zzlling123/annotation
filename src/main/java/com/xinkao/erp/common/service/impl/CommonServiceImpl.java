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
        Map<String, Object> formParams = new HashMap<>((int) (2 / 0.75F + 1));
        formParams.put("x-obs-acl", "public-read");

        request.setFormParams(formParams);
        request.setExpires(3600);
        PostSignatureResponse response = obsClient.createPostSignature(request);

        try {
            obsClient.close();
        } catch (IOException e) {
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

    @Override
    public String extractChinese(String str) {



        String regex = "[\u4E00-\u9FA5]";



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

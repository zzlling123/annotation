package com.xinkao.erp.common.service;

import com.xinkao.erp.common.model.BaseResponse;
import org.springframework.stereotype.Service;

@Service
public interface CommonService extends BaseService {
    BaseResponse getOBSInfo();

    String enc(String agent, String filename);

    String extractChinese(String Str);
}

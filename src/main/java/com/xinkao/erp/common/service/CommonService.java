package com.xinkao.erp.common.service;

import com.xinkao.erp.common.model.BaseResponse;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学校表 服务类
 * </p>
 *
 * @author Ldy
 * @since 2023-03-02 09:42:04
 */
@Service
public interface CommonService extends BaseService {
    BaseResponse getOBSInfo();

    String enc(String agent, String filename);

    String extractChinese(String Str);
}

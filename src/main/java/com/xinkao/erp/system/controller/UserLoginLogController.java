package com.xinkao.erp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.controller.BaseController;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.login.entity.UserLoginLog;
import com.xinkao.erp.login.entity.UserOptLog;
import com.xinkao.erp.login.service.UserLoginLogService;
import com.xinkao.erp.system.model.query.UserLoginLogQuery;
import com.xinkao.erp.system.model.vo.UserLoginLogPageVo;
import com.xinkao.erp.system.model.vo.UserOptLogPageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user-login-log")
public class UserLoginLogController extends BaseController {

    @Autowired
    private UserLoginLogService userLoginLogService;

    
    @PrimaryDataSource
    @PostMapping("/page")
    public BaseResponse<Page<UserLoginLogPageVo>> page(@RequestBody UserLoginLogQuery query) {

        Pageable pageable = query.getPageInfo();
        Page<UserLoginLogPageVo> voPage = userLoginLogService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    







}

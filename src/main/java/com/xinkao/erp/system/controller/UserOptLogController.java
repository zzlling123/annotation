package com.xinkao.erp.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.model.BaseResponse;
import com.xinkao.erp.common.model.support.Pageable;
import com.xinkao.erp.login.entity.UserOptLog;
import com.xinkao.erp.login.service.UserOptLogService;
import com.xinkao.erp.system.model.query.UserOptLogQuery;
import com.xinkao.erp.system.model.vo.UserOptLogDetailsVo;
import com.xinkao.erp.system.model.vo.UserOptLogPageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user-opt-log")
public class UserOptLogController {

    @Autowired
    private UserOptLogService userOptLogService;

    
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页")
    public BaseResponse<Page<UserOptLogPageVo>> page(@RequestBody UserOptLogQuery query) {

        Pageable pageable = query.getPageInfo();
        Page<UserOptLogPageVo> voPage = userOptLogService.page(query, pageable);
        return BaseResponse.ok(voPage);
    }

    @PrimaryDataSource
    @PostMapping("/details")
    @ApiOperation("详情查询")
    public BaseResponse details(@RequestBody UserOptLog userOptLog) {

        UserOptLogDetailsVo voDetails = userOptLogService.details(userOptLog.getId());
        return BaseResponse.ok(voDetails);
    }

}

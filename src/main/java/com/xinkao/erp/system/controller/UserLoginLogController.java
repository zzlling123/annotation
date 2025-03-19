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

/**
 * <p>
 * 用户登录表 前端控制器
 * </p>
 *
 * @author Ldy
 * @since 2023-12-14 21:07:45
 */
@RestController
@RequestMapping("/user-login-log")
public class UserLoginLogController extends BaseController {

    @Autowired
    private UserLoginLogService userLoginLogService;

    /**
     * 分页
     *
     * @return
     */
    @PrimaryDataSource
    @PostMapping("/page")
    @ApiOperation("分页")
    public BaseResponse<Page<UserLoginLogPageVo>> page(@RequestBody UserLoginLogQuery query) {
        //获取用户信息
        Pageable pageable = query.getPageInfo();
        Page<UserLoginLogPageVo> voPage = userLoginLogService.page(query, pageable);
        //处理手机号脱敏
        for (UserLoginLogPageVo record : voPage.getRecords()) {
            // 处理手机号加密
            String hideMobile = hideMobile(record.getAccount());// hideMobile(user.getMobile());
            record.setAccount(hideMobile);
        }
        return BaseResponse.ok(voPage);
    }

    /**
     * 获取用户手机号
     *
     * @return
     */
    @PrimaryDataSource
    @PostMapping("/getMobileById")
    @ApiOperation("获取用户手机号")
    public BaseResponse getMobileById(@RequestBody UserLoginLog userLoginLog) {
        return BaseResponse.ok("成功",userLoginLogService.getById(userLoginLog.getId()).getAccount());
    }

}

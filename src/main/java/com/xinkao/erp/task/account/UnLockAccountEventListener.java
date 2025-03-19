package com.xinkao.erp.task.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.xinkao.erp.common.constant.XinKaoConstant;
import com.xinkao.erp.common.event.EverySecondEvent;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户自动解锁批处理
 **/
@Slf4j
@Component
public class UnLockAccountEventListener implements ApplicationListener<EverySecondEvent>{

    @Autowired
    private UserService accountService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onApplicationEvent(EverySecondEvent event) {
        // 查询需要解锁的用户
//        List<User> userList = accountService.getNeedUnLockedUser();
//        if (null != userList) {
//            for (User user : userList) {
//                // 对用户进行解锁
//                accountService.unLockUserById(user.getId());
//                // 将redis中的登录失败次数清空
//                String redisKey= XinKaoConstant.LOGIN_FAIL_KEY + ":" + user.getAccount();
//                redisUtil.deleteObject(redisKey);
//                log.debug("解锁用户，{}", user.getAccount());
//            }
//        }
    }
}

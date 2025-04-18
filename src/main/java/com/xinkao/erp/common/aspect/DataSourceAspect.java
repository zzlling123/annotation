package com.xinkao.erp.common.aspect;

import com.xinkao.erp.common.annotation.PrimaryDataSource;
import com.xinkao.erp.common.exception.AuthenticationException;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.util.HttpContextUtils;
import com.xinkao.erp.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DataSourceAspect {

    @Autowired
    private RedisUtil redisUtil;

    @Pointcut("@annotation(com.xinkao.erp.common.annotation.PrimaryDataSource)")
//    @Pointcut("execution(public * com.xinkao.electronicArchives.controller.*.*(..))")
    public void datasourcePointcut() {}

    /**
     * 前置操作，拦截具体请求，获取header里的数据源id，设置线程变量里，用于后续切换数据源
     */
    @Before("datasourcePointcut()")
    public void doBefore(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        String token = HttpContextUtils.getHttpServletRequest().getHeader("Authorization");
        if (StringUtils.isBlank(token)){
            throw new AuthenticationException("请重新登录！");
        }
        //获取当前缓存下的用户信息
        LoginUser loginUser = redisUtil.get(token);
        if (loginUser == null){
            throw new AuthenticationException("请重新登录！");
        }

    }


}

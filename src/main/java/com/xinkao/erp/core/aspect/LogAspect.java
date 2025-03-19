package com.xinkao.erp.core.aspect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xinkao.erp.common.util.RedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.xinkao.erp.common.annotation.Log;
import com.xinkao.erp.common.enums.system.LogStatus;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.util.ServletUtils;
import com.xinkao.erp.common.util.ip.IpRegionUtils;
import com.xinkao.erp.common.util.ip.IpUtils;
import com.xinkao.erp.login.entity.UserOptLog;
import com.xinkao.erp.system.service.AsyncService;
import com.xinkao.erp.user.entity.User;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作日志切面
 **/
@Slf4j
@Aspect
@Component
public class LogAspect {

    ThreadLocal<Long> beginTime = new ThreadLocal<>();

    @Resource
    protected RedisUtil redisUtil;

    @Pointcut("@annotation(com.xinkao.erp.common.annotation.Log)")
    public void logPointCut() {

    }

    /**
     *  环绕通知
     *
     * @param joinPoint 切点
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        beginTime.set(System.currentTimeMillis());
        //执行方法
        Object result = joinPoint.proceed();
        //执行时长(毫秒)
        long costTime = System.currentTimeMillis() - beginTime.get();
        log.debug("执行时长：{}", costTime);
        //保存日志
        handleLog(joinPoint,  beginTime.get(), costTime, null, result);
        beginTime.remove();
        return result;
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        //执行时长(毫秒)
        long costTime = System.currentTimeMillis() - beginTime.get();
        handleLog(joinPoint, beginTime.get(), costTime, e, null);
        beginTime.remove();
    }

    private void handleLog(final JoinPoint joinPoint, final long requestTime, final long costTime, final Exception e, Object jsonResult) {
        // 获得注解
        try {
            Log controllerLog = getAnnotationLog(joinPoint);
            if (null == controllerLog) {
                return;
            }
            UserOptLog userOptLog = new UserOptLog();
            // 获取操作者信息
            LoginUser loginUser = redisUtil.getInfoByToken();
            if (loginUser == null) {
                return;
            }
            String username = loginUser.getUser().getRealName();
            userOptLog.setAccount(username);
            User user = loginUser.getUser();
            String userId = user.getId().toString();
            String realName = user.getRealName();
            userOptLog.setUserId(userId);
            userOptLog.setRealName(realName);

            userOptLog.setStatus(LogStatus.SUCCESS.getCode());
            if (e != null) {
                userOptLog.setStatus(LogStatus.ERROR.getCode());
                userOptLog.setErrorMsg(ExceptionUtil.stacktraceToString(e));
            }
            if (controllerLog.isSaveResponseData()) {
                userOptLog.setResponseData(JSONObject.toJSONString(jsonResult));
            } else {
                // 不保存但是打印出来
                log.debug(JSONObject.toJSONString(jsonResult));
            }
            // 获取IP地址
            String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            userOptLog.setClientIp(ip);
            userOptLog.setIpRegion(IpRegionUtils.getRegion(ip));
            // 获取浏览器相关信息
            String userAgentStr = ServletUtils.getRequest().getHeader("User-Agent");
            UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
            userOptLog.setBrowser(userAgent.getBrowser().getName());
            userOptLog.setOs(userAgent.getOs().getName());
            userOptLog.setUserAgent(userAgentStr);

            // 请求的方法名
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            userOptLog.setMethod(className + "." + methodName + "()");
            // 请求方式
            userOptLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 请求路径
            userOptLog.setRequestUrl(ServletUtils.getRequest().getRequestURI());
            // 请求时间
            userOptLog.setRequestTime(DateUtil.date(requestTime).toString());
            // 耗时多久
            userOptLog.setCostTime(costTime);
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, userOptLog);
            log.debug("记录日志:{} ", userOptLog);
            // 保存数据库
            SpringUtil.getBean(AsyncService.class).recordOptLog(userOptLog);
        } catch (Exception ex) {
            // 记录本地异常日志
            log.error("==环绕通知异常==");
            log.error("异常信息:{}", ex.getMessage());
            ex.printStackTrace();
        }

    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     * @param joinPoint
     * @param controllerLog 日志注解
     * @param sysOperLog 操作日志
     */
    private void getControllerMethodDescription(JoinPoint joinPoint, Log controllerLog, UserOptLog sysOperLog) {
        // 设置操作类别
        sysOperLog.setOperationType(controllerLog.operationType().getName());
        // 设置标题
        sysOperLog.setContent(controllerLog.content());
        // 是否需要保存request，参数和值
        if (controllerLog.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, sysOperLog);
        }
    }

    /**
     * 获取请求的参数
     * @param joinPoint
     * @param sysOperLog
     */
    private void setRequestValue(JoinPoint joinPoint, UserOptLog sysOperLog) {
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            Object o = args[i];
            if(o instanceof ServletRequest || (o instanceof ServletResponse) || o instanceof MultipartFile
                || o.getClass().isAssignableFrom(MultipartFile[].class)){
                args[i] = o.toString();
            }
        }
        String params = JSONObject.toJSONString(args);
        sysOperLog.setRequestParam(params);
    }


    /**
     * 是否存在注解，如果存在就获取
     */
    private Log getAnnotationLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(Log.class);
        }
        return null;
    }
    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Iterator iter = collection.iterator(); iter.hasNext();) {
                return iter.next() instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest
            || o instanceof HttpServletResponse;
    }
}

package com.xinkao.erp.core.aspect;

import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.util.RedisUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.xinkao.erp.common.annotation.DataAuth;
import com.xinkao.erp.common.enums.busi.UserLevelEnum;
import com.xinkao.erp.common.exception.PermissionDeniedException;
import com.xinkao.erp.user.entity.User;
import com.xinkao.erp.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataAuthCheckInterceptor implements HandlerInterceptor {

	@Resource
	private UserService userService;
	@Resource
	protected RedisUtil redisUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.debug("============= 权限校验 =============");
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			DataAuth dataAuth = method.getAnnotation(DataAuth.class);
			if (null == dataAuth) {
				dataAuth = method.getDeclaringClass().getAnnotation(DataAuth.class);
			}
			if (dataAuth != null) {
				LoginUser loginUserAll = redisUtil.getInfoByToken();
				User loginUser = loginUserAll.getUser();
				UserLevelEnum[] authList = dataAuth.authList();
				boolean check = true;
				if(!check) {
                    throw new PermissionDeniedException("数据权限不足");
				}
			}
		}
		return true;
	}

}

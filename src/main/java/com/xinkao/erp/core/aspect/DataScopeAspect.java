package com.xinkao.erp.core.aspect;

import cn.hutool.core.util.StrUtil;
import com.xinkao.erp.common.annotation.DataScope;
import com.xinkao.erp.common.exception.AuthenticationException;
import com.xinkao.erp.common.exception.RoleErrorException;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.util.HttpContextUtils;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DataScopeAspect {

	@Autowired
	private RedisUtil redisUtils;

	@Pointcut("@annotation(com.xinkao.erp.common.annotation.PrimaryDataSource)")
	public void datasourcePointcut() {}

	/**
	 * 前置操作，拦截具体请求，验证token
	 */
	@Before("datasourcePointcut()")
	public void doBefore(JoinPoint point) {
		//获取当前缓存下的用户信息
		if (StrUtil.isBlank(HttpContextUtils.getHttpServletRequest().getHeader("Authorization"))){
			throw new AuthenticationException("登录token不可为空！");
		}
		LoginUser loginUser = redisUtils.getInfoByToken();
		if (loginUser == null){
			throw new AuthenticationException("请重新登录！");
		}
		User user = loginUser.getUser();
		if (user.getState() == 0){
			throw new AuthenticationException("账号已停用！");
		}
		handleDataScope(point,loginUser.getUser().getRoleId());
	}
	protected void handleDataScope(final JoinPoint joinPoint,Integer roleId) {
		// 获得注解
		DataScope controllerDataScope = getAnnotationLog(joinPoint);
		if (controllerDataScope == null) {
			return;
		}
		String role = controllerDataScope.role();
		//验证用户权限
		if (StrUtil.isNotBlank(role)){
			List<String> roleArray = Arrays.asList(role.split(","));
			boolean flag = false;
			if (roleArray.contains(roleId.toString())){
				flag = true;
			}
			if (!flag){
				throw new RoleErrorException("您的权限不足！");
			}
		}
	}
	/**
	 * 是否存在注解，如果存在就获取
	 */
	private DataScope getAnnotationLog(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();

		if (method != null) {
			return method.getAnnotation(DataScope.class);
		}
		return null;
	}

	/**
	 * 后置操作，设置回默认的数据源id
	 */
//	@AfterReturning("datasourcePointcut()")
//	public void doAfter() {
//		DynamicDataSourceHolder.clearDataSource();
//	}

}

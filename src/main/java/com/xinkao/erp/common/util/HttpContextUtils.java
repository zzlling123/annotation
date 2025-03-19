

package com.xinkao.erp.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author lililiang
 * @Description
 * @createTime 2021/8/20 9:19
 */
@Slf4j
public class HttpContextUtils {

	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String DELETE = "DELETE";

	public static HttpServletRequest getHttpServletRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	public static String getDomain(){
		HttpServletRequest request = getHttpServletRequest();
		StringBuffer url = request.getRequestURL();
		return url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
	}

	public static String getOrigin(){
		HttpServletRequest request = getHttpServletRequest();
		return request.getHeader("Origin");
	}

	public static Map getParameterMap(){
		HttpServletRequest request = getHttpServletRequest();
		return request.getParameterMap();
	}

	public static String getPathParam() {
		HttpServletRequest request = getHttpServletRequest();
		String requestURI = request.getRequestURI();
		return requestURI.substring(requestURI.lastIndexOf("/") + 1);
	}

	public static String getIpAddrByRequest(HttpServletRequest request) {
		String ip = null;
		try {
			ip = request.getHeader("x-forwarded-for");
			if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StrUtil.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {
			log.error("IPUtils ERROR ", e);
		}

		return ip;
	}
	/**
	 * 获取ip
	 * @Author lililiang
	 * @Date 2021/7/7 14:42
	 */
	public static String getIpAddr() {
		getHttpServletRequest().getMethod();
		return getIpAddrByRequest(getHttpServletRequest());
	}

	/**
	 * 获取请求方法
	 * @Author lililiang
	 * @Date 2021/7/7 14:42
	 */
	public static String getMethod() {
		return getHttpServletRequest().getMethod();
	}

	/**
	 * 获取UserAgent
	 * @Author lililiang
	 * @Date 2021/7/7 14:42
	 */
	public static UserAgent getUserAgent() {
		return UserAgentUtil.parse(getHttpServletRequest().getHeader("USER-AGENT"));
	}
	/**
	 * 获取操作平台
	 * @Author lililiang
	 * @Date 2021/7/7 14:42
	 */
	public static String getPlatform() {
		return getUserAgent().getPlatform().toString();
	}

	/**
	 * 获取浏览器
	 * @Author lililiang
	 * @Date 2021/7/7 14:42
	 */
	public static String getBrowser() {
		return getUserAgent().getBrowser().toString();
	}

}

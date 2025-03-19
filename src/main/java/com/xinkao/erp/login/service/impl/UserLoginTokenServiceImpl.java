package com.xinkao.erp.login.service.impl;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.xinkao.erp.common.config.properties.XinKaoProperties;
import com.xinkao.erp.common.constant.XinKaoConstant;
import com.xinkao.erp.common.model.LoginUser;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.common.util.RedisUtil;
import com.xinkao.erp.common.util.ServletUtils;
import com.xinkao.erp.common.util.ip.IpRegionUtils;
import com.xinkao.erp.common.util.ip.IpUtils;
import com.xinkao.erp.login.entity.UserLoginToken;
import com.xinkao.erp.login.mapper.UserLoginTokenMapper;
import com.xinkao.erp.login.service.UserLoginTokenService;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
/**
 * <p>
 * 管理端-登录token(定时删除) 服务实现类
 * </p>
 *
 * @author hanhys
 * @since 2023-08-03 14:18:36
 */
@Service
@Slf4j
public class UserLoginTokenServiceImpl extends BaseServiceImpl<UserLoginTokenMapper, UserLoginToken> implements UserLoginTokenService {

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    /**
     * 默认的登录时间 24小时
     */
    private static final Long MILLIS_MINUTE_TEN = 24 * 60 * 60 * 1000L;

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private XinKaoProperties xinKaoProperties;
    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     * @param request
     */
    @Override
    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (StrUtil.isNotEmpty(token)) {
            Claims claims = parseToken(token);
            if (null != claims) {
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(XinKaoConstant.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                LoginUser user = redisUtil.get(userKey);
                return user;
            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    @Override
    public void setLoginUser(LoginUser loginUser) {
        if (null != loginUser && StrUtil.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    @Override
    public void delLoginUser(String token) {
        if (Validator.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            redisUtil.deleteObject(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    @Override
    public String createToken(LoginUser loginUser) {
        String token = IdUtil.fastUUID();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(XinKaoConstant.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    @Override
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    private void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTs(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTs() + xinKaoProperties.getToken().getExpireTime() * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        xinKaoProperties.getToken().getExpireTime();
        redisUtil.set(userKey, loginUser, xinKaoProperties.getToken().getExpireTime(), TimeUnit.MINUTES);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    private void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgentUtil.parse(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        loginUser.setIpAddr(ip);
        loginUser.setLoginLocation(IpRegionUtils.getRegion(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOs().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        long currentTimeMillis = System.currentTimeMillis();
        PrivateKey privateKey = null;
        try {
            byte[] keyBytes = Base64.decode(xinKaoProperties.getToken().getRsaPrivateKey());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory =  KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (Exception ex) {
            log.error("jwt加密过程发生异常，异常信息如下：{}", ex.getMessage());
            throw new JwtException("token加密失败");
        }
        String token = Jwts.builder()
            .setSubject("xinkao-developer")
            // 签发时间
            .setIssuedAt(new Date(currentTimeMillis))
            // 签发人
            .setIssuer(xinKaoProperties.getToken().getIssuer())
            // 数据
            .setClaims(claims)
            // 加密算法
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        Claims claims = null;
        try {
            byte[] keyBytes = Base64.decode(xinKaoProperties.getToken().getRsaPublicKey());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            claims = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            // 不受信任的JWT
            log.error("当前jwtToken不受信任，或被串改：{}", token);
//            throw new JwtException("当前jwtToken不受信任，或被串改");
        } catch (Exception ex) {
            log.error("jwt解密过程发生异常，异常信息如下：{}", ex.getMessage());
        }
        return claims;
//        return Jwts.parser()
//            .setSigningKey(xinKaoProperties.getToken().getSecret())
//            .parseClaimsJws(token)
//            .getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从请求中获取token
     * @param request
     * @return
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 先从header中获取
        String accessToken = request.getHeader(XinKaoConstant.ACCESS_TOKEN);
        // 从参数中获取
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter(XinKaoConstant.ACCESS_TOKEN);
            log.debug("Got access key from parameter: [{}: {}]", XinKaoConstant.ACCESS_TOKEN, accessToken);
        } else if (StringUtils.isBlank(accessToken)) {
            accessToken = (String) request.getAttribute(XinKaoConstant.ACCESS_TOKEN);
            log.debug("Got access key from attribute: [{}: {}]", XinKaoConstant.ACCESS_TOKEN, accessToken);
        } else {
            log.debug("Got access key from header: [{}: {}]", XinKaoConstant.ACCESS_TOKEN, accessToken);
        }
        return accessToken;
    }

    private String getTokenKey(String uuid) {
        return XinKaoConstant.LOGIN_TOKEN_KEY + uuid;
    }
    
}

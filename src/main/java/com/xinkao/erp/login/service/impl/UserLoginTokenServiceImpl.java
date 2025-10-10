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
@Service
@Slf4j
public class UserLoginTokenServiceImpl extends BaseServiceImpl<UserLoginTokenMapper, UserLoginToken> implements UserLoginTokenService {

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    private static final Long MILLIS_MINUTE_TEN = 24 * 60 * 60 * 1000L;

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private XinKaoProperties xinKaoProperties;
    @Override
    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (StrUtil.isNotEmpty(token)) {
            Claims claims = parseToken(token);
            if (null != claims) {

                String uuid = (String) claims.get(XinKaoConstant.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                LoginUser user = redisUtil.get(userKey);
                return user;
            }
        }
        return null;
    }




    @Override
    public void setLoginUser(LoginUser loginUser) {
        if (null != loginUser && StrUtil.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }




    @Override
    public void delLoginUser(String token) {
        if (Validator.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            redisUtil.deleteObject(userKey);
        }
    }


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

    @Override
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    private void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTs(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTs() + xinKaoProperties.getToken().getExpireTime() * MILLIS_MINUTE);

        String userKey = getTokenKey(loginUser.getToken());
        xinKaoProperties.getToken().getExpireTime();
        redisUtil.set(userKey, loginUser, xinKaoProperties.getToken().getExpireTime(), TimeUnit.MINUTES);
    }


    private void setUserAgent(LoginUser loginUser) {
        UserAgent userAgent = UserAgentUtil.parse(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        loginUser.setIpAddr(ip);
        loginUser.setLoginLocation(IpRegionUtils.getRegion(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOs().getName());
    }


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

            .setIssuedAt(new Date(currentTimeMillis))

            .setIssuer(xinKaoProperties.getToken().getIssuer())

            .setClaims(claims)

            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact();
        return token;
    }


    private Claims parseToken(String token) {
        Claims claims = null;
        try {
            byte[] keyBytes = Base64.decode(xinKaoProperties.getToken().getRsaPublicKey());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            claims = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
        } catch (JwtException e) {

            log.error("当前jwtToken不受信任，或被串改：{}", token);

        } catch (Exception ex) {
            log.error("jwt解密过程发生异常，异常信息如下：{}", ex.getMessage());
        }
        return claims;




    }


    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    private String getTokenFromRequest(HttpServletRequest request) {

        String accessToken = request.getHeader(XinKaoConstant.ACCESS_TOKEN);

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

package com.example.springsecurity.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: JJWT配置
 * @author: Zhaotianyi
 * @time: 2021/11/17 11:29
 */
@Component
@ConfigurationProperties(prefix = "jjwt.security")
public class JJWTProperties {
    // JWT加密密码
    private String secret;
    // base64加密密码 (与其上面二选一)
    private String base64Secret;
    // token过期时间
    private long tokenValidityInSeconds;
    // token出于记住时过期时间
    private long tokenValidityInSecondsForRememberMe;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getBase64Secret() {
        return base64Secret;
    }

    public void setBase64Secret(String base64Secret) {
        this.base64Secret = base64Secret;
    }

    public long getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }

    public void setTokenValidityInSeconds(long tokenValidityInSeconds) {
        this.tokenValidityInSeconds = tokenValidityInSeconds;
    }

    public long getTokenValidityInSecondsForRememberMe() {
        return tokenValidityInSecondsForRememberMe;
    }

    public void setTokenValidityInSecondsForRememberMe(long tokenValidityInSecondsForRememberMe) {
        this.tokenValidityInSecondsForRememberMe = tokenValidityInSecondsForRememberMe;
    }
}

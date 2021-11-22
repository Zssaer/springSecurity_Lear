package com.example.springsecurity.component;

import com.example.springsecurity.mapper.SimpleUserMapper;
import com.example.springsecurity.model.SimpleUser;
import com.example.springsecurity.utils.RedisUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

/**
 * @description: JWT认证工具类
 * @author: Zhaotianyi
 * @time: 2021/11/17 11:22
 */
@Component
public class JWTProvider {
    /**
     * 私钥
     */
    private Key key;
    /**
     * 有效时间
     */
    private long tokenValidityInMilliseconds;
    /**
     * 记住我有效时间
     */
    private long tokenValidityInMillisecondsForRememberMe;
    /**
     * jwt配置参数
     */
    @Autowired
    private JJWTProperties jjwtProperties;

    @Autowired
    private SimpleUserMapper simpleUserMapper;

    @Resource
    private RedisUtils redisUtils;


    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes;
        String secret = jjwtProperties.getSecret();
        if (StringUtils.hasText(secret)) {
            System.out.println("Warning: the JWT key used is not Base64-encoded. " +
                    "We recommend using the `jhipster.security.authentication.jwt.base64-secret` key for optimum security.");
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        } else {
            System.out.println("Using a Base64-encoded JWT secret key");
            keyBytes = Decoders.BASE64.decode(jjwtProperties.getBase64Secret());
        }
        // 使用mac-sha算法的密钥
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds =
                1000 * jjwtProperties.getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe =
                1000 * jjwtProperties.getTokenValidityInSecondsForRememberMe();
    }

    /**
     * 根据用户信息创建Token
     *
     * @param userDetails 用户信息
     * @param rememberMe  是否记住
     * @return Token
     */
    public String createToken(UserDetails userDetails, boolean rememberMe) {
        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        Map<String, Object> map = new HashMap<>(16);
        map.put("sub", userDetails.getUsername());
        String token = Jwts.builder()
                // 添加body
                .setClaims(map)
                // 指定摘要算法
                .signWith(key, SignatureAlgorithm.HS512)
                // 设置有效时间
                .setExpiration(validity)
                .compact();
        // 将其存入Redis中持久化
        redisUtils.set(userDetails.getUsername(), token, jjwtProperties.getTokenValidityInSeconds());
        return token;
    }

    /**
     * 根据身份认证创建Token
     *
     * @param authentication 身份认证类
     * @param rememberMe     是否记住
     * @return Token
     */
    public String createToken(Authentication authentication, boolean rememberMe) {
        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }
        Example example = new Example(SimpleUser.class);
        example.createCriteria().andEqualTo("userName", authentication.getName());
        List<SimpleUser> list = simpleUserMapper.selectByExample(example);
        Map<String, Object> map = new HashMap<>(16);
        map.put("sub", authentication.getName());
        map.put("user", list.get(0));
        return Jwts.builder()
                // 添加body
                .setClaims(map)
                // 指定摘要算法
                .signWith(key, SignatureAlgorithm.HS512)
                // 设置有效时间
                .setExpiration(validity)
                .compact();
    }

    /**
     * 根据Token获取身份认证
     *
     * @param token Token串
     * @return 身份认证类
     * @throws ExpiredJwtException   Token超时
     * @throws MalformedJwtException Token错误
     */
    public Authentication getAuthentication(String token) throws ExpiredJwtException, MalformedJwtException {
        // 根据token获取body
        Claims claims;
        claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody();

        String jwt = (String) redisUtils.get(claims.getSubject());
        // 如果其Redis中的jwt 与其header中的jwt不一致,代表其账号被二次登录,强制下线
        if (!token.equals(jwt)) {
            throw new MalformedJwtException("该账号被二次登录,请重新登录!");
        }

        SimpleUser principal;
        Collection<? extends GrantedAuthority> authorities;

        Example example = new Example(SimpleUser.class);
        example.createCriteria().andEqualTo("userName", claims.getSubject());
        principal = simpleUserMapper.selectByExample(example).get(0);
        authorities = principal.getAuthorities();
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}

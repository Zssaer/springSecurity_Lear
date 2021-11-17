package com.example.springsecurity.component;

import com.example.springsecurity.mapper.SimpleUserMapper;
import com.example.springsecurity.model.SimpleUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

/**
 * @description: TODO
 * @author: Zhaotianyi
 * @time: 2021/11/17 11:22
 */
@Component
public class JWTProvider {
    private Key key;	// 私钥
    private long tokenValidityInMilliseconds; // 有效时间
    private long tokenValidityInMillisecondsForRememberMe; // 记住我有效时间

    @Autowired
    private SimpleUserMapper simpleUserMapper;
    @Autowired
    private JJWTProperties jjwtProperties; // jwt配置参数

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
        this.key = Keys.hmacShaKeyFor(keyBytes); // 使用mac-sha算法的密钥
        this.tokenValidityInMilliseconds =
                1000 * jjwtProperties.getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe =
                1000 * jjwtProperties.getTokenValidityInSecondsForRememberMe();
    }

    public String createToken(UserDetails userDetails, boolean rememberMe) {
        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        Map<String ,Object> map = new HashMap<>();
        map.put("sub",userDetails.getUsername());
        return Jwts.builder()
                .setClaims(map) // 添加body
                .signWith(key, SignatureAlgorithm.HS512) // 指定摘要算法
                .setExpiration(validity) // 设置有效时间
                .compact();
    }


    public String createToken(Authentication authentication, boolean rememberMe) {
        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }
        Example example = new Example(SimpleUser.class);
        example.createCriteria().andEqualTo("userName",authentication.getName());
        List<SimpleUser> list = simpleUserMapper.selectByExample(example);
        Map<String ,Object> map = new HashMap<>();
        map.put("sub",authentication.getName());
        map.put("user",list.get(0));
        return Jwts.builder()
                .setClaims(map) // 添加body
                .signWith(key, SignatureAlgorithm.HS512) // 指定摘要算法
                .setExpiration(validity) // 设置有效时间
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody(); // 根据token获取body
        SimpleUser principal;
        Collection<? extends GrantedAuthority> authorities;

        Example example = new Example(SimpleUser.class);
        example.createCriteria().andEqualTo("userName",claims.getSubject());
        principal = simpleUserMapper.selectByExample(example).get(0);
        authorities = principal.getAuthorities();
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}

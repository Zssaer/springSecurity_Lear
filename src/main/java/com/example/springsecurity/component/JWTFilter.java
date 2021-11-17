package com.example.springsecurity.component;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @description: JWT过滤器
 * 用于jwt获取authentication,来传输给SpringSecurity,通过认证
 * @author: Zhaotianyi
 * @time: 2021/11/17 11:48
 */
public class JWTFilter extends GenericFilterBean {
    private final static String HEADER_AUTH_NAME = "auth";

    private JWTProvider jwtProvider;

    public JWTFilter(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String authToken = httpServletRequest.getHeader(HEADER_AUTH_NAME);
        if (StringUtils.hasText(authToken)) {
            try {
                // 从自定义JWT中中解析用户
                Authentication authentication = this.jwtProvider.getAuthentication(authToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (MalformedJwtException exception) {

            }
        }
        // 调用后续的Filter,如果上面的代码逻辑未能复原“session”，SecurityContext中没有信息，后面的流程还是需要"需要登录"
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

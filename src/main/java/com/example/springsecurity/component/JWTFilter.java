package com.example.springsecurity.component;

import com.alibaba.fastjson.JSON;
import com.example.springsecurity.exception.ServiceException;
import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;
import io.jsonwebtoken.ExpiredJwtException;
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

    private final JWTProvider jwtProvider;

    public JWTFilter(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, ServiceException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String authToken = httpServletRequest.getHeader(HEADER_AUTH_NAME);
        if (StringUtils.hasText(authToken)) {
            // 从自定义JWT中中解析用户
            Authentication authentication = null;
            // 解析头部auth TOKEN,过期拦截
            try {
                authentication = this.jwtProvider.getAuthentication(authToken);
            } catch (ExpiredJwtException e) {
                Result result = ResultBuilder.failResult("登录身份过期，请重新登录!");
                servletResponse.setContentType("application/json;charset=utf-8");
                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.getWriter().write(JSON.toJSONString(result));
                return;
            } catch (MalformedJwtException e) {
                Result result = ResultBuilder.failResult(e.getMessage());
                servletResponse.setContentType("application/json;charset=utf-8");
                servletResponse.setCharacterEncoding("UTF-8");
                servletResponse.getWriter().write(JSON.toJSONString(result));
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 调用后续的Filter,如果上面的代码逻辑未能复原“session”，SecurityContext中没有信息，后面的流程还是需要"需要登录"
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ServiceException e) {
            Result result = ResultBuilder.failResult(e.getMessage());
            servletResponse.setContentType("application/json;charset=utf-8");
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.getWriter().write(JSON.toJSONString(result));
        }
    }
}

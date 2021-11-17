package com.example.springsecurity.component;

import com.alibaba.fastjson.JSON;
import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;
import com.example.springsecurity.result.ResultCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 未授权拦截或者Token错误
 * @author: Zhaotianyi
 * @time: 2021/11/17 15:11
 */
@Component
public class UnAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Result result = ResultBuilder.failResult(ResultCode.UNAUTHORIZED, "您未登录，无法访问。");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(result));
        response.getWriter().flush();
    }
}

package com.example.springsecurity.component;

import com.alibaba.fastjson.JSON;
import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;
import com.example.springsecurity.result.ResultCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 重写新的权限拒绝访问拦截器
 * @author: Zhaotianyi
 * @time: 2021/11/17 14:24
 */
@Component
public class RewriteAccessDenyFilter implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Result result = ResultBuilder.failResult(ResultCode.NOPERMISSION, "当前用户无权限!");
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSON.toJSONString(result));
        response.getWriter().flush();
    }
}

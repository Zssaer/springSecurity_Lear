package com.example.springsecurity.component;

import com.example.springsecurity.exception.ServiceException;
import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;
import com.example.springsecurity.service.ImgValidService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @description: 自定义验证逻辑提供类
 * @author: Zhaotianyi
 * @time: 2021/11/22 10:10
 */
public class MyAuthenticationProvider extends DaoAuthenticationProvider {
    @Resource
    private ImgValidService imgValidService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException,ServiceException {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 获取请求中的验证码信息
        String validKey = req.getParameter("validKey");
        String verifyCode = req.getParameter("verifyCode");
        try {
            String cacheVerifyCode = imgValidService.get(validKey);
            // 进行判断验证码是否正确
            if (!verifyCode.toLowerCase().equals(cacheVerifyCode)){
                Result result = ResultBuilder.failResult("身份错误，请重新登录!");
                throw new ServiceException("验证码输入错误");
            }
        } catch (Exception e) {
            throw new ServiceException("验证码状态错误");
        }
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}

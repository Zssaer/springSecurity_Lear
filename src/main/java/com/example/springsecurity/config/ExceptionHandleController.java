package com.example.springsecurity.config;


import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;
import com.example.springsecurity.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @description: 自定义错误拦截
 * @author: Zhaotianyi
 * @time: 2021/5/6 16:49
 */
@RestControllerAdvice
public class ExceptionHandleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandleController.class);

    /**
     * 错误拦截
     */
    @ExceptionHandler(Exception.class)
    public Result ExceptionHandle(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResultBuilder.failResult("后台处理错误!请查看控制台错误信息!");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result accessDeniedExceptionHandle(AccessDeniedException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResultBuilder.failResult("用户认证失败!请重新登录。");
    }


}

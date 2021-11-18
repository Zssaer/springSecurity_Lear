package com.example.springsecurity.config;


import com.example.springsecurity.exception.ServiceException;
import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * 服务错误拦截
     */
    @ExceptionHandler(ServiceException.class)
    public Result ServiceExceptionHandle(ServiceException ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ResultBuilder.failResult(ex.getMessage());
    }


}

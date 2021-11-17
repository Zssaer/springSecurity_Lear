package com.example.springsecurity.controller;

import com.example.springsecurity.component.CustomUserDetailsService;
import com.example.springsecurity.mapper.SimpleUserMapper;
import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: TODO
 * @author: Zhaotianyi
 * @time: 2021/11/17 16:07
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @Resource
    private CustomUserDetailsService customUserDetailsService;


    @GetMapping
    public Result get() {
        return ResultBuilder.successResult("NICE!");
    }

    @PostMapping
    public Result login(String userName, String password) {
        String token = customUserDetailsService.login(userName, password);
        return ResultBuilder.successResult(token);
    }

}

package com.example.springsecurity.controller;

import com.example.springsecurity.component.CustomUserDetailsService;
import com.example.springsecurity.mapper.SimpleUserMapper;
import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @description: TODO
 * @author: Zhaotianyi
 * @time: 2021/11/17 16:07
 */
@RestController
@RequestMapping("/2login")
public class LoginController {
    @Resource
    private CustomUserDetailsService customUserDetailsService;


    @GetMapping
    public Result get() {
        return ResultBuilder.successResult("NICE!");
    }

    @PostMapping
    public Result login(@RequestParam(value = "userName") String userName, @RequestParam(value = "password") String password) {
        String token = customUserDetailsService.login(userName, password);
        return ResultBuilder.successResult(token);
    }

}

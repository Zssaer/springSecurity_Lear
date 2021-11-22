package com.example.springsecurity.controller;

import com.example.springsecurity.model.SimpleUser;
import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: TODO
 * @author: Zhaotianyi
 * @time: 2021/11/15 17:27
 */
@RestController
@RequestMapping("/hello")
public class TestController {
    @GetMapping
    public String hello() {
        return "hello";
    }

    @GetMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public Result info(){
        String userDetails = null;
        String userName =null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            userName = ((UserDetails)principal).getUsername();
        }else {
            SimpleUser user = (SimpleUser)principal;
            userName = user.getUserName();
        }
        String details = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        HashMap<String, String> result = new HashMap<>(16);
        result.put("UserName",userName);
        result.put("Details",details);

        return ResultBuilder.successResult(result);
    }

}

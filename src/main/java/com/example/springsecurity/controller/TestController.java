package com.example.springsecurity.controller;

import com.example.springsecurity.model.SimpleUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String info(){
        String userDetails = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            userDetails = ((UserDetails)principal).getUsername();
        }else {
            SimpleUser user = (SimpleUser)principal;
            return user.getUserName();
        }
        return userDetails;
    }

}

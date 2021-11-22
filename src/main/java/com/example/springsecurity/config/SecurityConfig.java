package com.example.springsecurity.config;

import com.example.springsecurity.component.*;
import com.example.springsecurity.result.ResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;


/**
 * @description: TODO
 * @author: Zhaotianyi
 * @time: 2021/11/15 17:31
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JWTProvider jwtProvider;
    @Autowired
    private RewriteAccessDenyFilter rewriteAccessDenyFilter;
    @Autowired
    private UnAuthenticationEntryPoint unAuthenticationEntryPoint;

    /**
     * 自定义密码认证
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MD5PasswordEncoder();
    }

    /**
     * 自定义AuthenticationProvider
     */
    @Bean
    public MyAuthenticationProvider myAuthenticationProvider() {
        MyAuthenticationProvider myAuthenticationProvider = new MyAuthenticationProvider();
        // 设置密码认证
        myAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        // 设置用户信息查询服务
        myAuthenticationProvider.setUserDetailsService(userDetailsService);
        return myAuthenticationProvider;
    }

    /**
     * 自定义AuthenticationManager
     */
    @Override
    protected AuthenticationManager authenticationManager() {
        // 加载自定义自定义AuthenticationProvider
        ProviderManager manager = new ProviderManager(Arrays.asList(myAuthenticationProvider()));
        return manager;
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        // 设置自定义的userDetailsService
//        auth.userDetailsService(userDetailsService)
//                //设置密文解密方式
//                .passwordEncoder(passwordEncoder());
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                //设置spring security为无状态认证
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //添加需要授权控制的页面
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                // 其他页面不需要指定权限(注意:但也就需要登录认证)
                .and()
                .formLogin()
//                .loginProcessingUrl("/login")
//                .successHandler((request, response, authentication) -> {
//                    response.setContentType("application/json;charset=utf-8");
//                    PrintWriter writer = response.getWriter();
//                    writer.println(ResultBuilder.successResult(jwtProvider.createToken(authentication, true)));
//                    writer.flush();
//                    writer.close();
//                })
//                .failureHandler((HttpServletRequest request, HttpServletResponse response,
//                                 AuthenticationException exception) -> {
//                    response.setContentType("application/json;charset=utf-8");
//                    PrintWriter writer = response.getWriter();
//                    writer.println(ResultBuilder.successResult("用户名或者密码错误!"));
//                    writer.flush();
//                    writer.close();
//                })
                .and()
                .httpBasic()
                // 添加登出处理器
                .and()
                .csrf().disable()
                // 登录添加过滤器,先提前主要获取头部JWTToken来通过SpringSecurity
                // 头部没有JWT Token则继续执行UsernamePasswordAuthenticationFilter过滤器。
                .addFilterBefore(new JWTFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                //添加自定义未授权和未登录结果返回
                .exceptionHandling()
                .accessDeniedHandler(rewriteAccessDenyFilter)
                .authenticationEntryPoint(unAuthenticationEntryPoint);


    }


}

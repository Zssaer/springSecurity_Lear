package com.example.springsecurity.config;

import com.example.springsecurity.component.JWTFilter;
import com.example.springsecurity.component.JWTProvider;
import com.example.springsecurity.component.RewriteAccessDenyFilter;
import com.example.springsecurity.component.UnAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

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


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)// 设置自定义的userDetailsService
                .passwordEncoder(passwordEncoder());
    }

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



    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();// 使用不使用加密算法保持密码
        //return new BCryptPasswordEncoder();
    }

}

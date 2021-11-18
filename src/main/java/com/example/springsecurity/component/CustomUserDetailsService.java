package com.example.springsecurity.component;

import com.example.springsecurity.exception.ServiceException;
import com.example.springsecurity.mapper.SimpleUserMapper;
import com.example.springsecurity.model.SimpleUser;
import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @description: TODO
 * @author: Zhaotianyi
 * @time: 2021/11/17 10:20
 */
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Resource
    private SimpleUserMapper simpleUserMapper;
    @Autowired
    private JWTProvider jwtProvider;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Example example = new Example(SimpleUser.class);
        example.createCriteria().andEqualTo("userName", login);
        // 1. 查询用户
        List<SimpleUser> userFromDatabase = simpleUserMapper.selectByExample(example);
        if (userFromDatabase.isEmpty()) {
            System.out.println("User  was not found in db");
            //这里找不到必须抛异常
            throw new UsernameNotFoundException("User " + login + " was not found in db");
        }

        // 2. 设置角色
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userFromDatabase.get(0).getRole());
        grantedAuthorities.add(grantedAuthority);
        String encodedPassword = userFromDatabase.get(0).getUserPassword();
        String saltHash = userFromDatabase.get(0).getSalt();
        String allEncoded = encodedPassword + "|" + saltHash;
        return new User(login, allEncoded, grantedAuthorities);
    }

    /**
     * 登录操作
     * @param loginName 登录名
     * @param password  密码
     * @return 用户登录Token
     */
    public String login(String loginName, String password) {
        if ("".equals(loginName) || loginName == null) {
            return "登陆失败!用户名不能为空!";
        }
        Example example = new Example(SimpleUser.class);
        example.createCriteria().andEqualTo("userName", loginName);
        List<SimpleUser> list = simpleUserMapper.selectByExample(example);
        if (list.isEmpty()) {
            return "登陆失败!用户不存在。";
        }
        SimpleUser user = list.get(0);
        String encodedPassword = user.getUserPassword();
        String saltHash = user.getSalt();
        String allEncoded = encodedPassword + "|" + saltHash;
        MD5PasswordEncoder encoder = new MD5PasswordEncoder();
        // 判断密码正确性
        if (!encoder.matches(password,allEncoded)) {
            return "登陆失败!用户密码错误。";
        }

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole());
        grantedAuthorities.add(grantedAuthority);

        User userDetails = new User(loginName, password, grantedAuthorities);
        return jwtProvider.createToken(userDetails, false);
    }
}

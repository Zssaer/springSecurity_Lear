package com.example.springsecurity.component;

import com.example.springsecurity.utils.MD5Util;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * @description: SpringSecurity自定义的Md5加密认证
 * @author: Zhaotianyi
 * @time: 2021/11/18 14:49
 */
public class MD5PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        return "";
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        if ("".equals(s)) {
            return false;
        }
        // 分割传来的密文,其中前者为加密密文,后者为带盐hash
        String[] strings = s.split("\\|");
        String encodedPassword = strings[0];
        String saltHash = strings[1];
        // 判断是否密文与加密密文是否相同
        return MD5Util.matchesHashWithSalt(charSequence.toString(), encodedPassword, saltHash);
    }
}

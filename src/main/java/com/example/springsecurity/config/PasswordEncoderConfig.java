package com.example.springsecurity.config;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @description: TODO
 * @author: Zhaotianyi
 * @time: 2021/11/17 10:08
 */
public class PasswordEncoderConfig implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        return charSequence.toString();
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return s.equals(charSequence.toString());
    }
}

package com.example.springsecurity.service;

import com.example.springsecurity.utils.EhcacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 图像缓存获取
 * @author: Zhaotianyi
 * @time: 2021/5/10 16:35
 */
@Service
public class ImgValidService {
    private long expireSeconds = 300;

    @Autowired
    private EhcacheUtils ehcacheUtils;

    public void set(String key, Object value) {
        this.ehcacheUtils.put(key, value, (int) expireSeconds);
    }


    public String get(String key) throws Exception {
        return ehcacheUtils.get(key, String.class);
    }

    public void remove(String key) throws Exception {
        ehcacheUtils.remove(key);
    }

}

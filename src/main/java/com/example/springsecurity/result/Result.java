package com.example.springsecurity.result;

import com.alibaba.fastjson.JSON;

/**
 * 统一API Result封装
 */
public class Result {
    private int code;
    private String message;
    private Object data;

    public Result setCode(ResultCode resultCode) {
        this.code = resultCode.code();
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

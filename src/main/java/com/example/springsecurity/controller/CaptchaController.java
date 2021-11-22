package com.example.springsecurity.controller;

import com.example.springsecurity.controller.response.CaptchaResp;
import com.example.springsecurity.result.Result;
import com.example.springsecurity.result.ResultBuilder;
import com.example.springsecurity.service.ImgValidService;
import com.example.springsecurity.utils.MD5Util;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: TODO
 * @author: Zhaotianyi
 * @time: 2021/5/10 15:55
 */
@Controller
public class CaptchaController {
    @Autowired
    private ImgValidService imgValidService;

    @ResponseBody
    @GetMapping("/captcha")
    public Result captcha() throws Exception {
        SpecCaptcha specCaptcha = new SpecCaptcha(128, 48, 5);
        String verCode = specCaptcha.text().toLowerCase();
        String MD5verCode = MD5Util.generateMd5(verCode);

        imgValidService.set(MD5verCode, verCode);
        return ResultBuilder.successResult(new CaptchaResp(specCaptcha.toBase64(), MD5verCode));
    }
}

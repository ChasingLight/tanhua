package com.tanhua.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.tanhua.admin.service.AdminService;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.vo.AdminVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
public class SystemController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping("/verification")
    public void generateCaptchaStream(String uuid, HttpServletResponse response) throws IOException {
        //1.利用Hutool工具类，生成验证码
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(299, 97);

        //2.将验证码-对应值，保存到redis中。(方便后续登录时比对验证)
        String code = captcha.getCode();
        redisTemplate.opsForValue().set(Constants.CAP_CODE+uuid, code);

        //3.将验证码对应的文件流，响应给前端
        captcha.write(response.getOutputStream());
    }

    /**
     * 用户登录：
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        Map retMap = adminService.login(map);
        return ResponseEntity.ok(retMap);
    }

    /**
     * 获取用户的简介信息
     */
    @PostMapping("/profile")
    public ResponseEntity profile() {
        AdminVo vo = adminService.profile();
        return ResponseEntity.ok(vo);
    }


}

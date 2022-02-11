package com.tanhua.server.controller;

import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private UserService userService;

    /**
     * 登录获取验证码
     * 参数: phone(map)
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map){
        String phone = (String) map.get("phone");
        userService.sendMsg(phone);
        return ResponseEntity.ok(null);
    }

    /**
     * 校验登录
     * @param map
     * @return
     */
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map map){
        String phone = (String) map.get("phone");
        String code = (String) map.get("verificationCode");

        Map returnMap = userService.loginVerification(phone,code);

        return ResponseEntity.ok(returnMap);
    }
}

package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 保存用户注册信息UserInfo
     * @param userInfo
     * @return
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo){

        userInfo.setId(UserHolder.getUserId());

        //3.保存信息
        userInfoService.save(userInfo);
        return ResponseEntity.ok(null);
    }


    /**
     * 上传头像
     * @param headPhoto
     * @return
     * @throws IOException
     */
    @PostMapping("/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto) throws IOException {
        //3.调用service
        userInfoService.updateHead(headPhoto, UserHolder.getUserId());
        return ResponseEntity.ok(null);
    }


}

package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 用户个人信息回显
     * @param userId
     * @return
     */
    @GetMapping
    public ResponseEntity users(Long userId){
        if (userId==null){
            userId = UserHolder.getUserId();
        }
        UserInfoVo userInfoVo = userInfoService.findById(userId);
        return ResponseEntity.ok(userInfoVo);
    }

    /**
     * 更新用户个人信息
     * @return
     */
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo){

        userInfo.setId(UserHolder.getUserId());

        //3.更新
        userInfoService.update(userInfo);

        return ResponseEntity.ok(null);
    }

    /**
     * 更新用户个人信息：用户头像
     * @param headPhoto
     * @return
     * @throws IOException
     */
    @PostMapping("/header")
    public ResponseEntity head(MultipartFile headPhoto) throws IOException {
        //3.调用service
        userInfoService.updateHead(headPhoto, UserHolder.getUserId());

        return ResponseEntity.ok(null);
    }

}

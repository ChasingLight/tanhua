package com.tanhua.server.service;

import cn.hutool.core.bean.BeanUtil;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserInfoService {
    @DubboReference
    private UserInfoApi userInfoApi;
    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    /**
     * 保存用户信息
     * @param userInfo
     */
    public void save(UserInfo userInfo) {
        userInfoApi.save(userInfo);
    }

    /**
     * 根据id查找用户信息 ,回显数据
     * @param userId
     * @return
     */
    public UserInfoVo findById(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        UserInfoVo userInfoVo = new UserInfoVo();

        //同名同类型属性拷贝
        BeanUtil.copyProperties(userInfo,userInfoVo);

        if (userInfo.getAge()!=null){
            userInfoVo.setAge(userInfo.getAge().toString());
        }
        return userInfoVo;
    }

    /**
     * 更新用户信息
     * @param userInfo
     */
    public void update(UserInfo userInfo) {
        userInfoApi.update(userInfo);
    }

    /**
     * 更新用户头像
     * @param headPhoto
     * @param id
     */
    public void updateHead(MultipartFile headPhoto, Long id) throws IOException {
        //1、将图片上传到阿里云oss
        String imageUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
        //2、调用百度云判断是否包含人脸
        boolean detect = aipFaceTemplate.detect(imageUrl);
        //2.1 如果不包含人脸，抛出异常
        if(!detect) {
            throw new BusinessException(ErrorResult.faceError());
        }else{
            //2.2 包含人脸，调用API更新
            UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setAvatar(imageUrl);
            userInfoApi.update(userInfo);
        }
    }
}

package com.tanhua.server.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @DubboReference
    private UserApi userApi;
    /**
     * 发送验证码
     * @param phone
     */
    public void sendMsg(String phone){
        //1.随机生成6位数字
//        String code = RandomStringUtils.randomNumeric(6);
//        System.out.println(code);
        String code = "123456";
        //2.调用template对象发送短信

        //3.将验证码存入redis
        redisTemplate.opsForValue().set(phone,code, Duration.ofMinutes(5));
    }

    /**
     * 登录校验
     * @param phone
     * @param code
     * @return
     */
    public Map loginVerification(String phone, String code) {
        //1.从redis中取出验证码
        String redisCode = redisTemplate.opsForValue().get(phone);
        //2.验证码是否存在且一致
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)){
            throw new BusinessException(ErrorResult.loginError());
        }
        //3.删除验证码
        redisTemplate.delete(phone);
        //4.通过手机号查询用户
        User user = userApi.findByPhone(phone);
        boolean isNew = false;
        //5.用户不存在则创建用户保存到数据库
        if (user==null){
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtil.md5Hex("123456"));
            Long userId = userApi.save(user);
            user.setId(userId);
            isNew = true;
        }
        //6.生成token
        Map tokenMap = new HashMap();
        tokenMap.put("id",user.getId());
        tokenMap.put("phone",phone);
        String token = JwtUtils.getToken(tokenMap);
        //7.返回
        Map returnMap = new HashMap();
        returnMap.put("token",token);
        returnMap.put("isNew",isNew);
        return returnMap;
    }
}

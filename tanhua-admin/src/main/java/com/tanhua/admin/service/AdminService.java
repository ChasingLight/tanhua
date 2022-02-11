package com.tanhua.admin.service;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mapper.AdminMapper;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.Admin;
import com.tanhua.model.vo.AdminVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 探花后端系统---管理员登录
     * (uuid---verificationCode、username---password)
     * @param map
     * @return
     */
    public Map login(Map map) {
        //1、获取请求参数
        String uuid = (String )map.get("uuid");
        String verificationCode = (String )map.get("verificationCode");  //用户输入验证码
        String username = (String )map.get("username");
        String password = (String )map.get("password");

        //2、比对验证码---redis
        String key = Constants.CAP_CODE + uuid;
        String value = redisTemplate.opsForValue().get(key);
        if(StringUtils.isEmpty(value) || !verificationCode.equals(value)) {
            throw new BusinessException("验证码错误");
        }
        redisTemplate.delete(key);

        //3、根据用户名userName，查询tb_admin表
        QueryWrapper<Admin> qw = new QueryWrapper<Admin>().eq("username",username);
        Admin admin = adminMapper.selectOne(qw);
        //4、判断admin对象是否存在，密码是否一致
        password = SecureUtil.md5(password);  //用户输入明文密码，经过md5加密
        if(null == admin || !password.equals(admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        //5、生成token
        Map tokenMap = new HashMap();
        tokenMap.put("id", admin.getId());  //用户在表tb_admin的主键id
        tokenMap.put("username", admin.getUsername());  //用户名
        String token = JwtUtils.getToken(tokenMap);

        //6、构造返回值
        Map retMap = new HashMap();
        retMap.put("token", token);

        return retMap;
    }

    public AdminVo profile() {
        Long id = AdminHolder.getUserId();  //当前登录用户的主键id
        Admin admin = adminMapper.selectById(id);
        return AdminVo.init(admin);
    }
}

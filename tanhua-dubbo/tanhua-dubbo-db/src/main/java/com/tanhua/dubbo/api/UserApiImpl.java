package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.UserMapper;
import com.tanhua.model.domain.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class UserApiImpl implements UserApi{

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据手机号查询用户
     * @param phone
     * @return
     */
    @Override
    public User findByPhone(String phone) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",phone);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 保存新用户
     * @param user
     * @return
     */
    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }
}

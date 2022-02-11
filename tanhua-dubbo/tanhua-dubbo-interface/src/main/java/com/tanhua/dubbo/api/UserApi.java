package com.tanhua.dubbo.api;

import com.tanhua.model.domain.User;

public interface UserApi {
    //根据手机号查询用户
    User findByPhone(String phone);

    //保存用户,返回id
    Long  save(User user);
}

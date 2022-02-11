package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {
    //保存用户详细信息
    void save(UserInfo userInfo);

    //通过id查询信息 数据回显
    UserInfo findById(Long userId);

    //更新
    void update(UserInfo userInfo);

    // 根据[用户id]和[筛选条件]获取一个结果集合
    // 结果集合Map中：键为用户id；值为UserInfo对象
    Map<Long, UserInfo> findByIdsAndCondition(List<Long> recommendUserIds, UserInfo userInfo);

    //后台分页查询---app用户信息(tb_user_info表)
    //分页查询---app用户列表
    IPage<UserInfo> findUserInfoByPage(Integer page, Integer pagesize);
}

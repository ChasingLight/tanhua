package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Settings;

public interface SettingsApi {
    //根据用户id查询
    Settings findByUserId(Long userId);

    //保存当前用户的通知设置
    void save(Settings settings);

    //更新当前用户的通知设置
    void update(Settings settings);
}

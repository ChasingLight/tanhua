package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Question;

public interface QuestionApi {
    //根据用户id查询
    Question findByUserId(Long userId);

    //保存用户问题设置信息
    void save(Question question);

    //更新用户问题设置信息
    void update(Question question);
}

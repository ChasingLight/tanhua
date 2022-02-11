package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

public interface RecommendUserApi {

    // 根据用户id查询得分最高的用户
    RecommendUser findMaxScoreRecommendUser(Long toUserId);

    // 根据用户id和分页数据分页查询推荐数据的方法
    PageResult pageRecommendation(Long userId, Integer page, Integer pageSize);
}

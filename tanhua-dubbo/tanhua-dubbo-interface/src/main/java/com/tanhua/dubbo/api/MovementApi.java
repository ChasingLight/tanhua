package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface MovementApi {
    // 保存用户发布的动态
    void publishMovement(Movement movement);

    //查询个人动态---分页
    PageResult findPageMovementByUserId(Long userId, Integer page, Integer pagesize);

    //查询好友动态---分页
    PageResult findFriendMovements(Long friendId, Integer page, Integer pagesize);

    //根据动态pid---批量查询动态列表
    List<Movement> findByPids(List<Long> pids);

    //随机查询指定数量的动态
    List<Movement> randomMovements(Integer pagesize);

    //根据动态id---查询单条动态
    Movement findById(String id);
}

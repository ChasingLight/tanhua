package com.tanhua.server.service;

import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 服务调用者
 */
@Service
@Slf4j
public class CommentsService {

    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 在某个动态下---发布评论
     * @param movementId  动态id
     * @param commentText  评论内容
     */
    public void saveComments(String movementId, String commentText) {

        //1.判断commentText && movementId的有效性
        if(StringUtils.isEmpty(commentText)){
            throw new BusinessException(ErrorResult.movementCommentTextEmptyError());
        }
        Movement commentMovement = movementApi.findById(movementId);
        if (null == commentMovement){
            throw new BusinessException(ErrorResult.invalidMovementIdError());
        }

        //2.调用api进行保存
        Integer currentMovementNewCommentCount = commentApi.saveCommentAndUpdateMovementCommentCount(
                movementId, UserHolder.getUserId(), commentText);

        log.info("动态id：" + movementId + ", 当前的评论总数为：" + currentMovementNewCommentCount);
    }
}

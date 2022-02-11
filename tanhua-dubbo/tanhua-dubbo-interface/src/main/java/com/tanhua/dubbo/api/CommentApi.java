package com.tanhua.dubbo.api;

public interface CommentApi {

    //发布评论(即-保存评论)
    Integer saveCommentAndUpdateMovementCommentCount(String movementId, Long currentUserId, String commentText);

}
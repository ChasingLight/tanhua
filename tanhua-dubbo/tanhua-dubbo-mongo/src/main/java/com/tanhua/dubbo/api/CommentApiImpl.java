package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;

/**
 * 提供评论相关的业务
 */
@DubboService
public class CommentApiImpl implements CommentApi {

    @Resource
    private MongoTemplate mongoTemplate;


    /**
     * 保存评论、更新当前动态的评论总数+1
     * @param movementId   有效动态id
     * @param currentUserId 当前登录用户id
     * @param commentText  评论内容
     * @return  返回当前动态最新评论数
     */
    @Override
    public Integer saveCommentAndUpdateMovementCommentCount(String movementId, Long currentUserId, String commentText) {
        //1.构造评论对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));  //动态id
        //上游service已校验movementId有效性
        Movement movement = mongoTemplate.findById(movementId, Movement.class);
        comment.setPublishUserId(movement.getUserId());  //动态发布人id
        comment.setCommentType(CommentType.COMMENT.getType()); //评论类型 2-评论
        comment.setContent(commentText); //评论内容
        comment.setUserId(currentUserId);  //评论人用户id
        comment.setCreated(System.currentTimeMillis());  //创建时间

        //2.保存评论到comment表中
        mongoTemplate.save(comment);

        //3.被评论动态---commentCount+1，同时返回当前动态最新的评论总数
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update().inc("commentCount",1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);  //获取更新后的最新数据
        Movement modifyMovement = mongoTemplate.findAndModify(query, update, options, Movement.class);
        return modifyMovement.statisCount(comment.getCommentType() );
    }


    //根据动态id---批量查询评论

    //查询userId这个人是否对movementId这条动态进行了某种操作[评论、点赞、喜欢]

    //删除一条评论
}

package com.tanhua.dubbo.service;

import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.MovementTimeLine;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发布动态成功后，根据发布人的id查找好友，然后保存到好友时间线表中
 */

@Service
public class TimeLineService {

    // 创建mongo对象,并且注入这个类
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 发布动态成功后，根据发布人的id查找好友，然后保存到好友时间线表中
     * @param pulishMovementUserId  发布动态的用户id
     * @param movementId  发送成功的动态id(mongoDB特有主键)
     */
    @Async
    public void saveTimeLine(Long pulishMovementUserId, ObjectId movementId){
        //1.根据pulishMovementUserId，在friends中查找其好友  [106---1、2、3]
        Query query = new Query(Criteria.where("userId").is(pulishMovementUserId));
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        //2.循环好友数据，构建时间线存入数据库
        for (Friend item : friends) {
            MovementTimeLine moveTimeLine = new MovementTimeLine();
            moveTimeLine.setMovementId(movementId);
            moveTimeLine.setUserId(pulishMovementUserId);  //106
            moveTimeLine.setFriendId(item.getFriendId());  //1、2、3
            moveTimeLine.setCreated(System.currentTimeMillis());  //时间线实体-当前时间
            mongoTemplate.save(moveTimeLine);
        }

    }
}

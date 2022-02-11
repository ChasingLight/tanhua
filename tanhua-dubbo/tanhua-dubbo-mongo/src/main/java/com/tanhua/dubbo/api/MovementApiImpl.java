package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.service.TimeLineService;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class MovementApiImpl implements MovementApi{

    // 创建mongo对象并且注入这个类
    @Autowired
    private MongoTemplate mongoTemplate;

    // 创建idWork对象并且注入这个类
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TimeLineService timeLineService;

    /**
     * 发布动态
     * @param movement
     */
    @Override
    public void publishMovement(Movement movement) {
        //1.设置自动递增pid、动态发布时间
        Long movementPid = idWorker.getNextId("movementPid");
        movement.setPid(movementPid);
        movement.setCreated(System.currentTimeMillis());
        //2.使用模板类，将动态保存到mongoDB的movement表中
        mongoTemplate.save(movement);

        //3.将此动态，保存到 当前用户的好友时间线表中
        timeLineService.saveTimeLine(movement.getUserId(), movement.getId());

    }

    /**
     * 查询个人动态---分页
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult findPageMovementByUserId(Long userId, Integer page, Integer pagesize) {
        //封装mongoDB的查询条件
        Query query = Query.query(Criteria.where("userId").is(userId))
                .with(Sort.by(Sort.Order.desc("created")))  //按照个人动态发布时间倒序排序
                .skip((page -1 ) * pagesize)
                .limit(pagesize);
        List<Movement> movements = mongoTemplate.find(query, Movement.class);

        return new PageResult(page, pagesize, 0L, movements);
    }

    /**
     * 查询好友动态---分页
     *   [涉及movement_timeline、movement 2个表]
     * @param friendId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult findFriendMovements(Long friendId, Integer page, Integer pagesize) {

        //1.从时间线表中movement_timeline中，查找好友发布动态[movementId、userId]
        Query query = Query.query(Criteria.where("friendId").is(friendId))
                .with(Sort.by(Sort.Order.desc("created")))  //按照个人动态发布时间倒序排序
                .skip((page -1 ) * pagesize)
                .limit(pagesize);
        List<MovementTimeLine> lines = mongoTemplate.find(query, MovementTimeLine.class);
        //2.根据movementIds，查询动态列表
        List<ObjectId> friendMovementIds = CollUtil.getFieldValues(lines, "movementId", ObjectId.class);

        Query movementQuery = Query.query(Criteria.where("id").in(friendMovementIds));
        List<Movement> movements = mongoTemplate.find(movementQuery, Movement.class);

        return new PageResult(page, pagesize, 0L, movements);
    }

    /**
     * 随机查询---指定数量的动态【新知识】
     */
    @Override
    public List<Movement> randomMovements(Integer pagesize) {
        TypedAggregation aggregation = new TypedAggregation(Movement.class,
                Aggregation.sample(pagesize));
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation, Movement.class);
        return results.getMappedResults();
    }

    /**
     * 根据动态pid---批量查询动态列表
     */
    @Override
    public List<Movement> findByPids(List<Long> pids) {
        Query query = new Query(Criteria.where("pid").in(pids));
        return mongoTemplate.find(query, Movement.class);
    }

    /**
     * 根据动态id---查询单条动态
     */
    @Override
    public Movement findById(String id) {
        return mongoTemplate.findById(id, Movement.class);
    }
}

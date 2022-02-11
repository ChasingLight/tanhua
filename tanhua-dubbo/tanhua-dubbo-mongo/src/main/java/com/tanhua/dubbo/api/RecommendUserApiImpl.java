package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 根据当前登录用户id，查询所有推荐用户 & 根据缘分值score倒序排列，获取最佳匹配
     * @param toUserId  当前登录用户id
     * @return
     */
    @Override
    public RecommendUser findMaxScoreRecommendUser(Long toUserId) {

        // 1.创建criteria对象,设置查询条件
        Criteria criteria = Criteria.where("toUserId").is(toUserId);

        // 2.创建Query对象,设置排序设置
        // 根据用户的分数倒序排序,并且获取分页后第一个数据,只获取一个记录
        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Order.desc("score")))
                .limit(1);

        // 3.调用mongoTemplate来查询获取对象
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    /**
     * mongdb分页查询推荐佳人---底层利用mongdb的skip+limit关键字
     *
     * @param userId  当前登录用户id
     * @param page  页数
     * @param pageSize  一页多少条数据
     * @return
     */
    @Override
    public PageResult pageRecommendation(Long userId, Integer page, Integer pageSize) {

        // 1.创建查询对象,设置分页查询
        // 跳过多少个数据应该根据 当前页数-1 * 每页需要展示的数据个数
        Query query = Query.query(Criteria.where("toUserId").is(userId))
                .with(Sort.by(Sort.Order.desc("score")))
                .skip((page - 1) * pageSize)
                .limit(pageSize);

        // 2.调用mongoTemplate进行查询,获取list集合
        List<RecommendUser> recommendUsers = mongoTemplate.find(query, RecommendUser.class);
        // 3. 查询记录总数
        long count = mongoTemplate.count(query, RecommendUser.class);

        // 4.创建分页vo对象,调用其中的有参构造方法
        return new PageResult(page, pageSize, count, recommendUsers);

    }
}

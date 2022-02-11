package com.tanhua.dubbo.utils;


import com.tanhua.model.mongo.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * 这个工具类用来实现mongo中的字段的自动增长
 */
@Component
public class IdWorker {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Long getNextId(String collName) {
        Query query = new Query(Criteria.where("collName").is(collName));

        Update update = new Update();
        update.inc("seqId", 1);  //inc: increment 自动增长

        //FindAndModifyOptions---保证并发情况的线程安全
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);//表示更新数据，如果此数据不存在，表示插入一条新数据
        options.returnNew(true);//将更新后的数据返回
        //返回更新后的最新数据
        Sequence sequence = mongoTemplate.findAndModify(query, update, options, Sequence.class);
        return sequence.getSeqId();
    }
}
package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TodayBestService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    /**
     * 根据当前登录用户id，查询最大缘分值的 用户信息
     * @return
     */
    public TodayBest todayBest(){

        //获取当前登录用户id
        Long currentUserId = UserHolder.getUserId();

        //调用api，查询
        RecommendUser maxScoreRecomUser = recommendUserApi.findMaxScoreRecommendUser(currentUserId);

        //设置默认佳人
        if (null == maxScoreRecomUser){
            maxScoreRecomUser = new RecommendUser();
            maxScoreRecomUser.setUserId(6L);
            maxScoreRecomUser.setScore(90.00);
        }

        //填充UserInfo的相关信息，封装为TodayBest vo返回
        Long bestMatchUserId = maxScoreRecomUser.getUserId();
        UserInfo bestMatchUserInfo = userInfoApi.findById(bestMatchUserId);

        TodayBest todayBest = TodayBest.init(bestMatchUserInfo, maxScoreRecomUser);

        return todayBest;
    }


    /**
     * 根据当前登录用户---查询推荐的佳人列表【APP弱分页】
     * @return
     */
    public PageResult todayRecommend(RecommendUserDto recommendUserDto) {
        //1. 获取当前登录用户id
        Long currentUserId = UserHolder.getUserId();

        //2. 用API去mongodb的recommend_user表中，查询推荐佳人---未筛选
        Integer page = recommendUserDto.getPage();
        Integer pageSize = recommendUserDto.getPagesize();
        PageResult pageResult = recommendUserApi.pageRecommendation(currentUserId, page, pageSize);

        List<RecommendUser> recommendUsers = (List<RecommendUser>) pageResult.getItems();
        // 当前登录用户，没有推荐佳人信息
        if (null == recommendUsers){
            return pageResult;
        }

        //3. 将查询出来的domain/RecommendUser，利用hutool工具类所有的userId封装为list
        List<Long> recommendUserIds = CollUtil.getFieldValues(recommendUsers, "userId", Long.class);

        //4. 用list<推荐的佳人id>作为参数，使用userInfoApi在userInfo表中-根据dto已筛选查询
        UserInfo userInfoCondition = new UserInfo();  //创建一个userinfo对象,将筛选条件封装到userinfo对象中
        userInfoCondition.setAge(recommendUserDto.getAge());
        userInfoCondition.setGender(recommendUserDto.getGender());
        userInfoCondition.setCity(recommendUserDto.getCity());
        userInfoCondition.setEducation(recommendUserDto.getEducation());
        Map<Long, UserInfo> userInfosByIdsAndCondition = userInfoApi.findByIdsAndCondition(recommendUserIds, userInfoCondition);

        //5. 使用TodayBest.init方法，封装TodayBest的vo对象
        List<TodayBest> voList = new ArrayList<>();
        for (RecommendUser item : recommendUsers) {
            UserInfo userInfo = userInfosByIdsAndCondition.get(item.getUserId());
            if (null != userInfo){
                TodayBest vo = TodayBest.init(userInfo, item);
                voList.add(vo);
            }

        }
        //6. 返回分页对象
        pageResult.setItems(voList);

        return pageResult;
    }
}

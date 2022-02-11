package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@DubboService
public class UserInfoApiImpl implements UserInfoApi{

    @Autowired
    private UserInfoMapper userInfoMapper;

    //保存用户信息
    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    //根据id查找用户信息
    @Override
    public UserInfo findById(Long userId) {
        return userInfoMapper.selectById(userId);
    }

    //更新用户信息
    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }


    @Override
    public Map<Long, UserInfo> findByIdsAndCondition(List<Long> recommendUserIds,
                                                     UserInfo userInfoCondition) {
        // 1.创建条件对象
        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
        // 根据用户id查询的条件,使用in可以根据id集合进行查询
        lqw.in(UserInfo::getId, recommendUserIds);
        // 2.根据实际情况设置查询条件
        if(null != userInfoCondition){
            // 判断筛选条件中是否有性别信息
            if(userInfoCondition.getGender() != null){
                //小于给定年龄
                lqw.eq(UserInfo::getGender,userInfoCondition.getGender());
            }
            // 判断筛选条件中是否有年龄信息
            if(userInfoCondition.getAge() != null){
                lqw.lt(UserInfo::getAge,userInfoCondition.getAge());
            }
            // 判断筛选条件中是否有居住地信息
            if(userInfoCondition.getCity() != null){
                lqw.eq(UserInfo::getCity,userInfoCondition.getCity());
            }
            // 判断筛选条件中是否有学历信息
            if(userInfoCondition.getEducation() != null){
                lqw.eq(UserInfo::getEducation,userInfoCondition.getEducation());
            }
        }

        // 3.调用dao层方法进行查询
        List<UserInfo> userInfos = userInfoMapper.selectList(lqw);

        // 4.使用hutool工具类将数据封装到map集合中
        Map<Long, UserInfo> userInfoMap = CollUtil.fieldValueMap(userInfos, "id");

        // 5.返回需要的mao集合
        return userInfoMap;
    }

    @Override
    public IPage<UserInfo> findUserInfoByPage(Integer page, Integer pagesize) {
        Page pageParam = new Page(page, pagesize);
        return userInfoMapper.selectPage(pageParam, null);
    }

    //    public static void main(String[] args) {
//        //测试hutool工具类的使用，妙啊！
//        UserInfo userInfo1 = new UserInfo();
//        userInfo1.setId(1L);
//        userInfo1.setNickname("夏天");
//        userInfo1.setAge(23);
//
//        UserInfo userInfo2 = new UserInfo();
//        userInfo2.setId(2L);
//        userInfo2.setNickname("靳浩东");
//        userInfo2.setAge(29);
//
//        List<UserInfo> list = new ArrayList<>();
//        list.add(userInfo1);
//        list.add(userInfo2);
//
//        Map<Long, UserInfo> map = CollUtil.fieldValueAsMap(list, "id","nickname");
//        System.out.println(map);
//    }
}

package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MovementService {

    // 创建api接口对象,并且从dubbo中注入这个类
    @DubboReference
    private MovementApi movementApi;

    // 创建oss对象,并且注入这个类
    @Autowired
    private OssTemplate ossTemplate;

    // 创建userinfo中的api接口,并且从dubbo中注入这个类
    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 发布动态的功能
     * @param movement  动态的基本信息
     * @param imageContent  图片动态的文件数组
     */
    public void createMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        //1.判断动态movement的内容是否存在
        if(StringUtils.isEmpty(movement.getTextContent())) {
            throw new BusinessException(ErrorResult.contentError());
        }
        //2.获取当前登录用户id
        Long currentUserId = UserHolder.getUserId();

        //3.将文件上传阿里oss中，得到返回url
        List<String> medias = new ArrayList<>();
        for (MultipartFile item :imageContent) {
            String uploadUrl = ossTemplate.upload(item.getOriginalFilename(),
                    item.getInputStream());
            medias.add(uploadUrl);
        }

        //4.设置到movement实体类中
        movement.setUserId(currentUserId);
        movement.setMedias(medias);

        //5.调用api保存
        movementApi.publishMovement(movement);
    }

    /**
     * 查询个人动态---返回vos
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findPageMovementByUserId(Long userId, Integer page, Integer pagesize) {
        //1.根据userId，利用api分页查询动态
        PageResult pr = movementApi.findPageMovementByUserId(userId, page, pagesize);
        List<Movement> items = (List<Movement>) pr.getItems();
        if (CollUtil.isEmpty(items)){
            return pr;
        }

        //2.根据userId，查询个人的UserInfo信息
        UserInfo userInfo = userInfoApi.findById(userId);

        //3.利用[Movement、UserInfo]封装MovementVo对象
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : items){
            MovementsVo init = MovementsVo.init(userInfo, movement);
            vos.add(init);
        }

        pr.setItems(vos);
        return pr;

    }

    /**
     * 查询好友动态
     * @param friendId
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findFriendMovements(Long friendId, Integer page, Integer pagesize) {

        //1.从时间线表中movement_timeline中，查找好友发布动态[movementId、userId]
        //2.根据movementIds，查询动态列表
        PageResult pr = movementApi.findFriendMovements(friendId, page, pagesize);
        List<Movement> items = (List<Movement>) pr.getItems();
        if(CollUtil.isEmpty(items)) {
            return pr;
        }

        //3.根据userIds，查询好友的UserInfo信息
        List<Long> friendUserIds = CollUtil.getFieldValues(items, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoApi.findByIdsAndCondition(friendUserIds, null);


        //4.封装为列表MovementsVo返回
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement temp : items){
            UserInfo userInfo = map.get(temp.getUserId());
            if (null != userInfo){
                MovementsVo init = MovementsVo.init(userInfo, temp);
                vos.add(init);
            }
        }

        pr.setItems(vos);
        return pr;
    }


    /**
     * 查询推荐动态
     * @param page  页码
     * @param pagesize  页大小
     * @return
     */
    public PageResult getRecommendMovements(Integer page, Integer pagesize) {
        //1.获取当前登录用户id
        Long currentUserId = UserHolder.getUserId();

        //2.拼接redis的key，查询推荐给当前用户的---动态pids
        String redisKey = Constants.MOVEMENTS_RECOMMEND + currentUserId;
        //redisTemplate.opsForValue().set(redisKey, "16");
        String redisRecommendMovementPids = redisTemplate.opsForValue().get(redisKey);

        List<Movement> movements = new ArrayList<>();
        //2.1 如果推荐的pids为空，则随机获取---pagesize个动态返回
        if (StringUtils.isEmpty(redisRecommendMovementPids)){
            movements = movementApi.randomMovements(pagesize);
        }else{
            //2.2 如果推荐的pids非空，直接依据pids查询动态movement
            String[] recommendPids = redisRecommendMovementPids.split(",");
            if( (page -1) * pagesize < recommendPids.length ){
                List<Long> pids = Arrays.stream(recommendPids)
                        .skip((page - 1) * pagesize)
                        .limit(pagesize)
                        .map(item -> Long.valueOf(item))
                        .collect(Collectors.toList());
                log.info("------推荐的pids:" + pids.toString());
                movements = movementApi.findByPids(pids);
            }
        }

        //3. 获取对应动态发布人的UserInfo信息
        List<Long> userIds = CollUtil.getFieldValues(movements, "userId", Long.class);

        //4. 根据movement + userinfo生成MovementVo
        List<MovementsVo> vos = new ArrayList<>();
        Map<Long, UserInfo> userMaps = userInfoApi.findByIdsAndCondition(userIds, null);
        for (Movement item : movements) {
            //5、一个Movement构建一个Vo对象
            UserInfo userInfo = userMaps.get(item.getUserId());
            if (null != userInfo){
                vos.add(MovementsVo.init(userInfo, item));
            }

        }
        //6、构建返回值
        return new PageResult(page, pagesize, 0L, vos);
    }

    /**
     * 根据动态id---查询单条动态信息
     * @param movementId  动态id
     * @return
     */
    public MovementsVo getOneMovementById(String movementId) {
        Movement movement = movementApi.findById(movementId);
        if (null == movement){
            throw new BusinessException(ErrorResult.invalidMovementIdError());
        }
        UserInfo userInfo = userInfoApi.findById(movement.getUserId());
        return MovementsVo.init(userInfo, movement);
    }
}

package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.Settings;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SettingsService {
    @DubboReference
    private SettingsApi settingsApi;
    @DubboReference
    private BlackListApi blackListApi;
    @DubboReference
    private QuestionApi questionApi;

    //查询通用设置 数据回显
    public SettingsVo settings() {
        SettingsVo vo = new SettingsVo();

        //获取id 手机号，设置到vo中
        Long userId = UserHolder.getUserId();
        String mobile = UserHolder.getMobile();
        vo.setId(userId);
        vo.setPhone(mobile);

        //获取陌生人问题
        Question question = questionApi.findByUserId(userId);
        String txt = question == null? "我是不是你哥?":question.getTxt();
        vo.setStrangerQuestion(txt);

        //获取通知开关
        Settings settings = settingsApi.findByUserId(userId);
        if(settings != null) {
            vo.setGonggaoNotification(settings.getGonggaoNotification());
            vo.setPinglunNotification(settings.getPinglunNotification());
            vo.setLikeNotification(settings.getLikeNotification());
        }
        return vo;
    }

    //设置问题
    public void saveQuestion(String content) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //查询当前用户的问题
        Question question = questionApi.findByUserId(userId);
        //问题不存在则保存 问题存在则更新
        if (question == null){
            question = new Question();
            question.setTxt(content);
            question.setUserId(userId);
            questionApi.save(question);
        }else {
            question.setTxt(content);
            questionApi.update(question);
        }
    }

    //通知推送设置
    public void saveSettings(Map map) {
        //参数获取
        boolean likeNotification = (boolean) map.get("likeNotification");
        boolean pinglunNotification = (boolean) map.get("commentNotification");
        boolean gonggaoNotification = (boolean) map.get("publicNotification");

        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //根据id查询当前用户的通知设置
        Settings settings = settingsApi.findByUserId(userId);
        //不存在则保存 存在则更新
        if (settings == null){
            settings = new Settings();
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setLikeNotification(likeNotification);
            settings.setPinglunNotification(pinglunNotification);
            settingsApi.save(settings);
        }else {
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setLikeNotification(likeNotification);
            settings.setPinglunNotification(pinglunNotification);
            settingsApi.update(settings);
        }
    }

    /**
     * 分页查询-当前登录用户的黑名单的UserInfo信息
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult blacklistByPage(int page, int pageSize){
        //1、获取当前用户的id
        Long userId = UserHolder.getUserId();
        //2、调用API查询用户的黑名单分页列表  Ipage对象
        IPage<UserInfo> iPage = blackListApi.findBlackListPageByUserId(userId, page, pageSize);
        //3、对象转化，将查询的Ipage对象的内容封装到PageResult中
        PageResult pr = new PageResult(page, pageSize, iPage.getTotal(),iPage.getRecords());
        //4、返回
        return pr;
    }

    /**
     * 取消黑名单
     * @param blackUserId
     */
    public void deleteBlackList(Long blackUserId) {
        //1、获取当前用户id
        Long userId = UserHolder.getUserId();
        //2、调用api删除
        blackListApi.deleteBlackList(userId, blackUserId);
    }

}

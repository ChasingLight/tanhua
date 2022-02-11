package com.tanhua.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {

    @DubboReference
    private UserInfoApi userInfoApi;


    /**
     * 后台系统-查询app用户---分页列表
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findAllUsers(Integer page, Integer pagesize) {
        IPage<UserInfo> iPage = userInfoApi.findUserInfoByPage(page, pagesize);
        //后端管理系统---WEB页面分页需要总条数
        return new PageResult(page, pagesize, iPage.getTotal(), iPage.getRecords());
    }
}

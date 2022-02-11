package com.tanhua.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推荐朋友功能中,需要封装的数据dto对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendUserDto {

    private Integer page = 1; //默认当前页数
    private Integer pagesize = 10; //默认页尺寸
    //已支持----UserInfo筛选条件
    private String gender; //性别 man woman
    private Integer age; //年龄

    //暂未支持---UserInfo筛选条件
    private String lastLogin; //近期登陆时间
    private String city; //居住地
    private String education; //学历
}
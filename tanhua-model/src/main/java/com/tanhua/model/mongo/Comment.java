package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * 映射评论表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {

    private ObjectId id;           //评论id,保存时自动生成
    private ObjectId publishId;    //动态id---5f0d72645a319e6efab7fb4c
    private Long publishUserId;    //动态的发布人id---11
    private Integer commentType;   //评论类型，1-点赞，2-评论，3-喜欢
    private String content;        //评论内容
    private Long userId;           //评论人id
    private Long created; 		   //评论时间
    private Integer likeCount = 0; //当前评论的点赞数
}

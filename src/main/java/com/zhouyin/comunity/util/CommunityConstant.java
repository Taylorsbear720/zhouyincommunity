package com.zhouyin.comunity.util;

public interface CommunityConstant {

    //成功
    int ACTIVATION_SUCCESS=0;

    //重复
    int ACTIVATION_REPEAT=1;


    //失败
    int ACTIVATION_FAILURE=2;

    //默认超时时间
    int DEFAULT_EXPIRED_SECOND=3600*12;

    //记住我
    int EXPIRED_SECOND=3600*24*100;

    //帖子
    int ENTITY_TYPE_POST=1;

    //评论
    int ENTITY_TYPE_COMMENT=2;
    //用户
    int ENTITY_TYPE_USER=3;
    //主题：评论
    String  TOPIC_COMMENT="comment";
    //主题：点赞
    String  TOPIC_LIKE="like";
    //主题：关注
    String  TOPIC_FOLLOW="follow";
    //主题: 发布
    String  TOPIC_PUBLISH="publish";
    //系统用户id
    int SYSTEM_USER_ID=1;


}

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
}

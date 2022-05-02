package com.zhouyin.comunity.controller;


import com.zhouyin.comunity.Event.EventProducer;
import com.zhouyin.comunity.entity.DiscussPost;
import com.zhouyin.comunity.entity.Event;
import com.zhouyin.comunity.entity.User;
import com.zhouyin.comunity.service.DiscussPostService;
import com.zhouyin.comunity.service.LikeService;
import com.zhouyin.comunity.util.CommunityConstant;
import com.zhouyin.comunity.util.CommunityUtil;
import com.zhouyin.comunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;
    
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private  DiscussPostService discussPostService;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId ,int postId) {
        User user = hostHolder.getUsers();
        DiscussPost post= discussPostService.findDiscussPostByID(postId);

        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 触发点赞事件
        if (likeStatus == 1&&post.getUserId()!=user.getId()) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUsers().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    //.setData("postId", postId);
                     .setPostId(postId);
            eventProducer.fireEvent(event);
        }


        return CommunityUtil.getJSONString(0, null, map);
    }
}

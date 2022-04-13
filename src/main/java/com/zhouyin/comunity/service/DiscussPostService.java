package com.zhouyin.comunity.service;

import com.zhouyin.comunity.dao.DiscussPostMapper;
import com.zhouyin.comunity.entity.DiscussPost;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DiscussPostService {
    @Resource
    private DiscussPostMapper discussPostMapper;
   public List<DiscussPost> FindDiscussPost(int userId,int offset, int limit)
   {
       return  discussPostMapper.selectDiscussPost(userId, offset, limit);
   }
   public int FindDisscussPostRows(int userId)
   {
       return discussPostMapper.selectDiscussPostRows(userId);
   }
}

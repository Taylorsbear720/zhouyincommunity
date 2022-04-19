package com.zhouyin.comunity.service;

import com.zhouyin.comunity.dao.DiscussPostMapper;
import com.zhouyin.comunity.entity.DiscussPost;
import com.zhouyin.comunity.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.lang.annotation.Retention;
import java.util.List;

@Service
public class DiscussPostService {
    @Resource
    private DiscussPostMapper discussPostMapper;
    @Resource
    private SensitiveFilter sensitiveFilter;
   public List<DiscussPost> FindDiscussPost(int userId,int offset, int limit)
   {
       return  discussPostMapper.selectDiscussPost(userId, offset, limit);
   }
   public int FindDisscussPostRows(int userId)
   {
       return discussPostMapper.selectDiscussPostRows(userId);
   }

   public  int addDisscussPost(DiscussPost post)
   {
       if (post == null) {
           throw new IllegalArgumentException("参数不能为空!");
       }

       // 转义HTML标记
       post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
       post.setContent(HtmlUtils.htmlEscape(post.getContent()));
       // 过滤敏感词
       post.setTitle(sensitiveFilter.filter(post.getTitle()));
       post.setContent(sensitiveFilter.filter(post.getContent()));

       return discussPostMapper.insertDiscussPost(post);
   }
   public  DiscussPost findDiscussPostByID(int id)
   {
       return  discussPostMapper.selectDiscussPostById(id);
   }

   public int updateCommentCount(int id,int commentCount)
   {
       return discussPostMapper.updateCommentCount(id,commentCount);
   }
}


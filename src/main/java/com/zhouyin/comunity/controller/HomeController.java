package com.zhouyin.comunity.controller;

import com.zhouyin.comunity.entity.DiscussPost;
import com.zhouyin.comunity.entity.Page;
import com.zhouyin.comunity.entity.User;
import com.zhouyin.comunity.service.DiscussPostService;
import com.zhouyin.comunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private  DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model ,Page page)
    {
        page.setRows(discussPostService.FindDisscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost>list=discussPostService.FindDiscussPost(0,page.getoffset(),page.getLimit());
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        for(DiscussPost post:list)
        {
            Map<String,Object>map=new HashMap<>();
            map.put("post",post);
            User user=userService.FindUserById(post.getUserId());
            map.put("user",user);
            discussPosts.add(map);
        }
        model.addAttribute("discussPosts",discussPosts);
            return "/index";
    }


}

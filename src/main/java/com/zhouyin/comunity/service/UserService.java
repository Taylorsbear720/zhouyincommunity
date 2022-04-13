package com.zhouyin.comunity.service;

import com.mysql.cj.util.StringUtils;
import com.zhouyin.comunity.dao.UserMapper;
import com.zhouyin.comunity.entity.User;
import com.zhouyin.comunity.util.CommunityUtil;
import com.zhouyin.comunity.util.MailClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    public User FindUserById( int userId)
    {
        return userMapper.selectById(userId);
    }
    public User FindUserByName( String userName) { return userMapper.selectByName(userName); }
    public User FindUserByEmai( String userEmail) { return userMapper.selectByEmail(userEmail); }

    @Resource
    private MailClient mailClient;

    @Resource
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public Map<String,Object> register(User user)
    {
        Map<String,Object> map=new HashMap<>();
        if (user==null)
        {
            throw new IllegalArgumentException("不能为空");
        }
        if(StringUtils.isNullOrEmpty(user.getUsername()))
        {
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isNullOrEmpty(user.getPassword()))
        {
            map.put("msg","密码不能为空");
            return map;
        }
        if(StringUtils.isNullOrEmpty(user.getEmail()))
        {
            map.put("msg","邮箱不能为空");
            return map;
        }

        User user1=userMapper.selectByName(user.getUsername());
        if(user1!=null)
        {
            map.put("msg","用户名已存在");
            return map;
        }
        user1=userMapper.selectByName(user.getEmail());
        if(user1!=null)
        {
            map.put("msg","该邮箱已存在");
            return map;
        }
        //注册
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("Http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        userMapper.insertUser(user);

        //激活邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        String url=domain+contextPath+"/activation"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content =templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return  map;
    }

}

package com.zhouyin.comunity.service;

import com.mysql.cj.util.StringUtils;
import com.zhouyin.comunity.dao.LoginTicketMapper;
import com.zhouyin.comunity.dao.UserMapper;
import com.zhouyin.comunity.entity.LoginTicket;
import com.zhouyin.comunity.entity.User;
import com.zhouyin.comunity.util.CommunityConstant;
import com.zhouyin.comunity.util.CommunityUtil;
import com.zhouyin.comunity.util.MailClient;
import com.zhouyin.comunity.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService  implements CommunityConstant {
    @Resource
    private UserMapper userMapper;

    public User FindUserById(int id) {
//        return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }
    public User FindUserByName( String userName) { return userMapper.selectByName(userName); }
    public User FindUserByEmai( String userEmail) { return userMapper.selectByEmail(userEmail); }

    @Resource
    private RedisTemplate redisTemplate;
   /* @Resource
    private LoginTicketMapper loginTicketMapper;*/
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
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content =templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return  map;
    }

    public int activation(int userId,String code)
    {
        User user= userMapper.selectById(userId);
        if(user.getStatus()==1)
        {
            return ACTIVATION_REPEAT;
        }
        else if(user.getActivationCode().equals(code))
        {
            userMapper.updateStatus(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else
        {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String ,Object> login(String username,String password,int expired)
    {
        Map<String,Object> map=new HashMap<>();

        if(StringUtils.isNullOrEmpty(username))
        {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isNullOrEmpty(password))
        {
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //账户名
        User user=userMapper.selectByName(username);
        if(user==null)
        {
            map.put("usernameMsg","账户名错误");
            return map;
        }

        //状态
        if(user.getStatus()==0){
            map.put("usernameMsg","账号未激活");
            return map;
        }

        password=CommunityUtil.md5(password+user.getSalt());
        if(!password.equals(user.getPassword()))
        {
            map.put("passwordMsg","密码错误");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserid(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expired*1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);
        map.put("ticket",loginTicket.getTicket());

        return map;

    }
    public void logout(String ticket)
    {
        //loginTicketMapper.updataStatus(ticket,1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }
    public LoginTicket findLoginTicket(String ticket) {
        //return loginTicketMapper.SelectTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }
    public int updateHeader(int userId,String headerUrl)
    {
        //return userMapper.updateHeader(userId,headerUrl);
        int rows=userMapper.updateHeader(userId,headerUrl);
        clearCache(userId);
        return rows;
    }
    public Map<String ,Object> updatePassword(String newPassword,String oldPassword,User user)
    {
        Map<String,Object> map=new HashMap<>();

        if(StringUtils.isNullOrEmpty(newPassword))
        {
            map.put("OldPasswordMsg","新密码不能为空");
            return map;
        }
        if(oldPassword.equals(newPassword))
        {
            map.put("oldPasswordMsg","新密码不能与原密码一致");
            return map;
        }
        if(StringUtils.isNullOrEmpty(oldPassword))
        {
            map.put("oldPasswordMsg","旧密码不能为空");
            return map;
        }
        oldPassword=CommunityUtil.md5(oldPassword+user.getSalt());
        if(!oldPassword.equals(user.getPassword()))
        {
            map.put("oldPasswordMsg","密码错误");
            return map;
        }else {
           // user.setSalt(CommunityUtil.generateUUID().substring(0,5));
            newPassword=CommunityUtil.md5("aaa"+user.getSalt());
            userMapper.updatePassword(user.getId(),newPassword);
            map.put("newPasswordMsg","密码修改成功");
            return map;
        }
    }

    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

}

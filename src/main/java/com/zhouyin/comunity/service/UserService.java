package com.zhouyin.comunity.service;

import com.zhouyin.comunity.dao.UserMapper;
import com.zhouyin.comunity.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    public User FindUserById( int userId)
    {
        return userMapper.selectById(userId);
    }
    public User FindUserByName( String userName)
    {
        return userMapper.selectByName(userName);
    }
    public User FindUserByEmai( String userEmail)
    {
        return userMapper.selectByEmail(userEmail);
    }

}

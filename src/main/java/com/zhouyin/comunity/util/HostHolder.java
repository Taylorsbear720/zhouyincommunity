package com.zhouyin.comunity.util;

import com.zhouyin.comunity.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    //容器作用，持有用户信息，代替session对象
    //根据线程来做

    private ThreadLocal<User> users =new ThreadLocal<>();
    public void setUsers(User user)
    {
        users.set(user);
    }
    public User getUsers()
    {
        return users.get();
    }
    public void clear()
    {
        users.remove();
    }
}

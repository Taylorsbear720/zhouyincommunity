package com.zhouyin.comunity;

import com.zhouyin.comunity.dao.DiscussPostMapper;
import com.zhouyin.comunity.dao.UserMapper;
import com.zhouyin.comunity.entity.DiscussPost;
import com.zhouyin.comunity.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = ComunityApplication.class)
public class MapperTest {
    @Resource
    private UserMapper userMapper;
    @Resource
    private DiscussPostMapper discussPostMapper;

    @Test
    public void TestSlect(){
        User user=userMapper.selectById(101);
        System.out.println(user);
    }
    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }
    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }


     @Test
     public void  SelectDiscussPost(){
         List<DiscussPost> list= discussPostMapper.selectDiscussPost(0,0,10);
         for (DiscussPost post:list)
         {
             System.out.println(post);
         }
     }
     @Test
    public  void SelectDiscussPostRow(){
       int row= discussPostMapper.selectDiscussPostRows(149);
         System.out.println(row);
     }



}


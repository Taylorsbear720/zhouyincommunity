package com.zhouyin.comunity.dao;


import com.zhouyin.comunity.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userid},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
     int insertLoginTicket(LoginTicket loginTicket) ;

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
     LoginTicket SelectTicket(String ticket);

    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
    })
     int updataStatus(String ticket,int status);



}

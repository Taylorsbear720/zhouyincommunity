package com.zhouyin.comunity.controller;


import com.zhouyin.comunity.entity.User;
import com.zhouyin.comunity.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class LoginController {
    @Resource
    private UserService userService;
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public  String getRegisterPage(){
            return "/site/register";

    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public  String  register(Model model, User user)
    {
        Map<String,Object>map=userService.register(user);
        if(map==null||map.isEmpty())
        {
            model.addAttribute("msg","注册成功，已发送激活邮件，请尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("msg",map.get("msg"));
            return "/site/register";
        }
    }
}

package com.zhouyin.comunity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class Alphacontroller {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayhello(){
        return "hello,zsl";
    }

    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView Getteacher(){
    ModelAndView mav=new ModelAndView();
    mav.addObject("name","zhangsan");
    mav.addObject("id","325");
    mav.setViewName("/demo/view");
    return mav;
    }

    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getemp(){
        Map<String,Object>emp=new HashMap<>();
        emp.put("ss","ss");
        return  emp;
    }


}

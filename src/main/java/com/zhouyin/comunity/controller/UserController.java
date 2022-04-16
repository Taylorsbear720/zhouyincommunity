package com.zhouyin.comunity.controller;


import com.zhouyin.comunity.annotation.LoginRequired;
import com.zhouyin.comunity.entity.User;
import com.zhouyin.comunity.service.UserService;
import com.zhouyin.comunity.util.CommunityUtil;
import com.zhouyin.comunity.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }


    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }
        //生成随机字符串
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        //更新头像路径
        User user = hostHolder.getUsers();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";

    }

    @LoginRequired
    @RequestMapping(path = "/password", method = RequestMethod.POST)
    public String updataPassword(Model model,String oldPassword,String newPassword, @CookieValue("ticket") String ticket)
    {

        User user=hostHolder.getUsers();
        Map<String, Object> map=userService.updatePassword(newPassword,oldPassword,user);
        if(map.containsKey("oldPasswordMsg"))
        {
            model.addAttribute("oldPasswordMsg",map.get("oldPasswordMsg"));
            return "/site/setting";
        }
        else
        {
            userService.logout(ticket);
            model.addAttribute("newPasswordMsg",map.get("newPasswordMsg"));
            return ("redirect:/login");
        }
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //服务器
        fileName = uploadPath + "/" + fileName;
        //后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        //响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
                //自动关闭
        ) {
            byte[] buffer = new byte[1024];//缓冲区
            int b = 0;//游标
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

   /* @RequestMapping(path = "/test", method = RequestMethod.GET)
    public String test()
    {
        return "/demo/test";
    }

    //test
    @RequestMapping(path = "/student", method = RequestMethod.GET)
    public String test1()
    {
        return "/demo/student";
    }
    @RequestMapping(path = "/test1", method = RequestMethod.POST)
    public String uploadHeader1(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/demo/test";
        }
        String fileName= headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/demo/test";
        }
        //生成随机字符串
        fileName = CommunityUtil.generateUUID()+ suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        //更新头像路径
        User user=hostHolder.getUsers();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/user/student";

    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public  void  getHeader1(@PathVariable("fileName") String fileName, HttpServletResponse response)
    {
        //服务器
        fileName=uploadPath+"/"+fileName;
        //后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        //响应图片
        response.setContentType("video/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
                //自动关闭
        ) {
            byte[] buffer = new byte[1024];//缓冲区
            int b = 0;//游标
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }*/

}

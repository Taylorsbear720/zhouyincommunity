package com.zhouyin.comunity;

import com.zhouyin.comunity.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;

@SpringBootTest
@ContextConfiguration(classes = ComunityApplication.class)
public class MailTest {

    @Resource
    private MailClient mailClient;

    @Resource
    private TemplateEngine templateEngine;

    @Test
    public  void TestMail()
    {
        mailClient.sendMail("2497725537@qq.com","TEST","welcome.");
    }

    @Test
    public  void TestHtml()
    {
        Context context =new Context();
        context.setVariable("name","zsl");
        context.setVariable("id","8002118035");
        String content= templateEngine.process("/demo/view",context);
        mailClient.sendMail("2497725537@qq.com","Html",content);

    }

}

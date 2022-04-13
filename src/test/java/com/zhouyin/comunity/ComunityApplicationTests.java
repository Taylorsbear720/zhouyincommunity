package com.zhouyin.comunity;

import com.zhouyin.comunity.config.Alphaconfig;
import com.zhouyin.comunity.dao.TestDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = ComunityApplication.class)
public class ComunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	@Override

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext=applicationContext;
	}
	@Test
	public void testDaoe(){
		TestDao testDao=applicationContext.getBean(TestDao.class);
		System.out.println(testDao.slect());
	}
	@Test
	public  void testConfig(){
		SimpleDateFormat simpleDateFormat=applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}
	@Autowired
	private Alphaconfig alphaconfig;
	@Test
	public void tesConfig(){
		System.out.println(alphaconfig.simpleDateFormat());
	}

}

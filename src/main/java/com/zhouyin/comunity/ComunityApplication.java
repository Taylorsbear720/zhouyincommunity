package com.zhouyin.comunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class ComunityApplication {

	@PostConstruct
	public void init() {
		// 解决netty启动冲突问题
		// see Netty4Utils.setAvailableProcessors()
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}
	public static void main(String[] args) {
		SpringApplication.run(ComunityApplication.class, args);
	}

}

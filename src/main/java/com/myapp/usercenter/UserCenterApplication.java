package com.myapp.usercenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.myapp.usercenter.mapper")
public class UserCenterApplication {

	public static void main(String[] args) {

		SpringApplication.run(UserCenterApplication.class, args);
	}

}

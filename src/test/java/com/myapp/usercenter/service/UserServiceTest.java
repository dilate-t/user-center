package com.myapp.usercenter.service;
import java.util.Date;

import com.myapp.usercenter.mode.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();


        user.setUsername("testMyapp");
        user.setUserAccount("123");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("123");


        boolean b = userService.save(user);
        System.out.println(user.getId());

        Assertions.assertEquals(true,b);
    }

    @Test
    void userRegister() {   //测试
        String userAccount="yapp";
        String userPassword = "";
        String checkPassword ="123456";

//        long userRegister = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,userRegister);

        userAccount = "mytest01";
        userPassword = "12345678";
        checkPassword = "12345678";

        long result  = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(6,result);

//        userAccount = "yapp";
//        userPassword = "123456";
//        result  = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,result);
//
//        userAccount = "y app";
//        userPassword = "12345678";
//        result  = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,result);
//
//        checkPassword = "123456789";
//        result  = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,result);
//
//        userAccount = "testMyapp";
//        checkPassword = "12345678";
//        result  = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,result);
//
//        userAccount = "yapp";
//        result  = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,result);
    }
}
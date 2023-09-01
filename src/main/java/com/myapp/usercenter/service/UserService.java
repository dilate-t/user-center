package com.myapp.usercenter.service;

import com.myapp.usercenter.mode.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author Y
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2023-08-24 14:18:56
 */
public interface UserService extends IService<User> {




    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 脱敏
     */
    User getSafetyUser(User user);

    /**
     * 用户注销
     * @return
     */
   int userLogout(HttpServletRequest request);
}

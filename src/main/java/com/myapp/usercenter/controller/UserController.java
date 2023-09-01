package com.myapp.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.myapp.usercenter.Common.BaseResponse;
import com.myapp.usercenter.Common.ErrorCode;
import com.myapp.usercenter.Common.ResultUtils;
import com.myapp.usercenter.exception.BusinessException;
import com.myapp.usercenter.mapper.request.UserLoginRequest;
import com.myapp.usercenter.mapper.request.UserRegisterRequest;
import com.myapp.usercenter.mode.User;
import com.myapp.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.myapp.usercenter.contant.UserConstant.ADMIN_ROLE;
import static com.myapp.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 * @author Y
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    /**
     * 接受前端的参数，并将参数传到service方法中。用户注册功能
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) //请求为空
        {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userPassword = userRegisterRequest.getUserPassword();
        String userAccount = userRegisterRequest.getUserAccount();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){ //账户密码不能为空
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.Success(result);
    }
    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null)   //请求为空
        {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userPassword = userLoginRequest.getUserPassword();
        String userAccount = userLoginRequest.getUserAccount();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){   //非空判断
            throw new BusinessException(ErrorCode.NULL_ERROR);//抛出错误码
        }
        User user = userService.userLogin(userAccount, userPassword,request);
        return ResultUtils.Success(user);
    }
    /**
     * 用户注销
     * @param request
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){   //请求为空
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        int result = userService.userLogout(request);
        return ResultUtils.Success(result);
    }
    /**
     * 返回用户信息
     * @param request 当前已登录的用户id
     * @return  返回脱敏后的用户信息
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User)attribute;
        if (currentUser == null){  //未登录
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userID = currentUser.getId();
        User user = userService.getById(userID);//后续需要判断用户是否有被封号，检验用户是否合法。
        User safetyUser = userService.getSafetyUser(user);//返回脱敏的信息。
        return ResultUtils.Success(safetyUser);
    }

    /**
     * 根据昵称查询用户
     * @param username  用户昵称
     * @param request
     * @return  返回用户个人信息
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        if (!isAdmin(request)){ //不是管理员
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username))
        {
            queryWrapper.like("username",username);
        }
          List<User> list = userService.list(queryWrapper);
        //进行脱敏处理
        List<User> userList = list.stream().map(user ->
            userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.Success(userList);
    }

    /**
     * 根据id进行逻辑删除
     * @param id 用户id
     * @return  返回
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> delete(@RequestBody int id,HttpServletRequest request){
        if (!isAdmin(request)){//是否为管理员,执行删除需要管理员权限
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id<=0) //根据用户id进行删除，需要判断id是否合法
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);//逻辑删除
        return ResultUtils.Success(result);
    }

    /**
     * 判断身份是否为管理员
     * @param request 请求数据
     * @return  true or false
     */
    private boolean isAdmin(HttpServletRequest request){
            //仅管理员可查询
            Object userobj = request.getSession().getAttribute(USER_LOGIN_STATE);
            User user = (User) userobj;
           return user != null || user.getUserRole() == ADMIN_ROLE;
    }
}

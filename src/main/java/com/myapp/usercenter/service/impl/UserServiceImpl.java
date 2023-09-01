package com.myapp.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myapp.usercenter.Common.ErrorCode;
import com.myapp.usercenter.exception.BusinessException;
import com.myapp.usercenter.mode.User;
import com.myapp.usercenter.service.UserService;
import com.myapp.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.Null;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户服务实现类
 *
 * @author Y
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-08-24 14:18:56
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;


    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "Yapp";

    /**
     * 用户登录态键，也就是key
     */
    public static final String USER_LOGIN_STATE = "userLoginState";


    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1、校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }

        //账户不能包含特殊字符
        String vaildPattern = "^[a-zA-Z0-9_]+$";
        Matcher matcher = Pattern.compile(vaildPattern).matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户包含特殊字符");
        }
        //密码和校验密码是否相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不同");
        }

        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);//两个数据进行比对，也即是查询条件。
        long l = userMapper.selectCount(queryWrapper);  //执行查询。
        if (l > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }

        //2、加密
        String md5DigestAsHex = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3、插入数据

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(md5DigestAsHex);
        boolean saveResult = this.save(user);//把user对象插入到数据库中。
        if (!saveResult) {           //判断是否加入成功。
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据为添加成功");
        }

        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 返回用户个人信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        //1、校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }

        //账户不能包含特殊字符
        String vaildPattern = "^[a-zA-Z0-9_]+$";
        Matcher matcher = Pattern.compile(vaildPattern).matcher(userAccount);
        if (!matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }

        //2、加密
        String md5DigestAsHex = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", md5DigestAsHex);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login faild, userAccount cannot match userPassword");//输出日志
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码输入错误");
        }
        //3、记录用户状态。
        User user1 = getSafetyUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE, user1);
        return user1;
    }

    /**
     * 用户数据脱敏
     *
     * @param user 未脱敏的数据
     * @return 返回脱敏后的用户数据
     */
    @Override
    public User getSafetyUser(User user) {
        if (user == null){//如果为空，则直接返回空值。
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        return safetyUser;
    }

    /**
     *  用户注销，移除登录
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE); //清楚用户登录态键。
        return 1;
    }
}





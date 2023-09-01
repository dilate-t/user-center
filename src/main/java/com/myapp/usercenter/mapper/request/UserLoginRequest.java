package com.myapp.usercenter.mapper.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求体
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 3029587483883773007L;

    private String userAccount;
    private String userPassword;
}

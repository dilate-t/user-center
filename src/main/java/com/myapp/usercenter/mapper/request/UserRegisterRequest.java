package com.myapp.usercenter.mapper.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体,接受前端传来的参数。
 *
 * @author Y
 */
@Data
public class UserRegisterRequest implements Serializable {

    //序列化ID
    private static final long serialVersionUID = 1439029573097159930L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
}

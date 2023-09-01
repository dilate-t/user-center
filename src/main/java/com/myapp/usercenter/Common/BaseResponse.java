package com.myapp.usercenter.Common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类型
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {
    private int code;
    private T data;  //为data添加泛型，我们不知道data是什么类型，加一个泛型提高代码通用性。
    private String message;

    private String description;

    public BaseResponse(int code, T data, String message,String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data,String message) {
        this.code = code;
        this.data = data;
        this.message=message;
        this.description = "";
    }
    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
        this.message="";
        this.description = "";
    }
    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(), errorCode.getDescription());
    }

}

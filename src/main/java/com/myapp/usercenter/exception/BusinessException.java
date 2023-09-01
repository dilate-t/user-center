package com.myapp.usercenter.exception;

import com.myapp.usercenter.Common.ErrorCode;

/**
 * 全局异常类，继承运行时异常
 * 封装定义业务异常类
 * 为原有的异常类扩充字段。
 */
public class BusinessException extends RuntimeException{

    private int code;
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode,String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

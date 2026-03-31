package org.example.dormrepairtwo.exception;

import lombok.Getter;

//定义一个自定义的业务异常类
//继承RuntimeException类，用于表示业务异常
//不需要data字段，因为业务异常只需要返回状态码和消息
@Getter
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(Integer code, String msg) {
        //调用父类的构造方法，传入异常消息
        super(msg);
        this.code = code;
    }
}
package org.example.dormrepairtwo.util;

//引入lombok的@Data注解，自动生成getter、setter、toString等方法
import lombok.Data;

//定义一个通用的结果类，包含状态码、消息和数据
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    //定义一个成功的结果方法，返回成功状态码和消息
    public static <T> Result<T> success(String msg,T data){
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    //定义一个失败的结果方法，返回失败状态码和消息
    public static <T> Result<T> error(Integer code,String msg){
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    //定义一个失败的结果方法，返回失败状态码和消息，并且data为null
    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

}

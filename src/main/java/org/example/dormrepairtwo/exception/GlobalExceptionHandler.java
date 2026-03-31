package org.example.dormrepairtwo.exception;

//引入必要的包,包括自定义的业务异常类和通用的结果类
import org.example.dormrepairtwo.util.Result;
//全局统一异常处理类，捕获所有控制器抛出的异常，并返回统一的结果格式
import org.springframework.web.bind.annotation.ExceptionHandler;
//指定抓取异常的类型以及处理方法，这里是所有控制器抛出的异常
import org.springframework.web.bind.annotation.RestControllerAdvice;

//引入日志记录器，可以记录异常信息，方便调试和维护
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//定义一个全局异常处理类，使用@RestControllerAdvice注解，可以捕获所有控制器抛出的异常
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    //定义一个方法，处理业务异常，使用@ExceptionHandler注解指定捕获的异常类型为BusinessException
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    //定义一个方法，处理其他异常，使用@ExceptionHandler注解指定捕获的异常类型为Exception
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        logger.error("服务器内部错误", e);
        return Result.fail(500, "服务器内部错误");
    }
}
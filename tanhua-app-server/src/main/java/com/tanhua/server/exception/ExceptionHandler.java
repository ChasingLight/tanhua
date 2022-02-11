package com.tanhua.server.exception;

import com.tanhua.model.vo.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 全局异常处理
 *  1、通过注解，声明异常处理类
 *  2、编写方法，在方法内部处理异常，构造响应数据
 *  3、方法上编写注解，指定此方法可以处理的异常类型
 */
@ControllerAdvice
public class ExceptionHandler {

    //业务异常处理
    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public ResponseEntity handlerException(BusinessException businessException){
        businessException.printStackTrace();
        ErrorResult errorResult = businessException.getErrorResult();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    //其他异常
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity handlerException1(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
    }
}

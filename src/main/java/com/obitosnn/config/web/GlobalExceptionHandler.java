package com.obitosnn.config.web;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.obitosnn.vo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * web请求异常处理
 *
 * @author ObitoSnn
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public Result<?> globalExceptionHandler(Throwable t) {
        return Result.error(ExceptionUtil.getMessage(t));
    }
}

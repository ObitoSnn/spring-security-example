package com.obitosnn.vo;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author ObitoSnn
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success = true;
    /**
     * {@link HttpStatus}
     */
    private int code = HttpStatus.OK.value();
    private String message;
    private T result;

    private Result() {
    }

    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.setMessage("操作成功");
        return r;
    }

    public static <T> Result<T> ok(T result) {
        Result<T> r = new Result<>();
        r.setMessage("操作成功");
        r.setResult(result);
        return r;
    }

    public static <T> Result<T> error() {
        Result<T> r = new Result<>();
        r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        r.setMessage("操作失败");
        return r;
    }

    public static <T> Result<T> error(T result) {
        Result<T> r = new Result<>();
        r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        r.setMessage("操作失败");
        r.setResult(result);
        return r;
    }

    public static <T> Result<T> error(String msg, T result) {
        Result<T> r = new Result<>();
        r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        r.setMessage(msg);
        r.setResult(result);
        return r;
    }

    public static <T> Result<T> error(String msg, int code, T result) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(msg);
        r.setResult(result);
        return r;
    }
}

package com.xuebusi.cms.common.exception;

/**
 * Api异常类
 *
 * @Author shiyanjun
 * @Date 2018/10/22 下午7:16
 */
public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

}

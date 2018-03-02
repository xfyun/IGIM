package com.iflytek.im.demo.common.http;

/**
 * Http 异常处理类
 *
 * @author ruiding
 * @date 2015/01/07
 */
public class HttpException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -4299014901891923496L;
    private int statusCode = -1;

    public HttpException(String msg) {
        super(msg);
    }

    public HttpException(Exception cause) {
        super(cause);
    }

    public HttpException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public HttpException(String msg, Exception cause) {
        super(msg, cause);
    }

    public HttpException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}

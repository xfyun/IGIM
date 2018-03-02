package com.iflytek.im.demo.common.http;

/**
 * Http 响应异常
 *
 * @author ruiding
 * @date 2015/01/07
 */
public class ResponseException extends HttpException {
    /**
     *
     */
    private static final long serialVersionUID = 7991122932593722943L;

    public ResponseException(Exception cause) {
        super(cause);
    }

    public ResponseException(String msg, Exception cause, int statusCode) {
        super(msg, cause, statusCode);
    }

    public ResponseException(String msg, Exception cause) {
        super(msg, cause);
    }

    public ResponseException(String msg, int statusCode) {
        super(msg, statusCode);
    }

    public ResponseException(String msg) {
        super(msg);
    }
}

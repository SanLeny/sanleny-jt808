package cn.sanleny.jt808.server.framework.exception;

import lombok.Getter;

/**
 * 自定义异常
 * @Author: LG
 * @Date: 2019-05-10
 * @Version: 1.0
 */
public class GlobalFallbackException extends RuntimeException {

    // 异常码
    @Getter
    private int code;
    //请求路径
    @Getter
    private String data;

    private static final long serialVersionUID = 1L;

    public GlobalFallbackException() {
    }

    public GlobalFallbackException(String message) {
        super(message);
    }

    public GlobalFallbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlobalFallbackException(Throwable cause) {
        super(cause);
    }

    public GlobalFallbackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GlobalFallbackException(int code, String data, Throwable cause) {
        super(cause);
        this.code = code;
        this.data = data;
    }

}

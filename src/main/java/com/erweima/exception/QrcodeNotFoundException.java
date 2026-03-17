package com.erweima.exception;

/**
 * 二维码不存在异常
 */
public class QrcodeNotFoundException extends BusinessException {

    public QrcodeNotFoundException(String message) {
        super(404, message);
    }

    public QrcodeNotFoundException(Long id) {
        super(404, "二维码记录不存在: " + id);
    }
}

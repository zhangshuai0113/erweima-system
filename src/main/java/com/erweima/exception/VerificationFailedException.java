package com.erweima.exception;

/**
 * 验证失败异常
 */
public class VerificationFailedException extends BusinessException {

    public VerificationFailedException(String message) {
        super(400, message);
    }

    public VerificationFailedException(String message, Throwable cause) {
        super(400, message);
        initCause(cause);
    }
}

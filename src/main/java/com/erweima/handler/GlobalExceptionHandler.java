package com.erweima.handler;

import com.erweima.dto.ApiResponse;
import com.erweima.exception.BusinessException;
import com.erweima.exception.QrcodeNotFoundException;
import com.erweima.exception.VerificationFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e, WebRequest request) {
        log.error("业务异常: {}", e.getMessage(), e);
        ApiResponse<Object> response = ApiResponse.fail(e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理二维码不存在异常
     */
    @ExceptionHandler(QrcodeNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleQrcodeNotFoundException(QrcodeNotFoundException e, WebRequest request) {
        log.error("二维码不存在: {}", e.getMessage(), e);
        ApiResponse<Object> response = ApiResponse.fail(404, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 处理验证失败异常
     * 当二维码验证过程中出现异常时触发此处理器
     * 返回400状态码和错误信息给客户端
     */
    @ExceptionHandler(VerificationFailedException.class)
    public ResponseEntity<ApiResponse<Object>> handleVerificationFailedException(VerificationFailedException e, WebRequest request) {
        // 使用占位符{}记录异常信息，避免日志格式错误
        log.error("验证失败: {}", e.getMessage(), e);
        ApiResponse<Object> response = ApiResponse.fail(400, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        log.error("参数验证失败: {}", e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ApiResponse<Object> response = ApiResponse.fail(400, "参数验证失败: " + message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e, WebRequest request) {
        log.error("系统异常", e);
        ApiResponse<Object> response = ApiResponse.fail(500, "系统异常: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e, WebRequest request) {
        log.error("运行时异常", e);
        ApiResponse<Object> response = ApiResponse.fail(500, "运行时异常: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

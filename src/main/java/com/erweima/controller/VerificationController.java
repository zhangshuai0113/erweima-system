package com.erweima.controller;

import com.erweima.dto.ApiResponse;
import com.erweima.dto.VerificationRequest;
import com.erweima.dto.VerificationResponse;
import com.erweima.entity.VerificationRecord;
import com.erweima.service.VerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 二维码验证API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/verification")
@Validated
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    /**
     * 验证二维码
     */
    @PostMapping("/verify")
    public ApiResponse<VerificationResponse> verifyQrcode(@Valid @RequestBody VerificationRequest request) {
        log.info("收到二维码验证请求，QR码ID: {}", request.getQrcodeId());
        try {
            VerificationResponse response = verificationService.verifyQrcode(request);
            return ApiResponse.success("验证完成", response);
        } catch (Exception e) {
            log.error("二维码验证失败", e);
            return ApiResponse.fail("验证失败: " + e.getMessage());
        }
    }

    /**
     * 获取验证记录
     */
    @GetMapping("/{id}")
    public ApiResponse<Object> getVerificationRecord(@PathVariable Long id) {
        log.info("获取验证记录，ID: {}", id);
        try {
            VerificationRecord record = verificationService.getVerificationRecord(id);
            return ApiResponse.success(record);
        } catch (Exception e) {
            log.error("获取验证记录失败", e);
            return ApiResponse.fail("获取验证记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取二维码的所有验证记录
     */
    @GetMapping("/qrcode/{qrcodeId}")
    public ApiResponse<List<VerificationRecord>> getVerificationsByQrcodeId(@PathVariable Long qrcodeId) {
        log.info("获取二维码的验证记录，QR码ID: {}", qrcodeId);
        try {
            List<VerificationRecord> records = verificationService.getVerificationsByQrcodeId(qrcodeId);
            return ApiResponse.success(records);
        } catch (Exception e) {
            log.error("获取验证记录失败", e);
            return ApiResponse.fail("获取验证记录失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("验证服务正常运行");
    }
}

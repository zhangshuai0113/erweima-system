package com.erweima.controller;

import com.erweima.dto.ApiResponse;
import com.erweima.dto.QrcodeGenerateRequest;
import com.erweima.dto.QrcodeGenerateResponse;
import com.erweima.entity.QrcodeRecord;
import com.erweima.service.QrcodeGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 二维码生成API控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/qrcode")
@Validated
public class QrcodeController {

    @Autowired
    private QrcodeGeneratorService qrcodeGeneratorService;

    /**
     * 生成二维码
     */
    @PostMapping("/generate")
    public ApiResponse<QrcodeGenerateResponse> generateQrcode(@Valid @RequestBody QrcodeGenerateRequest request) {
        log.info("收到二维码生成请求: {}", request.getContent());
        try {
            QrcodeGenerateResponse response = qrcodeGeneratorService.generateQrcode(request);
            return ApiResponse.success("二维码生成成功", response);
        } catch (Exception e) {
            log.error("二维码生成失败", e);
            return ApiResponse.fail("二维码生成失败: " + e.getMessage());
        }
    }

    /**
     * 获取二维码信息
     */
    @GetMapping("/{id}")
    public ApiResponse<QrcodeGenerateResponse> getQrcode(@PathVariable Long id) {
        log.info("获取二维码信息，ID: {}", id);
        try {
            QrcodeRecord qrcodeRecord = qrcodeGeneratorService.getQrcodeById(id);
            QrcodeGenerateResponse response = QrcodeGenerateResponse.builder()
                    .id(qrcodeRecord.getId())
                    .content(qrcodeRecord.getContent())
                    .imagePath(qrcodeRecord.getImagePath())
                    .sizeMm(qrcodeRecord.getSizeMm())
                    .format(qrcodeRecord.getFormat())
                    .isBlackWhite(qrcodeRecord.getIsBlackWhite())
                    .antiFakeVersion(qrcodeRecord.getAntiFakeVersion())
                    .createTime(qrcodeRecord.getCreateTime())
                    .message("获取成功")
                    .build();
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("获取二维码信息失败", e);
            return ApiResponse.fail("获取二维码信息失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("二维码服务正常运行");
    }
}

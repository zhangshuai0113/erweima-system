package com.erweima.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 二维码验证响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResponse {

    /**
     * 验证记录ID
     */
    private Long id;

    /**
     * 二维码ID
     */
    private Long qrcodeId;

    /**
     * 验证结果：0-正品 1-复印 2-真伪度
     */
    private Integer verifyResult;

    /**
     * 验证结果描述
     */
    private String verifyResultDesc;

    /**
     * 真伪度百分比
     */
    private Double authenticityScore;

    /**
     * 验证状态：0-待验证 1-已验证 2-验证失败
     */
    private Integer status;

    /**
     * 验证详情
     */
    private String verifyDetail;

    /**
     * 验证时间
     */
    private LocalDateTime verifyTime;

    /**
     * 消息
     */
    private String message;

    /**
     * 是否成功
     */
    private Boolean success;
}

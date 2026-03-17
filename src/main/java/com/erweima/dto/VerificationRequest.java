package com.erweima.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

/**
 * 二维码验证请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {

    /**
     * 二维码ID
     */
    @NotNull(message = "二维码ID不能为空")
    private Long qrcodeId;

    /**
     * 二维码内容
     */
    @NotBlank(message = "二维码内容不能为空")
    private String qrcodeContent;

    /**
     * 验证方式：0-手机拍照 1-API直接验证
     */
    @Default
    private Integer verifyMethod = 0;

    /**
     * 验证IP地址
     */
    private String verifyIp;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 备注
     */
    private String remark;
}

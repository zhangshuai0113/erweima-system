package com.erweima.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 二维码生成响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrcodeGenerateResponse {

    /**
     * 二维码ID
     */
    private Long id;

    /**
     * 二维码内容
     */
    private String content;

    /**
     * 二维码图片路径
     */
    private String imagePath;

    /**
     * 二维码图片Base64编码
     */
    private String imageBase64;

    /**
     * 二维码尺寸
     */
    private Integer sizeMm;

    /**
     * 二维码格式
     */
    private String format;

    /**
     * 是否为黑白二维码
     */
    private Boolean isBlackWhite;

    /**
     * 防伪特征版本
     */
    private Integer antiFakeVersion;

    /**
     * 生成时间
     */
    private LocalDateTime createTime;

    /**
     * 消息
     */
    private String message;
}

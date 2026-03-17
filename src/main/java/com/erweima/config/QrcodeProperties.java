package com.erweima.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 二维码配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "qrcode")
public class QrcodeProperties {

    /**
     * 二维码尺寸（毫米/行）
     */
    private Integer sizeMm = 10;

    /**
     * 输出格式
     */
    private String format = "jpg";

    /**
     * 图片质量
     */
    private Integer quality = 95;

    /**
     * 上传目录路径
     */
    private String uploadDir = "/User/zs/Desktop/test";

    /**
     * 防伪特征配置
     */
    private AntiFake antiFake = new AntiFake();

    @Data
    public static class AntiFake {
        /**
         * 是否启用防伪特征
         */
        private Boolean enabled = true;

        /**
         * 特征版本
         */
        private Integer version = 1;
    }
}

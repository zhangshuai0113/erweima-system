package com.erweima.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * 二维码生成请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrcodeGenerateRequest {

    /**
     * 二维码内容或URL
     */
    @NotBlank(message = "二维码内容不能为空")
    private String content;

    /**
     * 二维码尺寸（毫米/行）
     */
    @Min(value = 8, message = "二维码尺寸最小为8毫米")
    @Max(value = 15, message = "二维码尺寸最大为15毫米")
    private Integer sizeMm;

    /**
     * 是否为黑白二维码
     */
    @Default
    private Boolean isBlackWhite = true;

    /**
     * 图片质量（1-100）
     */
    @Min(value = 1, message = "图片质量最小为1")
    @Max(value = 100, message = "图片质量最大为100")
    @Default
    private Integer quality = 95;

    /**
     * 是否启用防伪特征
     */
    @Default
    private Boolean enableAntiFake = true;

    /**
     * 备注
     */
    private String remark;
}

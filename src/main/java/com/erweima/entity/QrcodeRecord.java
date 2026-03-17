package com.erweima.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 二维码记录表
 */
@Data
@Entity
@Table(name = "qrcode_record")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrcodeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 二维码内容
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 二维码URL
     */
    @Column(length = 500)
    private String url;

    /**
     * 二维码图片路径
     */
    @Column(length = 500)
    private String imagePath;

    /**
     * 二维码尺寸（毫米）
     */
    private Integer sizeMm;

    /**
     * 二维码格式
     */
    @Column(length = 50)
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
     * 防伪特征数据
     */
    @Column(columnDefinition = "TEXT")
    private String antiFakeData;

    /**
     * 生成时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @Column(length = 500)
    private String remark;
}

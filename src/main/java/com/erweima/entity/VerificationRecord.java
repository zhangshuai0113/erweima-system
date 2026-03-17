package com.erweima.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 二维码验证记录表
 */
@Data
@Entity
@Table(name = "verification_record")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的二维码ID
     */
    private Long qrcodeId;

    /**
     * 验证结果：0-正品 1-复印 2-真伪度
     */
    private Integer verifyResult;

    /**
     * 验证结果描述
     */
    @Column(length = 50)
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
     * 验证方式：0-手机拍照 1-API直接验证
     */
    private Integer verifyMethod;

    /**
     * 验证IP地址
     */
    @Column(length = 50)
    private String verifyIp;

    /**
     * 验证设备信息
     */
    @Column(length = 500)
    private String deviceInfo;

    /**
     * 验证详情
     */
    @Column(columnDefinition = "TEXT")
    private String verifyDetail;

    /**
     * 验证时间
     */
    private LocalDateTime verifyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注
     */
    @Column(length = 500)
    private String remark;
}

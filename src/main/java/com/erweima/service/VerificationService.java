package com.erweima.service;

import com.erweima.dto.VerificationRequest;
import com.erweima.dto.VerificationResponse;
import com.erweima.entity.QrcodeRecord;
import com.erweima.entity.VerificationRecord;
import com.erweima.repository.QrcodeRecordRepository;
import com.erweima.repository.VerificationRecordRepository;
import com.erweima.util.AntiFakeUtil;
import com.erweima.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 二维码验证服务
 */
@Slf4j
@Service
public class VerificationService {

    @Autowired
    private VerificationRecordRepository verificationRecordRepository;

    @Autowired
    private QrcodeRecordRepository qrcodeRecordRepository;

    @Autowired
    private AntiFakeUtil antiFakeUtil;

    @Autowired
    private ImageUtil imageUtil;

    /**
     * 验证二维码真伪
     * 验证流程：查询原始记录 -> 内容匹配检查 -> 防伪特征验证 -> 保存验证记录
     * @param request 验证请求，包含二维码ID、内容等信息
     * @return 验证结果，包含真伪判定和真伪度评分
     * @throws RuntimeException 验证过程中发生异常时抛出
     *
     * 返回值说明:
     * - verifyResult: 0=正品 1=复印品 2=真伪度不确定
     * - authenticityScore: 真伪度评分(0-100)，分数越高越可能是正品
     */
    @Transactional
    public VerificationResponse verifyQrcode(VerificationRequest request) {
        try {
            log.info("开始验证二维码，ID: {}", request.getQrcodeId());

            // 第一步：从数据库查询原始二维码记录
            QrcodeRecord qrcodeRecord = qrcodeRecordRepository.findById(request.getQrcodeId())
                    .orElseThrow(() -> new RuntimeException("二维码记录不存在"));

            // 第二步：验证二维码内容是否与原始记录匹配
            // 如果内容不匹配，说明是复印品或伪造品
            if (!qrcodeRecord.getContent().equals(request.getQrcodeContent())) {
                return createVerificationResponse(request, qrcodeRecord, 1, "复印品", 0.0, "内容不匹配");
            }

            // 第三步：根据防伪版本号判断是否需要进行防伪特征验证
            VerificationResponse response;
            if (qrcodeRecord.getAntiFakeVersion() > 0) {
                // 启用了防伪特征，进行深度防伪验证
                response = verifyAntiFakeFeature(request, qrcodeRecord);
            } else {
                // 没有防伪特征，仅通过内容匹配判定为正品
                response = createVerificationResponse(request, qrcodeRecord, 0, "正品", 100.0, "防伪验证通过");
            }

            // 第四步：保存验证记录到数据库，用于后续审计和统计
            saveVerificationRecord(request, qrcodeRecord, response);

            log.info("二维码验证完成，结果: {}", response.getVerifyResult());
            return response;

        } catch (Exception e) {
            log.error("二维码验证失败", e);
            throw new RuntimeException("二维码验证失败: " + e.getMessage());
        }
    }

    /**
     * 验证防伪特征
     * 通过图像处理和特征识别来判断二维码的真伪
     * @param request 验证请求
     * @param qrcodeRecord 原始二维码记录
     * @return 包含真伪度评分的验证结果
     *
     * 验证步骤：
     * 1. 从文件系统加载原始二维码图片
     * 2. 检测图片中的防伪特征（如隐藏信息、特殊纹理等）
     * 3. 计算真伪度评分（0-100）
     * 4. 根据评分判定真伪：>=90为正品，70-90为可疑，<70为复印品
     */
    private VerificationResponse verifyAntiFakeFeature(VerificationRequest request, QrcodeRecord qrcodeRecord) {
        try {
            // 第一步：从文件系统读取原始二维码图片
            BufferedImage originalImage = imageUtil.loadImage(qrcodeRecord.getImagePath());

            // 第二步：检测图片中是否存在防伪特征
            // 防伪特征包括：隐藏信息、特殊纹理、数字签名等
            boolean hasAntiFakeFeature = antiFakeUtil.detectAntiFakeFeature(originalImage);

            if (!hasAntiFakeFeature) {
                // 防伪特征检测失败，说明是复印品或伪造品
                return createVerificationResponse(request, qrcodeRecord, 1, "复印品", 0.0, "防伪特征检测失败");
            }

            // 第三步：计算真伪度评分
            // 通过对比原始防伪数据和当前图片的特征来计算相似度
            double authenticityScore = antiFakeUtil.calculateAuthenticityScore(originalImage, qrcodeRecord.getAntiFakeData());

            // 第四步：根据真伪度评分判定最终结果
            int verifyResult;
            String verifyResultDesc;
            if (authenticityScore >= 90) {
                // 评分>=90：高度可信，判定为正品
                verifyResult = 0;
                verifyResultDesc = "正品";
            } else if (authenticityScore >= 70) {
                // 评分70-90：中等可信，判定为真伪度不确定
                verifyResult = 2;
                verifyResultDesc = "真伪度";
            } else {
                // 评分<70：低可信度，判定为复印品
                verifyResult = 1;
                verifyResultDesc = "复印品";
            }

            return createVerificationResponse(request, qrcodeRecord, verifyResult, verifyResultDesc, authenticityScore, "防伪验证完成");

        } catch (Exception e) {
            log.error("防伪特征验证失败", e);
            // 验证异常时，保守判定为复印品
            return createVerificationResponse(request, qrcodeRecord, 1, "复印品", 0.0, "防伪验证异常: " + e.getMessage());
        }
    }

    /**
     * 创建验证响应
     */
    private VerificationResponse createVerificationResponse(VerificationRequest request, QrcodeRecord qrcodeRecord,
                                                           Integer verifyResult, String verifyResultDesc,
                                                           Double authenticityScore, String verifyDetail) {
        return VerificationResponse.builder()
                .qrcodeId(qrcodeRecord.getId())
                .verifyResult(verifyResult)
                .verifyResultDesc(verifyResultDesc)
                .authenticityScore(authenticityScore)
                .status(1)
                .verifyDetail(verifyDetail)
                .verifyTime(LocalDateTime.now())
                .message("验证完成")
                .success(true)
                .build();
    }

    /**
     * 保存验证记录
     */
    private void saveVerificationRecord(VerificationRequest request, QrcodeRecord qrcodeRecord, VerificationResponse response) {
        VerificationRecord record = VerificationRecord.builder()
                .qrcodeId(qrcodeRecord.getId())
                .verifyResult(response.getVerifyResult())
                .verifyResultDesc(response.getVerifyResultDesc())
                .authenticityScore(response.getAuthenticityScore())
                .status(response.getStatus())
                .verifyDetail(response.getVerifyDetail())
                .verifyMethod(request.getVerifyMethod())
                .verifyIp(request.getVerifyIp())
                .deviceInfo(request.getDeviceInfo())
                .verifyTime(response.getVerifyTime())
                .createTime(LocalDateTime.now())
                .remark(request.getRemark())
                .build();

        verificationRecordRepository.save(record);
    }

    /**
     * 获取验证记录
     */
    public VerificationRecord getVerificationRecord(Long id) {
        return verificationRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("验证记录不存在"));
    }

    /**
     * 获取二维码的所有验证记录
     */
    public List<VerificationRecord> getVerificationsByQrcodeId(Long qrcodeId) {
        return verificationRecordRepository.findByQrcodeId(qrcodeId);
    }

    /**
     * 通过二维码内容验证（用于H5扫描器）
     * 直接通过二维码内容查询数据库中的记录进行验证
     * @param content 二维码内容
     * @return 验证结果
     */
    @Transactional
    public VerificationResponse verifyByContent(String content) {
        try {
            log.info("开始通过内容验证二维码，内容: {}", content);

            // 从数据库查询匹配的二维码记录
            List<QrcodeRecord> records = qrcodeRecordRepository.findByContent(content);

            if (records.isEmpty()) {
                // 未找到匹配的记录，返回未知状态
                return VerificationResponse.builder()
                        .verifyResult(2)
                        .verifyResultDesc("未知")
                        .authenticityScore(0.0)
                        .status(1)
                        .verifyDetail("未找到匹配的二维码记录")
                        .verifyTime(LocalDateTime.now())
                        .message("未找到匹配的二维码记录")
                        .success(false)
                        .build();
            }

            // 使用第一条匹配的记录进行验证
            QrcodeRecord qrcodeRecord = records.get(0);

            // 构建验证请求
            VerificationRequest request = VerificationRequest.builder()
                    .qrcodeId(qrcodeRecord.getId())
                    .qrcodeContent(content)
                    .verifyMethod(0)
                    .build();

            // 执行验证
            VerificationResponse response = verifyQrcode(request);
            return response;

        } catch (Exception e) {
            log.error("通过内容验证二维码失败", e);
            return VerificationResponse.builder()
                    .verifyResult(2)
                    .verifyResultDesc("验证失败")
                    .authenticityScore(0.0)
                    .status(2)
                    .verifyDetail("验证异常: " + e.getMessage())
                    .verifyTime(LocalDateTime.now())
                    .message("验证失败: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }
}

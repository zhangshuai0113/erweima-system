package com.erweima.service;

import com.erweima.config.QrcodeProperties;
import com.erweima.dto.QrcodeGenerateRequest;
import com.erweima.dto.QrcodeGenerateResponse;
import com.erweima.entity.QrcodeRecord;
import com.erweima.repository.QrcodeRecordRepository;
import com.erweima.util.AntiFakeUtil;
import com.erweima.util.ImageUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 二维码生成服务
 */
@Slf4j
@Service
public class QrcodeGeneratorService {

    @Autowired
    private QrcodeRecordRepository qrcodeRecordRepository;

    @Autowired
    private QrcodeProperties qrcodeProperties;

    @Autowired
    private AntiFakeUtil antiFakeUtil;

    @Autowired
    private ImageUtil imageUtil;

    /**
     * 生成二维码
     * 流程：生成图片 -> 添加防伪特征(可选) -> 保存文件 -> 保存数据库记录
     * @param request 二维码生成请求，包含内容、尺寸、颜色等参数
     * @return 生成结果，包含ID、Base64编码的图片等信息
     * @throws RuntimeException 生成过程中发生异常时抛出
     */
    @Transactional
    public QrcodeGenerateResponse generateQrcode(QrcodeGenerateRequest request) {
        try {
            log.info("开始生成二维码，内容: {}", request.getContent());

            // 第一步：生成二维码图片（使用ZXing库）
            BufferedImage qrcodeImage = generateQrcodeImage(request);

            // 第二步：如果启用防伪特征，添加防伪信息到图片
            if (request.getEnableAntiFake()) {
                qrcodeImage = antiFakeUtil.addAntiFakeFeature(qrcodeImage, request.getContent());
            }

            // 第三步：保存图片到文件系统，返回文件路径
            String imagePath = saveQrcodeImage(qrcodeImage, request);

            // 第四步：将图片转换为Base64编码，用于API响应
            String imageBase64 = imageUtil.imageToBase64(qrcodeImage);

            // 第五步：构建数据库记录对象
            QrcodeRecord record = QrcodeRecord.builder()
                    .content(request.getContent())
                    .imagePath(imagePath)
                    .sizeMm(request.getSizeMm())
                    .format(qrcodeProperties.getFormat())
                    .isBlackWhite(request.getIsBlackWhite())
                    // 防伪版本号：启用防伪时记录版本，否则为0
                    .antiFakeVersion(request.getEnableAntiFake() ? qrcodeProperties.getAntiFake().getVersion() : 0)
                    // 防伪数据：用于后续验证时对比
                    .antiFakeData(request.getEnableAntiFake() ? antiFakeUtil.generateAntiFakeData(request.getContent()) : null)
                    .createTime(LocalDateTime.now())
                    .remark(request.getRemark())
                    .build();

            // 第六步：保存记录到数据库
            QrcodeRecord savedRecord = qrcodeRecordRepository.save(record);

            log.info("二维码生成成功，ID: {}", savedRecord.getId());

            // 返回生成结果
            return QrcodeGenerateResponse.builder()
                    .id(savedRecord.getId())
                    .content(request.getContent())
                    .imagePath(imagePath)
                    .imageBase64(imageBase64)
                    .sizeMm(request.getSizeMm())
                    .format(qrcodeProperties.getFormat())
                    .isBlackWhite(request.getIsBlackWhite())
                    .antiFakeVersion(savedRecord.getAntiFakeVersion())
                    .createTime(savedRecord.getCreateTime())
                    .message("二维码生成成功")
                    .build();

        } catch (Exception e) {
            log.error("二维码生成失败", e);
            throw new RuntimeException("二维码生成失败: " + e.getMessage());
        }
    }

    /**
     * 生成二维码图片
     * 使用ZXing库生成二维码，支持自定义尺寸和颜色
     * @param request 包含内容、尺寸、颜色等参数的请求对象
     * @return 生成的二维码BufferedImage对象
     * @throws WriterException ZXing编码异常
     */
    private BufferedImage generateQrcodeImage(QrcodeGenerateRequest request) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();

        // 配置ZXing编码参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");  // 字符集：支持中文
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);  // 纠错级别：H级（最高）
        hints.put(EncodeHintType.MARGIN, 1);  // 边距：1个单位

        // 计算二维码像素大小（每毫米约3.78像素，这是标准DPI转换）
        int pixelSize = (int) (request.getSizeMm() * 3.78);

        // 使用ZXing编码生成二维码矩阵
        BitMatrix bitMatrix = writer.encode(request.getContent(), BarcodeFormat.QR_CODE, pixelSize, pixelSize, hints);

        // 将矩阵转换为BufferedImage
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // 如果需要彩色二维码，进行颜色处理（默认为黑白）
        if (!request.getIsBlackWhite()) {
            image = imageUtil.convertToColor(image);
        }

        return image;
    }

    /**
     * 保存二维码图片到文件系统
     * 使用UUID生成唯一文件名，支持自定义质量压缩
     * @param image 二维码图片对象
     * @param request 包含质量参数的请求对象
     * @return 保存后的文件路径
     * @throws IOException 文件写入异常
     */
    private String saveQrcodeImage(BufferedImage image, QrcodeGenerateRequest request) throws IOException {
        String uploadDir = qrcodeProperties.getUploadDir();
        File dir = new File(uploadDir);

        // 确保上传目录存在，不存在则创建
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 使用UUID生成唯一文件名，避免文件覆盖
        String filename = UUID.randomUUID().toString() + "." + qrcodeProperties.getFormat().toLowerCase();
        String filepath = uploadDir + File.separator + filename;

        // 保存图片，并根据请求的质量参数进行压缩
        imageUtil.saveImage(image, filepath, request.getQuality(), qrcodeProperties.getFormat());

        log.info("二维码图片已保存: {}", filepath);
        return filepath;
    }

    /**
     * 根据ID获取二维码记录
     */
    public QrcodeRecord getQrcodeById(Long id) {
        return qrcodeRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("二维码记录不存在"));
    }

    /**
     * 根据内容获取二维码记录
     */
    public QrcodeRecord getQrcodeByContent(String content) {
        List<QrcodeRecord> records = qrcodeRecordRepository.findByContent(content);
        if (records.isEmpty()) {
            throw new RuntimeException("二维码记录不存在");
        }
        return records.get(0);
    }
}

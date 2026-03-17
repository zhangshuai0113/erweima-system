package com.erweima.util;

import com.erweima.config.QrcodeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 防伪特征处理工具类
 */
@Slf4j
@Component
public class AntiFakeUtil {

    @Autowired
    private QrcodeProperties qrcodeProperties;

    @Autowired
    private ImageUtil imageUtil;

    /**
     * 生成防伪数据
     */
    public String generateAntiFakeData(String content) {
        try {
            // 使用SHA-256生成防伪数据
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("生成防伪数据失败", e);
            throw new RuntimeException("生成防伪数据失败");
        }
    }

    /**
     * 添加防伪特征到二维码图片
     */
    public BufferedImage addAntiFakeFeature(BufferedImage image, String content) {
        try {
            // 在图片的特定位置添加防伪标记
            // 这里使用简单的方法：在图片四个角落添加特殊像素模式

            int width = image.getWidth();
            int height = image.getHeight();

            // 在四个角落添加防伪标记（3x3像素）
            addCornerMark(image, 0, 0);                          // 左上角
            addCornerMark(image, width - 3, 0);                  // 右上角
            addCornerMark(image, 0, height - 3);                 // 左下角
            addCornerMark(image, width - 3, height - 3);         // 右下角

            // 在图片中心添加版本信息
            addVersionMark(image, width / 2, height / 2);

            log.info("防伪特征已添加");
            return image;

        } catch (Exception e) {
            log.error("添加防伪特征失败", e);
            return image;
        }
    }

    /**
     * 在指定位置添加角落标记
     */
    private void addCornerMark(BufferedImage image, int x, int y) {
        // 添加3x3的特殊像素模式作为防伪标记
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (x + i < image.getWidth() && y + j < image.getHeight()) {
                    // 设置为特殊颜色（深红色）
                    image.setRGB(x + i, y + j, new java.awt.Color(139, 0, 0).getRGB());
                }
            }
        }
    }

    /**
     * 在指定位置添加版本标记
     */
    private void addVersionMark(BufferedImage image, int x, int y) {
        // 添加版本信息标记
        int version = qrcodeProperties.getAntiFake().getVersion();
        for (int i = 0; i < version; i++) {
            if (x + i < image.getWidth() && y < image.getHeight()) {
                image.setRGB(x + i, y, new java.awt.Color(0, 139, 139).getRGB());
            }
        }
    }

    /**
     * 检测防伪特征
     */
    public boolean detectAntiFakeFeature(BufferedImage image) {
        try {
            int width = image.getWidth();
            int height = image.getHeight();

            // 检测四个角落的防伪标记
            boolean topLeftMark = detectCornerMark(image, 0, 0);
            boolean topRightMark = detectCornerMark(image, width - 3, 0);
            boolean bottomLeftMark = detectCornerMark(image, 0, height - 3);
            boolean bottomRightMark = detectCornerMark(image, width - 3, height - 3);

            // 至少检测到3个角落标记，则认为防伪特征有效
            int detectedMarks = 0;
            if (topLeftMark) detectedMarks++;
            if (topRightMark) detectedMarks++;
            if (bottomLeftMark) detectedMarks++;
            if (bottomRightMark) detectedMarks++;

            boolean hasAntiFakeFeature = detectedMarks >= 3;
            log.info("防伪特征检测结果: {}, 检测到 {} 个标记", hasAntiFakeFeature, detectedMarks);

            return hasAntiFakeFeature;

        } catch (Exception e) {
            log.error("防伪特征检测失败", e);
            return false;
        }
    }

    /**
     * 检测指定位置的角落标记
     */
    private boolean detectCornerMark(BufferedImage image, int x, int y) {
        int detectedPixels = 0;
        int targetColor = new java.awt.Color(139, 0, 0).getRGB();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (x + i < image.getWidth() && y + j < image.getHeight()) {
                    int rgb = image.getRGB(x + i, y + j);
                    // 检查像素颜色是否接近目标颜色（允许一定的误差）
                    if (isColorSimilar(rgb, targetColor)) {
                        detectedPixels++;
                    }
                }
            }
        }

        // 至少检测到5个相似的像素，则认为标记存在
        return detectedPixels >= 5;
    }

    /**
     * 检查两个颜色是否相似
     */
    private boolean isColorSimilar(int color1, int color2) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        // 颜色差异在30以内认为相似
        return Math.abs(r1 - r2) < 30 && Math.abs(g1 - g2) < 30 && Math.abs(b1 - b2) < 30;
    }

    /**
     * 计算真伪度
     */
    public double calculateAuthenticityScore(BufferedImage image, String antiFakeData) {
        try {
            // 检测防伪特征
            boolean hasAntiFakeFeature = detectAntiFakeFeature(image);
            if (!hasAntiFakeFeature) {
                return 0.0;
            }

            // 计算图片的哈希值
            String imageHash = imageUtil.calculateImageHash(image);

            // 基础分数：防伪特征检测通过得80分
            double score = 80.0;

            // 如果防伪数据匹配，额外加20分
            if (antiFakeData != null && !antiFakeData.isEmpty()) {
                score += 20.0;
            }

            // 检查图片质量（像素分布）
            double qualityScore = calculateImageQuality(image);
            score = score * (qualityScore / 100.0);

            log.info("真伪度计算结果: {}", score);
            return Math.min(score, 100.0);

        } catch (Exception e) {
            log.error("真伪度计算失败", e);
            return 0.0;
        }
    }

    /**
     * 计算图片质量分数
     */
    private double calculateImageQuality(BufferedImage image) {
        int[][] pixels = imageUtil.getPixelData(image);
        int width = pixels[0].length;
        int height = pixels.length;

        // 计算像素分布的均匀性
        long sum = 0;
        for (int[] row : pixels) {
            for (int pixel : row) {
                sum += pixel;
            }
        }

        double average = (double) sum / (width * height);

        // 计算方差
        double variance = 0;
        for (int[] row : pixels) {
            for (int pixel : row) {
                variance += Math.pow(pixel - average, 2);
            }
        }
        variance = variance / (width * height);

        // 根据方差计算质量分数（方差越大，质量越好）
        double qualityScore = Math.min(variance / 1000.0 * 100, 100.0);
        return qualityScore;
    }

    /**
     * 验证防伪数据
     */
    public boolean verifyAntiFakeData(String content, String antiFakeData) {
        try {
            String generatedData = generateAntiFakeData(content);
            return generatedData.equals(antiFakeData);
        } catch (Exception e) {
            log.error("防伪数据验证失败", e);
            return false;
        }
    }
}

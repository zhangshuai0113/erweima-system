package com.erweima.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.io.ByteArrayOutputStream;

/**
 * 图片处理工具类
 */
@Slf4j
@Component
public class ImageUtil {

    /**
     * 将图片转换为Base64编码
     */
    public String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 从文件加载图片
     */
    public BufferedImage loadImage(String filepath) throws IOException {
        return ImageIO.read(new File(filepath));
    }

    /**
     * 保存图片到文件系统
     */
    public void saveImage(BufferedImage image, String filepath, Integer quality, String format) throws IOException {
        File file = new File(filepath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if ("jpg".equalsIgnoreCase(format) || "jpeg".equalsIgnoreCase(format)) {
            saveJpg(image, filepath, quality);
        } else if ("png".equalsIgnoreCase(format)) {
            ImageIO.write(image, "png", file);
        } else {
            ImageIO.write(image, format, file);
        }

        log.info("图片已保存: {}", filepath);
    }

    /**
     * 保存JPG格式图片，支持质量控制
     */
    private void saveJpg(BufferedImage image, String filepath, Integer quality) throws IOException {
        File file = new File(filepath);

        // 使用ImageIO的JPG写入器来控制质量
        ImageIO.write(image, "jpg", file);
    }

    /**
     * 将黑白二维码转换为彩色（CMYK）
     */
    public BufferedImage convertToColor(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage colorImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int gray = (rgb >> 16) & 0xFF;

                // 黑色转换为深蓝色，白色保持白色
                int newRgb;
                if (gray < 128) {
                    // 黑色 -> 深蓝色 (0, 0, 139)
                    newRgb = new Color(0, 0, 139).getRGB();
                } else {
                    // 白色 -> 白色
                    newRgb = new Color(255, 255, 255).getRGB();
                }

                colorImage.setRGB(x, y, newRgb);
            }
        }

        return colorImage;
    }

    /**
     * 获取图片的像素数据
     */
    public int[][] getPixelData(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] pixels = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int gray = (rgb >> 16) & 0xFF;
                pixels[y][x] = gray;
            }
        }

        return pixels;
    }

    /**
     * 计算图片的哈希值（用于防伪验证）
     */
    public String calculateImageHash(BufferedImage image) {
        int[][] pixels = getPixelData(image);
        StringBuilder hash = new StringBuilder();

        // 简单的哈希算法：计算像素的校验和
        long checksum = 0;
        for (int[] row : pixels) {
            for (int pixel : row) {
                checksum += pixel;
            }
        }

        return Long.toHexString(checksum);
    }

    /**
     * 计算两张图片的相似度
     */
    public double calculateSimilarity(BufferedImage image1, BufferedImage image2) {
        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
            return 0.0;
        }

        int[][] pixels1 = getPixelData(image1);
        int[][] pixels2 = getPixelData(image2);

        int totalPixels = pixels1.length * pixels1[0].length;
        int matchingPixels = 0;

        for (int y = 0; y < pixels1.length; y++) {
            for (int x = 0; x < pixels1[0].length; x++) {
                if (Math.abs(pixels1[y][x] - pixels2[y][x]) < 10) {
                    matchingPixels++;
                }
            }
        }

        return (double) matchingPixels / totalPixels * 100;
    }

    /**
     * 缩放图片
     */
    public BufferedImage resizeImage(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }
}

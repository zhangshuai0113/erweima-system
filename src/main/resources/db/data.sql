-- 初始化测试数据
INSERT INTO qrcode_record (content, url, image_path, size_mm, format, is_black_white, anti_fake_version, create_time, remark)
VALUES
('https://example.com/product/001', 'https://example.com/product/001', '/uploads/qrcode/test_001.jpg', 10, 'jpg', TRUE, 1, NOW(), '测试二维码1'),
('https://example.com/product/002', 'https://example.com/product/002', '/uploads/qrcode/test_002.jpg', 12, 'jpg', TRUE, 1, NOW(), '测试二维码2'),
('https://example.com/product/003', 'https://example.com/product/003', '/uploads/qrcode/test_003.jpg', 15, 'jpg', FALSE, 1, NOW(), '测试二维码3');

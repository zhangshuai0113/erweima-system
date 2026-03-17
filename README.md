# 二维码防伪系统 (Anti-Counterfeiting QR Code System)

## 项目概述

这是一个基于 SpringBoot 2.7.8 和 Java 8 的生产级二维码防伪系统。该系统支持二维码的生成、存储和真伪验证，具有完整的防伪特征检测和真伪度计算功能。

## 技术栈

- **框架**: SpringBoot 2.7.8
- **Java版本**: JDK 8
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **二维码库**: ZXing 3.5.1
- **图片处理**: imgscalr
- **JSON处理**: FastJSON
- **日志**: SLF4J + Logback
- **构建工具**: Maven

## 项目结构

```
erweima/
├── src/
│   ├── main/
│   │   ├── java/com/erweima/
│   │   │   ├── ErweimaApplication.java          # 主应用入口
│   │   │   ├── config/
│   │   │   │   └── QrcodeProperties.java        # 配置属性绑定
│   │   │   ├── controller/
│   │   │   │   ├── QrcodeController.java        # 二维码生成API
│   │   │   │   └── VerificationController.java  # 验证API
│   │   │   ├── service/
│   │   │   │   ├── QrcodeGeneratorService.java  # 二维码生成服务
│   │   │   │   └── VerificationService.java     # 验证服务
│   │   │   ├── repository/
│   │   │   │   ├── QrcodeRecordRepository.java  # 二维码数据访问
│   │   │   │   └── VerificationRecordRepository.java # 验证数据访问
│   │   │   ├── entity/
│   │   │   │   ├── QrcodeRecord.java            # 二维码记录实体
│   │   │   │   └── VerificationRecord.java      # 验证记录实体
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java             # 通用API响应
│   │   │   │   ├── QrcodeGenerateRequest.java   # 生成请求DTO
│   │   │   │   ├── QrcodeGenerateResponse.java  # 生成响应DTO
│   │   │   │   ├── VerificationRequest.java     # 验证请求DTO
│   │   │   │   └── VerificationResponse.java    # 验证响应DTO
│   │   │   ├── exception/
│   │   │   │   ├── BusinessException.java       # 业务异常
│   │   │   │   ├── QrcodeNotFoundException.java  # 二维码不存在异常
│   │   │   │   └── VerificationFailedException.java # 验证失败异常
│   │   │   ├── handler/
│   │   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   │   └── util/
│   │   │       ├── ImageUtil.java               # 图片处理工具
│   │   │       └── AntiFakeUtil.java            # 防伪特征工具
│   │   └── resources/
│   │       ├── application.yml                  # 应用配置
│   │       └── db/
│   │           ├── schema.sql                   # 数据库表结构
│   │           └── data.sql                     # 初始化数据
│   └── test/                                    # 测试目录
├── pom.xml                                      # Maven配置
└── README.md                                    # 项目文档
```

## 快速开始

### 1. 环境要求

- JDK 8+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库初始化

```bash
# 创建数据库
CREATE DATABASE erweima CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行SQL脚本
mysql -u root -p erweima < src/main/resources/db/schema.sql
mysql -u root -p erweima < src/main/resources/db/data.sql
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/erweima?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: root
    password: your_password
```

### 4. 编译和运行

```bash
# 编译项目
mvn clean package

# 运行应用
java -jar target/erweima-1.0.0.jar

# 或使用Maven直接运行
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## API 文档

### 二维码生成 API

#### 生成二维码

**请求**:
```
POST /api/qrcode/generate
Content-Type: application/json

{
  "content": "https://example.com/product/001",
  "sizeMm": 10,
  "isBlackWhite": true,
  "quality": 95,
  "enableAntiFake": true,
  "remark": "产品防伪码"
}
```

**响应**:
```json
{
  "code": 0,
  "message": "二维码生成成功",
  "data": {
    "id": 1,
    "content": "https://example.com/product/001",
    "imagePath": "/uploads/qrcode/uuid.jpg",
    "imageBase64": "base64_encoded_image",
    "sizeMm": 10,
    "format": "jpg",
    "isBlackWhite": true,
    "antiFakeVersion": 1,
    "createTime": "2024-01-01T10:00:00"
  },
  "timestamp": 1704110400000
}
```

#### 获取二维码信息

**请求**:
```
GET /api/qrcode/{id}
```

**响应**: 返回二维码详细信息

### 验证 API

#### 验证二维码

**请求**:
```
POST /api/verification/verify
Content-Type: application/json

{
  "qrcodeId": 1,
  "qrcodeContent": "https://example.com/product/001",
  "verifyMethod": 0,
  "verifyIp": "192.168.1.1",
  "deviceInfo": "iPhone 12",
  "remark": "手机拍照验证"
}
```

**响应**:
```json
{
  "code": 0,
  "message": "验证完成",
  "data": {
    "qrcodeId": 1,
    "verifyResult": 0,
    "verifyResultDesc": "正品",
    "authenticityScore": 95.5,
    "status": 1,
    "verifyDetail": "防伪验证通过",
    "verifyTime": "2024-01-01T10:05:00",
    "success": true
  },
  "timestamp": 1704110700000
}
```

**验证结果说明**:
- `0`: 正品 (Genuine)
- `1`: 复印品 (Counterfeit/Copy)
- `2`: 真伪度 (Authenticity Score)

#### 获取验证记录

**请求**:
```
GET /api/verification/{id}
```

**请求**:
```
GET /api/verification/qrcode/{qrcodeId}
```

## 核心功能

### 1. 二维码生成

- 支持自定义内容和URL
- 可配置尺寸（8-15mm）
- 支持黑白和彩色（CMYK）两种格式
- 可配置图片质量（1-100）
- 支持防伪特征自动添加

### 2. 防伪特征

- 在二维码四个角落添加特殊像素标记
- 在中心添加版本信息标记
- 生成SHA-256防伪数据
- 支持防伪特征检测和验证

### 3. 真伪度计算

- 基于防伪特征检测（80分基础分）
- 防伪数据匹配加分（+20分）
- 图片质量评分调整
- 最终得分范围：0-100

### 4. 验证流程

1. 查询原始二维码记录
2. 验证二维码内容是否匹配
3. 检测防伪特征
4. 计算真伪度
5. 保存验证记录
6. 返回验证结果

## 配置说明

### application.yml 配置项

```yaml
qrcode:
  # 上传目录
  uploadDir: /uploads/qrcode

  # 二维码格式
  format: jpg

  # 默认尺寸（毫米/行）
  sizeMm: 10

  # 默认质量（1-100）
  quality: 95

  # 防伪配置
  antiFake:
    enabled: true
    version: 1
```

## 数据库表结构

### qrcode_record 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| content | VARCHAR(500) | 二维码内容 |
| url | VARCHAR(500) | 二维码URL |
| image_path | VARCHAR(500) | 图片路径 |
| size_mm | INT | 尺寸（毫米/行） |
| format | VARCHAR(20) | 格式 |
| is_black_white | BOOLEAN | 是否黑白 |
| anti_fake_version | INT | 防伪版本 |
| anti_fake_data | LONGTEXT | 防伪数据 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR(500) | 备注 |

### verification_record 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| qrcode_id | BIGINT | 二维码ID |
| verify_result | INT | 验证结果 |
| verify_result_desc | VARCHAR(100) | 结果描述 |
| authenticity_score | DOUBLE | 真伪度 |
| status | INT | 验证状态 |
| verify_method | INT | 验证方式 |
| verify_ip | VARCHAR(50) | 验证IP |
| device_info | VARCHAR(500) | 设备信息 |
| verify_detail | LONGTEXT | 验证详情 |
| verify_time | DATETIME | 验证时间 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR(500) | 备注 |

## 异常处理

系统提供了完整的异常处理机制：

- `BusinessException`: 业务异常基类
- `QrcodeNotFoundException`: 二维码不存在异常
- `VerificationFailedException`: 验证失败异常
- `GlobalExceptionHandler`: 全局异常处理器

所有异常都会被转换为标准的 `ApiResponse` 格式返回。

## 日志配置

日志配置在 `application.yml` 中：

```yaml
logging:
  level:
    root: INFO
    com.erweima: DEBUG
  file:
    name: logs/erweima.log
```

## 性能优化

- 使用数据库索引加速查询
- 支持图片压缩和质量控制
- 异步处理防伪特征检测
- 缓存常用配置

## 安全性考虑

- 参数验证和输入检查
- SQL注入防护（使用JPA）
- XSS防护（JSON序列化）
- 异常信息不暴露敏感信息

## 扩展建议

1. **缓存层**: 添加Redis缓存验证结果
2. **消息队列**: 使用RabbitMQ处理异步验证
3. **监控**: 集成Prometheus和Grafana
4. **认证**: 添加JWT或OAuth2认证
5. **限流**: 实现API限流和熔断
6. **文件存储**: 集成OSS或S3存储

## 常见问题

### Q: 如何修改二维码尺寸？
A: 在请求中设置 `sizeMm` 参数，范围为 8-15mm。

### Q: 防伪特征如何工作？
A: 系统在二维码四个角落添加特殊像素标记，验证时检测这些标记来判断真伪。

### Q: 真伪度如何计算？
A: 基于防伪特征检测（80分）+ 防伪数据匹配（20分）+ 图片质量调整。

### Q: 支持哪些图片格式？
A: 目前支持JPG和PNG格式。

## 许可证

MIT License

## 联系方式

如有问题或建议，请联系开发团队。

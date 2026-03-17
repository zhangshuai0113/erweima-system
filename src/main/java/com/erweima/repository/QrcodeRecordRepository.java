package com.erweima.repository;

import com.erweima.entity.QrcodeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 二维码记录Repository
 */
@Repository
public interface QrcodeRecordRepository extends JpaRepository<QrcodeRecord, Long> {

    /**
     * 根据URL查询
     */
    Optional<QrcodeRecord> findByUrl(String url);

    /**
     * 根据内容查询
     */
    List<QrcodeRecord> findByContent(String content);

    /**
     * 查询时间范围内的记录
     */
    List<QrcodeRecord> findByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询黑白二维码
     */
    List<QrcodeRecord> findByIsBlackWhiteTrue();

    /**
     * 查询彩色二维码
     */
    List<QrcodeRecord> findByIsBlackWhiteFalse();
}

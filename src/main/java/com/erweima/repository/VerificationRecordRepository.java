package com.erweima.repository;

import com.erweima.entity.VerificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 验证记录Repository
 */
@Repository
public interface VerificationRecordRepository extends JpaRepository<VerificationRecord, Long> {

    /**
     * 根据二维码ID查询验证记录
     */
    List<VerificationRecord> findByQrcodeId(Long qrcodeId);

    /**
     * 根据验证结果查询
     */
    List<VerificationRecord> findByVerifyResult(Integer verifyResult);

    /**
     * 查询时间范围内的验证记录
     */
    List<VerificationRecord> findByVerifyTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询正品记录
     */
    List<VerificationRecord> findByVerifyResultAndStatus(Integer verifyResult, Integer status);
}

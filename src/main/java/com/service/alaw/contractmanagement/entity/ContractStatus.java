package com.service.alaw.contractmanagement.entity;

public enum ContractStatus {
    PENDING,    // 업로드 완료, 분석 대기 중 (Queue 진입)
    PROCESSING, // AI가 분석 중
    COMPLETED,  // 분석 완료 (결과 조회 가능)
    FAILED      // 분석 실패 (OCR 에러 등)
}

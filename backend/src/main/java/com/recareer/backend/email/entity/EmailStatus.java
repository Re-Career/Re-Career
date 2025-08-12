package com.recareer.backend.email.entity;

public enum EmailStatus {
    PENDING,    // 전송 대기
    SENDING,    // 전송 중
    SENT,       // 전송 완료
    FAILED      // 전송 실패
}
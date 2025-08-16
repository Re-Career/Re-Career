package com.recareer.backend.mentoringRecord.entity;

public enum MentoringRecordStatus {
    RECORD_PENDING,     // 멘토링 기록 생성 대기 (Reservation이 COMPLETED된 직후)
    AUDIO_PENDING,      // 녹음 파일 업로드 대기
    AUDIO_PROCESSING,   // 녹음 파일 업로드 완료, AI 전사/요약 처리 중
    AUDIO_COMPLETED,    // AI 전사/요약 완료
    FEEDBACK_PENDING,   // 멘티 피드백 작성 대기
    ALL_COMPLETED       // 모든 후속 처리 완료
}
package com.recareer.backend.reservation.entity;

public enum ReservationStatus {
  REQUESTED, // 멘토링 요청
  CONFIRMED, // 멘토링 확인
  CANCELED, // 멘토링 취소 (노쇼 포함)
  COMPLETED // 멘토링 완료
}

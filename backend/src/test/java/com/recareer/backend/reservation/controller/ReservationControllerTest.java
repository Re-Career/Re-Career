package com.recareer.backend.reservation.controller;

import com.recareer.backend.auth.util.AuthUtil;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.position.entity.Position;
import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.dto.ReservationResponseDto;
import com.recareer.backend.reservation.dto.ReservationUpdateRequestDto;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.entity.ReservationStatus;
import com.recareer.backend.reservation.service.ReservationService;
import com.recareer.backend.response.ApiResponse;
import com.recareer.backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * ReservationController 단위 테스트
 * 
 * 멘토링 예약 컨트롤러의 비즈니스 로직을 테스트합니다.
 * - 인증 및 권한 검증 로직
 * - 응답 데이터 구조 및 HTTP 상태 코드
 * - 서비스 계층과의 통합
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationController 단위 테스트")
class ReservationControllerTest {

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationService reservationService;

    @Mock
    private AuthUtil authUtil;


    @Mock
    private Reservation mockReservation;

    // 테스트 데이터
    private String validToken;
    private String invalidToken;
    private Long userId;
    private Long mentorUserId;
    private Long reservationId;
    private User testUser;
    private User testMentorUser;
    private Mentor testMentor;
    private Reservation testReservation;
    private ReservationRequestDto testRequestDto;
    private ReservationResponseDto testResponseDto;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        validToken = "Bearer valid.jwt.token";
        invalidToken = "Bearer invalid.jwt.token";
        userId = 1L;
        mentorUserId = 2L;
        reservationId = 1L;
        testDateTime = LocalDateTime.of(2024, 1, 15, 14, 0);

        testUser = User.builder()
            .id(userId)
            .name("김멘티")
            .email("mentee@test.com")
            .build();

        testMentorUser = User.builder()
            .id(mentorUserId)
            .name("박멘토")
            .email("mentor@test.com")
            .build();

        Position position = Position.builder().id(1L).name("Software Engineer").build();

        testMentor = Mentor.builder()
            .id(1L)
            .user(testMentorUser)
            .position(position)
            .build();

        testReservation = Reservation.builder()
            .id(reservationId)
            .user(testUser)
            .mentor(testMentor)
            .reservationTime(testDateTime)
            .status(ReservationStatus.REQUESTED)
            .build();

        testRequestDto = ReservationRequestDto.builder()
            .userId(userId)
            .mentorId(1L)
            .reservationTime(testDateTime)
            .build();

        testResponseDto = ReservationResponseDto.from(testReservation);
    }

    @Nested
    @DisplayName("예약 목록 조회 API 테스트")
    class GetReservationsByUserIdTest {

        @Test
        @DisplayName("유효한 토큰으로 본인 예약 목록 조회 성공")
        void getReservationsByUserId_ValidToken_Success() {
            // Given
            List<ReservationResponseDto> reservations = Arrays.asList(testResponseDto);
            given(authUtil.validateTokenAndGetUserId(validToken)).willReturn(userId);
            given(reservationService.findAllReservationsByUserId(userId)).willReturn(reservations);

            // When
            ResponseEntity<ApiResponse<List<ReservationResponseDto>>> response = 
                reservationController.getReservations(validToken, userId);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getMessage()).isEqualTo("요청이 성공적으로 처리되었습니다.");
            assertThat(response.getBody().getData()).hasSize(1);
            assertThat(response.getBody().getData().get(0).getReservationId()).isEqualTo(reservationId);

            verify(authUtil).validateTokenAndGetUserId(validToken);
            verify(reservationService).findAllReservationsByUserId(userId);
        }

        @Test
        @DisplayName("다른 사용자의 예약 목록 조회 시 403 Forbidden")
        void getReservationsByUserId_OtherUser_Forbidden() {
            // Given
            Long otherUserId = 999L;
            given(authUtil.validateTokenAndGetUserId(validToken)).willReturn(userId);

            // When
            ResponseEntity<ApiResponse<List<ReservationResponseDto>>> response = 
                reservationController.getReservations(validToken, otherUserId);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getMessage()).isEqualTo("본인의 예약 목록만 조회할 수 있습니다");
            assertThat(response.getBody().getData()).isNull();

            verify(authUtil).validateTokenAndGetUserId(validToken);
            verify(reservationService, never()).findAllReservationsByUserId(any());
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 조회 시 401 Unauthorized")
        void getReservationsByUserId_InvalidToken_Unauthorized() {
            // Given
            given(authUtil.validateTokenAndGetUserId(invalidToken))
                .willThrow(new IllegalArgumentException("유효하지 않은 토큰입니다"));

            // When
            ResponseEntity<ApiResponse<List<ReservationResponseDto>>> response = 
                reservationController.getReservations(invalidToken, userId);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getMessage()).isEqualTo("유효하지 않은 토큰입니다");
            assertThat(response.getBody().getData()).isNull();

            verify(authUtil).validateTokenAndGetUserId(invalidToken);
            verify(reservationService, never()).findAllReservationsByUserId(any());
        }
    }

    @Nested
    @DisplayName("예약 생성 API 테스트")
    class CreateReservationTest {

        @Test
        @DisplayName("유효한 토큰으로 예약 생성 성공")
        void createReservation_ValidToken_Success() {
            // Given
            given(authUtil.validateTokenAndGetUserId(validToken)).willReturn(userId);
            given(reservationService.createReservation(any(ReservationRequestDto.class))).willReturn(reservationId);

            // When
            ResponseEntity<ApiResponse<Long>> response = 
                reservationController.createReservation(validToken, testRequestDto);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getMessage()).isEqualTo("요청이 성공적으로 처리되었습니다.");
            assertThat(response.getBody().getData()).isEqualTo(reservationId);

            verify(authUtil).validateTokenAndGetUserId(validToken);
            verify(reservationService).createReservation(any(ReservationRequestDto.class));
        }

        @Test
        @DisplayName("다른 사용자 이름으로 예약 생성 시 403 Forbidden")
        void createReservation_OtherUser_Forbidden() {
            // Given
            Long otherUserId = 999L;
            ReservationRequestDto otherUserRequestDto = ReservationRequestDto.builder()
                .userId(otherUserId)
                .mentorId(1L)
                .reservationTime(testDateTime)
                .build();

            given(authUtil.validateTokenAndGetUserId(validToken)).willReturn(userId);

            // When
            ResponseEntity<ApiResponse<Long>> response = 
                reservationController.createReservation(validToken, otherUserRequestDto);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getMessage()).isEqualTo("본인의 이름으로만 예약할 수 있습니다");
            assertThat(response.getBody().getData()).isNull();

            verify(authUtil).validateTokenAndGetUserId(validToken);
            verify(reservationService, never()).createReservation(any());
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 예약 생성 시 401 Unauthorized")
        void createReservation_InvalidToken_Unauthorized() {
            // Given
            given(authUtil.validateTokenAndGetUserId(invalidToken))
                .willThrow(new IllegalArgumentException("유효하지 않은 토큰입니다"));

            // When
            ResponseEntity<ApiResponse<Long>> response = 
                reservationController.createReservation(invalidToken, testRequestDto);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getMessage()).isEqualTo("유효하지 않은 토큰입니다");
            assertThat(response.getBody().getData()).isNull();

            verify(authUtil).validateTokenAndGetUserId(invalidToken);
            verify(reservationService, never()).createReservation(any());
        }
    }

    @Nested
    @DisplayName("예약 상태 업데이트 API 테스트")
    class UpdateReservationStatusTest {

        @Test
        @DisplayName("멘토가 예약 승인 시 성공")
        void updateReservationStatus_MentorConfirm_Success() {
            // Given
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.CONFIRMED);

            given(authUtil.validateTokenAndGetUserId(validToken)).willReturn(mentorUserId);
            given(reservationService.findById(reservationId)).willReturn(mockReservation);
            given(mockReservation.isMentorParticipant(mentorUserId)).willReturn(true);
            doNothing().when(reservationService).updateReservationStatus(eq(reservationId), any(ReservationUpdateRequestDto.class));

            // When
            ResponseEntity<ApiResponse<String>> response = 
                reservationController.updateReservationStatus(validToken, reservationId, updateDto);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getMessage()).isEqualTo("요청이 성공적으로 처리되었습니다.");
            assertThat(response.getBody().getData()).isEqualTo("멘토링이 수락되었습니다.");

            verify(authUtil).validateTokenAndGetUserId(validToken);
            verify(reservationService).findById(reservationId);
            verify(reservationService).updateReservationStatus(eq(reservationId), any(ReservationUpdateRequestDto.class));
        }

        @Test
        @DisplayName("멘토가 예약 완료 처리 시 성공")
        void updateReservationStatus_MentorComplete_Success() {
            // Given
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.COMPLETED);

            given(authUtil.validateTokenAndGetUserId(validToken)).willReturn(mentorUserId);
            given(reservationService.findById(reservationId)).willReturn(mockReservation);
            given(mockReservation.isMentorParticipant(mentorUserId)).willReturn(true);
            doNothing().when(reservationService).updateReservationStatus(eq(reservationId), any(ReservationUpdateRequestDto.class));

            // When
            ResponseEntity<ApiResponse<String>> response = 
                reservationController.updateReservationStatus(validToken, reservationId, updateDto);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getMessage()).isEqualTo("요청이 성공적으로 처리되었습니다.");
            assertThat(response.getBody().getData()).isEqualTo("멘토링이 완료되었습니다.");

            verify(authUtil).validateTokenAndGetUserId(validToken);
            verify(reservationService).findById(reservationId);
            verify(reservationService).updateReservationStatus(eq(reservationId), any(ReservationUpdateRequestDto.class));
        }

        @Test
        @DisplayName("멘토가 예약 취소 시 성공")
        void updateReservationStatus_MentorCancel_Success() {
            // Given
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.CANCELED);
            updateDto.setCancelReason("일정 변경으로 인한 취소");

            given(authUtil.validateTokenAndGetUserId(validToken)).willReturn(mentorUserId);
            given(reservationService.findById(reservationId)).willReturn(mockReservation);
            given(mockReservation.isMentorParticipant(mentorUserId)).willReturn(true);
            doNothing().when(reservationService).updateReservationStatus(eq(reservationId), any(ReservationUpdateRequestDto.class));

            // When
            ResponseEntity<ApiResponse<String>> response = 
                reservationController.updateReservationStatus(validToken, reservationId, updateDto);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getMessage()).isEqualTo("요청이 성공적으로 처리되었습니다.");
            assertThat(response.getBody().getData()).isEqualTo("멘토링이 취소되었습니다.");

            verify(authUtil).validateTokenAndGetUserId(validToken);
            verify(reservationService).findById(reservationId);
            verify(reservationService).updateReservationStatus(eq(reservationId), any(ReservationUpdateRequestDto.class));
        }

        @Test
        @DisplayName("멘토가 아닌 사용자가 상태 변경 시 403 Forbidden")
        void updateReservationStatus_NotMentor_Forbidden() {
            // Given
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.CONFIRMED);

            given(authUtil.validateTokenAndGetUserId(validToken)).willReturn(userId); // 멘티 ID
            given(reservationService.findById(reservationId)).willReturn(mockReservation);
            given(mockReservation.isMentorParticipant(userId)).willReturn(false);

            // When
            ResponseEntity<ApiResponse<String>> response = 
                reservationController.updateReservationStatus(validToken, reservationId, updateDto);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getMessage()).isEqualTo("해당 멘토링의 멘토만 상태를 변경할 수 있습니다");
            assertThat(response.getBody().getData()).isNull();

            verify(authUtil).validateTokenAndGetUserId(validToken);
            verify(reservationService).findById(reservationId);
            verify(reservationService, never()).updateReservationStatus(any(), any());
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 상태 변경 시 401 Unauthorized")
        void updateReservationStatus_InvalidToken_Unauthorized() {
            // Given
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.CONFIRMED);

            given(authUtil.validateTokenAndGetUserId(invalidToken))
                .willThrow(new IllegalArgumentException("유효하지 않은 토큰입니다"));

            // When
            ResponseEntity<ApiResponse<String>> response = 
                reservationController.updateReservationStatus(invalidToken, reservationId, updateDto);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getMessage()).isEqualTo("유효하지 않은 토큰입니다");
            assertThat(response.getBody().getData()).isNull();

            verify(authUtil).validateTokenAndGetUserId(invalidToken);
            verify(reservationService, never()).findById(any());
            verify(reservationService, never()).updateReservationStatus(any(), any());
        }
    }

}
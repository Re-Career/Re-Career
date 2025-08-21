package com.recareer.backend.reservation.service;

import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.entity.MentoringRecordStatus;
import com.recareer.backend.mentoringRecord.repository.MentoringRecordRepository;
import com.recareer.backend.position.entity.Position;
import com.recareer.backend.reservation.dto.ReservationRequestDto;
import com.recareer.backend.reservation.dto.ReservationResponseDto;
import com.recareer.backend.reservation.dto.ReservationUpdateRequestDto;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.entity.ReservationStatus;
import com.recareer.backend.reservation.repository.ReservationRepository;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * ReservationServiceImpl 테스트
 * 
 * 멘토링 예약 서비스의 핵심 기능들을 테스트합니다.
 * - 예약 생성, 조회, 상태 변경 등의 정상 케이스
 * - 존재하지 않는 엔티티, 잘못된 상태 전환 등의 예외 케이스
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService 테스트")
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private MentoringRecordRepository mentoringRecordRepository;


    @InjectMocks
    private ReservationServiceImpl reservationService;

    // 테스트 데이터
    private User testUser;
    private User testMentorUser;
    private Mentor testMentor;
    private Reservation testReservation;
    private ReservationRequestDto testRequestDto;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2024, 1, 15, 14, 0);
        
        // 테스트용 멘티 사용자
        testUser = User.builder()
            .id(1L)
            .name("김멘티")
            .email("mentee@test.com")
            .build();
        
        // 테스트용 멘토 사용자
        testMentorUser = User.builder()
            .id(2L)
            .name("박멘토")
            .email("mentor@test.com")
            .build();
        
        Position position = Position.builder().id(1L).name("Software Engineer").build();

        // 테스트용 멘토
        testMentor = Mentor.builder()
            .id(1L)
            .user(testMentorUser)
            .position(position)
            .build();
        
        // 테스트용 예약
        testReservation = Reservation.builder()
            .id(1L)
            .user(testUser)
            .mentor(testMentor)
            .reservationTime(testDateTime)
            .status(ReservationStatus.REQUESTED)
            .build();
        
        // 테스트용 예약 요청 DTO
        testRequestDto = ReservationRequestDto.builder()
            .userId(1L)
            .mentorId(1L)
            .reservationTime(testDateTime)
            .build();
    }

    @Nested
    @DisplayName("예약 조회 테스트")
    class FindReservationsTest {

        @Test
        @DisplayName("사용자 ID로 예약 목록 조회 성공")
        void findAllReservationsByUserId_Success() {
            // Given
            Long userId = 1L;
            List<Reservation> reservations = Arrays.asList(testReservation);
            given(reservationRepository.findAllByUserId(userId)).willReturn(reservations);

            // When
            List<ReservationResponseDto> result = reservationService.findAllReservationsByUserId(userId);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isNotNull();
            verify(reservationRepository).findAllByUserId(userId);
        }

        @Test
        @DisplayName("존재하지 않는 사용자의 예약 조회 - 빈 목록 반환")
        void findAllReservationsByUserId_EmptyList() {
            // Given
            Long userId = 999L;
            given(reservationRepository.findAllByUserId(userId)).willReturn(Arrays.asList());

            // When
            List<ReservationResponseDto> result = reservationService.findAllReservationsByUserId(userId);

            // Then
            assertThat(result).isEmpty();
            verify(reservationRepository).findAllByUserId(userId);
        }

        @Test
        @DisplayName("예약 ID로 예약 조회 성공")
        void findById_Success() {
            // Given
            Long reservationId = 1L;
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));

            // When
            Reservation result = reservationService.findById(reservationId);

            // Then
            assertThat(result).isEqualTo(testReservation);
            verify(reservationRepository).findById(reservationId);
        }

        @Test
        @DisplayName("존재하지 않는 예약 ID 조회 시 예외 발생")
        void findById_NotFound_ThrowsException() {
            // Given
            Long reservationId = 999L;
            given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> reservationService.findById(reservationId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 예약을 찾을 수 없습니다.");
            
            verify(reservationRepository).findById(reservationId);
        }
    }

    @Nested
    @DisplayName("예약 생성 테스트")
    class CreateReservationTest {

        @Test
        @DisplayName("예약 생성 성공")
        void createReservation_Success() {
            // Given
            given(mentorRepository.findById(testRequestDto.getMentorId())).willReturn(Optional.of(testMentor));
            given(userRepository.findById(testRequestDto.getUserId())).willReturn(Optional.of(testUser));
            given(reservationRepository.save(any(Reservation.class))).willReturn(testReservation);

            // When
            Long result = reservationService.createReservation(testRequestDto);

            // Then
            assertThat(result).isEqualTo(testReservation.getId());
            verify(mentorRepository).findById(testRequestDto.getMentorId());
            verify(userRepository).findById(testRequestDto.getUserId());
            verify(reservationRepository).save(any(Reservation.class));
        }

        @Test
        @DisplayName("존재하지 않는 멘토로 예약 생성 시 예외 발생")
        void createReservation_MentorNotFound_ThrowsException() {
            // Given
            given(mentorRepository.findById(testRequestDto.getMentorId())).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> reservationService.createReservation(testRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 멘토를 찾을 수 없습니다.");
            
            verify(mentorRepository).findById(testRequestDto.getMentorId());
            verify(userRepository, never()).findById(any());
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 사용자로 예약 생성 시 예외 발생")
        void createReservation_UserNotFound_ThrowsException() {
            // Given
            given(mentorRepository.findById(testRequestDto.getMentorId())).willReturn(Optional.of(testMentor));
            given(userRepository.findById(testRequestDto.getUserId())).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> reservationService.createReservation(testRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 유저를 찾을 수 없습니다.");
            
            verify(mentorRepository).findById(testRequestDto.getMentorId());
            verify(userRepository).findById(testRequestDto.getUserId());
            verify(reservationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("예약 상태 업데이트 테스트")
    class UpdateReservationStatusTest {

        @Test
        @DisplayName("예약 승인(CONFIRMED) 성공")
        void updateReservationStatus_ToConfirmed_Success() {
            // Given
            Long reservationId = 1L;
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.CONFIRMED);
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));
            given(reservationRepository.save(any(Reservation.class))).willReturn(testReservation);

            // When
            reservationService.updateReservationStatus(reservationId, updateDto);

            // Then
            assertThat(testReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
            
            verify(reservationRepository).findById(reservationId);
            verify(reservationRepository).save(testReservation);
        }


        @Test
        @DisplayName("이미 승인된 예약을 다시 승인하려 할 때 예외 발생")
        void updateReservationStatus_AlreadyConfirmed_ThrowsException() {
            // Given
            testReservation.setStatus(ReservationStatus.CONFIRMED);
            Long reservationId = 1L;
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.CONFIRMED);
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));

            // When & Then
            assertThatThrownBy(() -> reservationService.updateReservationStatus(reservationId, updateDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("요청된 상태의 예약만 수락할 수 있습니다.");
            
            verify(reservationRepository).findById(reservationId);
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("예약 완료(COMPLETED) 성공 - 멘토링 기록 생성")
        void updateReservationStatus_ToCompleted_Success() {
            // Given
            testReservation.setStatus(ReservationStatus.CONFIRMED);
            Long reservationId = 1L;
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.COMPLETED);
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));
            given(reservationRepository.save(any(Reservation.class))).willReturn(testReservation);
            given(mentoringRecordRepository.existsByReservationId(reservationId)).willReturn(false);

            // When
            reservationService.updateReservationStatus(reservationId, updateDto);

            // Then
            assertThat(testReservation.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
            
            verify(reservationRepository).findById(reservationId);
            verify(mentoringRecordRepository).existsByReservationId(reservationId);
            verify(mentoringRecordRepository).save(any(MentoringRecord.class));
            verify(reservationRepository).save(testReservation);
        }

        @Test
        @DisplayName("확인되지 않은 예약을 완료하려 할 때 예외 발생")
        void updateReservationStatus_NotConfirmedToCompleted_ThrowsException() {
            // Given
            Long reservationId = 1L;
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.COMPLETED);
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));

            // When & Then
            assertThatThrownBy(() -> reservationService.updateReservationStatus(reservationId, updateDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("확인된 상태의 예약만 완료할 수 있습니다.");
            
            verify(reservationRepository).findById(reservationId);
            verify(mentoringRecordRepository, never()).existsByReservationId(any());
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("예약 취소(CANCELED) 성공")
        void updateReservationStatus_ToCanceled_Success() {
            // Given
            Long reservationId = 1L;
            String cancelReason = "개인 사정으로 인한 취소";
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.CANCELED);
            updateDto.setCancelReason(cancelReason);
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));
            given(reservationRepository.save(any(Reservation.class))).willReturn(testReservation);

            // When
            reservationService.updateReservationStatus(reservationId, updateDto);

            // Then
            assertThat(testReservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
            assertThat(testReservation.getCancelReason()).isEqualTo(cancelReason);
            
            verify(reservationRepository).findById(reservationId);
            verify(reservationRepository).save(testReservation);
        }

        @Test
        @DisplayName("취소 사유 없이 예약 취소 시 예외 발생")
        void updateReservationStatus_ToCanceledWithoutReason_ThrowsException() {
            // Given
            Long reservationId = 1L;
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.CANCELED);
            updateDto.setCancelReason(null); // 취소 사유 없음
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));

            // When & Then
            assertThatThrownBy(() -> reservationService.updateReservationStatus(reservationId, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("취소 사유는 필수입니다.");
            
            verify(reservationRepository).findById(reservationId);
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("빈 취소 사유로 예약 취소 시 예외 발생")
        void updateReservationStatus_ToCanceledWithEmptyReason_ThrowsException() {
            // Given
            Long reservationId = 1L;
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.CANCELED);
            updateDto.setCancelReason("   "); // 공백만 있는 취소 사유
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));

            // When & Then
            assertThatThrownBy(() -> reservationService.updateReservationStatus(reservationId, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("취소 사유는 필수입니다.");
            
            verify(reservationRepository).findById(reservationId);
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("완료된 예약을 취소하려 할 때 예외 발생")
        void updateReservationStatus_CompletedToCanceled_ThrowsException() {
            // Given
            testReservation.setStatus(ReservationStatus.COMPLETED);
            Long reservationId = 1L;
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.CANCELED);
            updateDto.setCancelReason("취소 사유");
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));

            // When & Then
            assertThatThrownBy(() -> reservationService.updateReservationStatus(reservationId, updateDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("완료된 멘토링은 취소할 수 없습니다.");
            
            verify(reservationRepository).findById(reservationId);
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("지원하지 않는 상태로 변경 시 예외 발생")
        void updateReservationStatus_UnsupportedStatus_ThrowsException() {
            // Given
            Long reservationId = 1L;
            // 실제로는 enum에 없는 값이지만, 테스트를 위해 null로 설정
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(null);
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));

            // When & Then
            assertThatThrownBy(() -> reservationService.updateReservationStatus(reservationId, updateDto))
                .isInstanceOf(NullPointerException.class);
            
            verify(reservationRepository).findById(reservationId);
            verify(reservationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("멘토링 기록 생성 테스트")
    class CreateMentoringRecordTest {

        @Test
        @DisplayName("멘토링 기록이 없을 때 새로 생성")
        void createMentoringRecordIfNotExists_NotExists_CreateNew() {
            // Given
            testReservation.setStatus(ReservationStatus.CONFIRMED);
            Long reservationId = 1L;
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.COMPLETED);
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));
            given(reservationRepository.save(any(Reservation.class))).willReturn(testReservation);
            given(mentoringRecordRepository.existsByReservationId(reservationId)).willReturn(false);

            // When
            reservationService.updateReservationStatus(reservationId, updateDto);

            // Then
            verify(mentoringRecordRepository).existsByReservationId(reservationId);
            verify(mentoringRecordRepository).save(argThat(record -> 
                record.getReservation().equals(testReservation) &&
                record.getStatus() == MentoringRecordStatus.AUDIO_PENDING
            ));
        }

        @Test
        @DisplayName("멘토링 기록이 이미 존재할 때 생성하지 않음")
        void createMentoringRecordIfNotExists_AlreadyExists_DoNotCreate() {
            // Given
            testReservation.setStatus(ReservationStatus.CONFIRMED);
            Long reservationId = 1L;
            ReservationUpdateRequestDto updateDto = new ReservationUpdateRequestDto();
            updateDto.setStatus(ReservationStatus.COMPLETED);
            
            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(testReservation));
            given(reservationRepository.save(any(Reservation.class))).willReturn(testReservation);
            given(mentoringRecordRepository.existsByReservationId(reservationId)).willReturn(true);

            // When
            reservationService.updateReservationStatus(reservationId, updateDto);

            // Then
            verify(mentoringRecordRepository).existsByReservationId(reservationId);
            verify(mentoringRecordRepository, never()).save(any(MentoringRecord.class));
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("null ID로 예약 조회 시 적절한 처리")
        void findById_NullId_HandledProperly() {
            // Given
            given(reservationRepository.findById(null)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> reservationService.findById(null))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 예약을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("음수 ID로 예약 조회 시 적절한 처리")
        void findById_NegativeId_HandledProperly() {
            // Given
            Long negativeId = -1L;
            given(reservationRepository.findById(negativeId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> reservationService.findById(negativeId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 예약을 찾을 수 없습니다.");
        }
    }
}
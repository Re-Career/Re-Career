package com.recareer.backend.mentoringRecord.service;

import com.recareer.backend.common.service.S3Service;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordRequestDto;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordResponseDto;
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.entity.MentoringRecordStatus;
import com.recareer.backend.mentoringRecord.repository.MentoringRecordRepository;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.entity.ReservationStatus;
import com.recareer.backend.reservation.repository.ReservationRepository;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.repository.UserRepository;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.position.entity.Position;
import com.recareer.backend.position.repository.PositionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class MentoringRecordServiceTest {

    @Mock
    private MentoringRecordRepository mentoringRecordRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private AudioTranscriptionService audioTranscriptionService;

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private MentoringRecordServiceImpl mentoringRecordService;

    private User mentorUser;
    private User menteeUser;
    private Mentor mentor;
    private Reservation reservation;
    private MentoringRecord mentoringRecord;

    @BeforeEach
    void setUp() {
        mentorUser = User.builder()
                .id(1L)
                .name("멘토 김철수")
                .email("mentor@test.com")
                .role(Role.MENTOR)
                .provider("google")
                .providerId("mentor123")
                .profileImageUrl("https://example.com/mentor.jpg")
                .build();

        menteeUser = User.builder()
                .id(2L)
                .name("멘티 이영희")
                .email("mentee@test.com")
                .role(Role.MENTEE)
                .provider("google")
                .providerId("mentee123")
                .profileImageUrl("https://example.com/mentee.jpg")
                .build();

        mentor = Mentor.builder()
                .id(mentorUser.getId())
                .user(mentorUser)
                .position(createPosition("시니어 백엔드 개발자"))
                .description("5년차 백엔드 개발자입니다.")
                .isVerified(true)
                .build();

        reservation = Reservation.builder()
                .id(1L)
                .mentor(mentor)
                .user(menteeUser)
                .reservationTime(LocalDateTime.of(2025, 8, 15, 14, 0))
                .status(ReservationStatus.COMPLETED)
                .build();

        mentoringRecord = MentoringRecord.builder()
                .id(1L)
                .reservation(reservation)
                .status(MentoringRecordStatus.AUDIO_PENDING)
                .build();
    }

    @Test
    @DisplayName("ID로 멘토링 기록 조회 성공")
    void findMentoringRecordById_Success() {
        when(mentoringRecordRepository.findById(1L)).thenReturn(Optional.of(mentoringRecord));

        MentoringRecord result = mentoringRecordService.findMentoringRecordById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getReservation().getId()).isEqualTo(1L);
        verify(mentoringRecordRepository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 멘토링 기록 조회 시 예외 발생")
    void findMentoringRecordById_NotFound() {
        when(mentoringRecordRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentoringRecordService.findMentoringRecordById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 멘토링 기록을 찾을 수 없습니다.");

        verify(mentoringRecordRepository).findById(999L);
    }

    @Test
    @DisplayName("새로운 멘토링 기록 생성 및 피드백 저장 성공")
    void createOrUpdateMentoringRecord_CreateNew() {
        MentoringRecordRequestDto requestDto = MentoringRecordRequestDto.builder()
                .menteeFeedback("멘토링이 매우 도움이 되었습니다!")
                .build();

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(mentoringRecordRepository.findByReservationId(1L)).thenReturn(Optional.empty());
        when(mentoringRecordRepository.save(any(MentoringRecord.class))).thenReturn(mentoringRecord);

        Long result = mentoringRecordService.createOrUpdateMentoringRecord(1L, requestDto);

        assertThat(result).isEqualTo(1L);
        verify(reservationRepository).findById(1L);
        verify(mentoringRecordRepository).findByReservationId(1L);
        verify(mentoringRecordRepository).save(any(MentoringRecord.class));
    }

    @Test
    @DisplayName("기존 멘토링 기록 업데이트 성공")
    void createOrUpdateMentoringRecord_UpdateExisting() {
        MentoringRecordRequestDto requestDto = MentoringRecordRequestDto.builder()
                .menteeFeedback("업데이트된 피드백입니다.")
                .build();

        mentoringRecord.setAudioFileUrl("https://s3.amazonaws.com/audio.mp3");

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(mentoringRecordRepository.findByReservationId(1L)).thenReturn(Optional.of(mentoringRecord));
        when(mentoringRecordRepository.save(any(MentoringRecord.class))).thenReturn(mentoringRecord);

        Long result = mentoringRecordService.createOrUpdateMentoringRecord(1L, requestDto);

        assertThat(result).isEqualTo(1L);
        assertThat(mentoringRecord.getMenteeFeedback()).isEqualTo("업데이트된 피드백입니다.");
        assertThat(mentoringRecord.getStatus()).isEqualTo(MentoringRecordStatus.ALL_COMPLETED);
        verify(mentoringRecordRepository).save(mentoringRecord);
    }

    @Test
    @DisplayName("존재하지 않는 예약으로 멘토링 기록 생성 시 예외 발생")
    void createOrUpdateMentoringRecord_ReservationNotFound() {
        MentoringRecordRequestDto requestDto = MentoringRecordRequestDto.builder()
                .menteeFeedback("피드백")
                .build();

        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentoringRecordService.createOrUpdateMentoringRecord(999L, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 예약을 찾을 수 없습니다.");

        verify(reservationRepository).findById(999L);
    }

    @Test
    @DisplayName("오디오 파일 업로드 및 처리 성공")
    void uploadAudioAndProcess_Success() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "audio", "test-audio.mp3", "audio/mpeg", "test audio content".getBytes());

        String audioFileUrl = "https://s3.amazonaws.com/audio.mp3";
        String transcribedText = "전사된 텍스트입니다.";
        String summary = "상담 내용 요약입니다.";

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(mentoringRecordRepository.findByReservationId(1L)).thenReturn(Optional.of(mentoringRecord));
        when(s3Service.uploadAudioFile(audioFile)).thenReturn(audioFileUrl);
        when(audioTranscriptionService.transcribeAudio(audioFile)).thenReturn(transcribedText);
        when(audioTranscriptionService.summarizeText(transcribedText)).thenReturn(summary);
        when(mentoringRecordRepository.save(any(MentoringRecord.class))).thenReturn(mentoringRecord);

        Long result = mentoringRecordService.uploadAudioAndProcess(1L, audioFile);

        assertThat(result).isEqualTo(1L);
        verify(s3Service).uploadAudioFile(audioFile);
        verify(audioTranscriptionService).transcribeAudio(audioFile);
        verify(audioTranscriptionService).summarizeText(transcribedText);
        verify(mentoringRecordRepository, times(2)).save(any(MentoringRecord.class));
    }


    @Test
    @DisplayName("멘토링 기록 상태 업데이트 성공")
    void updateMentoringRecordStatus_Success() {
        when(mentoringRecordRepository.findById(1L)).thenReturn(Optional.of(mentoringRecord));
        when(mentoringRecordRepository.save(any(MentoringRecord.class))).thenReturn(mentoringRecord);

        mentoringRecordService.updateMentoringRecordStatus(1L, MentoringRecordStatus.ALL_COMPLETED);

        assertThat(mentoringRecord.getStatus()).isEqualTo(MentoringRecordStatus.ALL_COMPLETED);
        verify(mentoringRecordRepository).findById(1L);
        verify(mentoringRecordRepository).save(mentoringRecord);
    }

    @Test
    @DisplayName("예약 ID로 멘토링 기록 조회 성공")
    void findByReservationId_Success() {
        when(mentoringRecordRepository.findByReservationId(1L)).thenReturn(Optional.of(mentoringRecord));

        MentoringRecord result = mentoringRecordService.findByReservationId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getReservation().getId()).isEqualTo(1L);
        verify(mentoringRecordRepository).findByReservationId(1L);
    }

    @Test
    @DisplayName("예약 ID로 멘토링 기록 조회 시 기록이 없으면 예외 발생")
    void findByReservationId_NotFound() {
        when(mentoringRecordRepository.findByReservationId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentoringRecordService.findByReservationId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 예약에 대한 멘토링 기록을 찾을 수 없습니다.");

        verify(mentoringRecordRepository).findByReservationId(999L);
    }

    @Test
    @DisplayName("사용자 ID로 완료된 멘토링 기록 목록 조회 성공")
    void findCompletedMentoringRecordsByUserId_Success() {
        MentoringRecord completedRecord1 = MentoringRecord.builder()
                .id(1L)
                .reservation(reservation)
                .status(MentoringRecordStatus.ALL_COMPLETED)
                .menteeFeedback("첫 번째 피드백")
                .summary("첫 번째 요약")
                .build();

        MentoringRecord completedRecord2 = MentoringRecord.builder()
                .id(2L)
                .reservation(reservation)
                .status(MentoringRecordStatus.ALL_COMPLETED)
                .menteeFeedback("두 번째 피드백")
                .summary("두 번째 요약")
                .build();

        List<MentoringRecord> mockRecords = List.of(completedRecord1, completedRecord2);

        when(mentoringRecordRepository.findCompletedMentoringRecordsByUserId(2L))
                .thenReturn(mockRecords);

        List<MentoringRecordResponseDto> result = mentoringRecordService.findCompletedMentoringRecordsByUserId(2L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMenteeFeedback()).isEqualTo("첫 번째 피드백");
        assertThat(result.get(1).getMenteeFeedback()).isEqualTo("두 번째 피드백");
        verify(mentoringRecordRepository).findCompletedMentoringRecordsByUserId(2L);
    }

    @Test
    @DisplayName("사용자에게 완료된 멘토링 기록이 없을 때 빈 목록 반환")
    void findCompletedMentoringRecordsByUserId_EmptyList() {
        when(mentoringRecordRepository.findCompletedMentoringRecordsByUserId(2L))
                .thenReturn(List.of());

        List<MentoringRecordResponseDto> result = mentoringRecordService.findCompletedMentoringRecordsByUserId(2L);

        assertThat(result).isEmpty();
        verify(mentoringRecordRepository).findCompletedMentoringRecordsByUserId(2L);
    }

    private Position createPosition(String name) {
        Position position = Position.builder()
                .name(name)
                .category("IT")
                .description(name + " 포지션")
                .build();
        return position;
    }
}
package com.recareer.backend.mentoringRecord.controller;

import com.recareer.backend.auth.util.AuthUtil;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordListResponseDto;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordRequestDto;
import com.recareer.backend.mentoringRecord.dto.MentoringRecordResponseDto;
import com.recareer.backend.mentoringRecord.entity.MentoringRecord;
import com.recareer.backend.mentoringRecord.entity.MentoringRecordStatus;
import com.recareer.backend.mentoringRecord.service.MentoringRecordService;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.entity.ReservationStatus;
import com.recareer.backend.reservation.service.ReservationService;
import com.recareer.backend.response.ApiResponse;
import com.recareer.backend.user.entity.Role;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.common.entity.Job;
import com.recareer.backend.common.repository.JobRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * MentoringRecordController 단위 테스트
 * 
 * 멘토링 기록 컨트롤러의 비즈니스 로직을 테스트합니다.
 * - 인증 및 권한 검증 로직
 * - 응답 데이터 구조 및 HTTP 상태 코드
 * - 서비스 계층과의 통합
 */
@ExtendWith(MockitoExtension.class)
class MentoringRecordControllerTest {

    @Mock
    private MentoringRecordService mentoringRecordService;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ReservationService reservationService;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private MentoringRecordController mentoringRecordController;

    private User mentorUser;
    private User menteeUser;
    private Mentor mentor;
    private Reservation reservation;
    private MentoringRecord mentoringRecord;
    private MentoringRecordResponseDto responseDto;

    @BeforeEach
    void setUp() {
        mentorUser = User.builder()
                .id(1L)
                .name("멘토 김철수")
                .email("mentor@test.com")
                .role(Role.MENTOR)
                .build();

        menteeUser = User.builder()
                .id(2L)
                .name("멘티 이영희")
                .email("mentee@test.com")
                .role(Role.MENTEE)
                .build();

        mentor = Mentor.builder()
                .id(mentorUser.getId())
                .user(mentorUser)
                .job(createJob("시니어 백엔드 개발자"))
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
                .status(MentoringRecordStatus.ALL_COMPLETED)
                .menteeFeedback("멘토링이 매우 도움이 되었습니다!")
                .summary("커리어 전환에 대한 상담을 진행했습니다.")
                .audioFileUrl("https://s3.amazonaws.com/audio.mp3")
                .build();

        responseDto = MentoringRecordResponseDto.from(mentoringRecord);
    }

    @Nested
    @DisplayName("완료된 상담 리스트 조회")
    class GetCompletedMentoringRecordsByUserId {

        @Test
        @DisplayName("성공: 본인의 완료된 상담 기록 조회")
        void success() {
            // given
            String accessToken = "Bearer valid-token";
            MentoringRecordListResponseDto listResponseDto = MentoringRecordListResponseDto.builder()
                    .mentoringRecordId(1L)
                    .reservationTime(LocalDateTime.of(2025, 8, 10, 14, 0))
                    .status(MentoringRecordStatus.ALL_COMPLETED)
                    .mentorName("멘토 김민주")
                    .mentorPosition("시니어 백엔드 개발자")
                    .menteeName("멘티 이영희")
                    .hasAudioFile(true)
                    .hasFeedback(false)
                    .build();
            List<MentoringRecordListResponseDto> mockListRecords = Arrays.asList(listResponseDto);

            given(authUtil.validateTokenAndGetUserId(accessToken)).willReturn(2L);
            given(mentoringRecordService.findCompletedMentoringRecordsListByUserId(2L)).willReturn(mockListRecords);

            // when
            ResponseEntity<ApiResponse<List<MentoringRecordListResponseDto>>> response = 
                    mentoringRecordController.getCompletedMentoringRecords(accessToken, 2L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getData()).hasSize(1);
            assertThat(response.getBody().getData().get(0).getMentoringRecordId()).isEqualTo(1L);
            
            verify(authUtil).validateTokenAndGetUserId(accessToken);
            verify(mentoringRecordService).findCompletedMentoringRecordsListByUserId(2L);
        }

        @Test
        @DisplayName("실패: 다른 사용자의 완료된 상담 기록 조회")
        void forbidden() {
            // given
            String accessToken = "Bearer valid-token";
            
            given(authUtil.validateTokenAndGetUserId(accessToken)).willReturn(1L);

            // when
            ResponseEntity<ApiResponse<List<MentoringRecordListResponseDto>>> response = 
                    mentoringRecordController.getCompletedMentoringRecords(accessToken, 2L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getMessage()).isEqualTo("본인의 완료된 상담 기록만 조회할 수 있습니다");
            
            verify(authUtil).validateTokenAndGetUserId(accessToken);
            verify(mentoringRecordService, never()).findCompletedMentoringRecordsListByUserId(anyLong());
        }

        @Test
        @DisplayName("실패: 유효하지 않은 토큰")
        void unauthorized() {
            // given
            String accessToken = "Bearer invalid-token";
            
            given(authUtil.validateTokenAndGetUserId(accessToken))
                    .willThrow(new IllegalArgumentException("유효하지 않은 토큰입니다."));

            // when
            ResponseEntity<ApiResponse<List<MentoringRecordListResponseDto>>> response = 
                    mentoringRecordController.getCompletedMentoringRecords(accessToken, 2L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody().getMessage()).isEqualTo("유효하지 않은 토큰입니다.");
            
            verify(authUtil).validateTokenAndGetUserId(accessToken);
            verify(mentoringRecordService, never()).findCompletedMentoringRecordsByUserId(anyLong());
        }
    }

    @Nested
    @DisplayName("완료된 상담 상세 조회")
    class GetMentoringRecord {

        @Test
        @DisplayName("성공: 멘티가 자신의 상담 기록 조회")
        void successAsMentee() {
            // given
            String accessToken = "Bearer valid-token";
            
            given(authUtil.validateTokenAndGetUserId(accessToken)).willReturn(2L);
            given(mentoringRecordService.findMentoringRecordById(1L)).willReturn(mentoringRecord);

            // when
            ResponseEntity<ApiResponse<MentoringRecordResponseDto>> response = 
                    mentoringRecordController.getMentoringRecord(accessToken, 1L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getData().getMentoringRecordId()).isEqualTo(1L);
            
            verify(authUtil).validateTokenAndGetUserId(accessToken);
            verify(mentoringRecordService).findMentoringRecordById(1L);
        }

        @Test
        @DisplayName("성공: 멘토가 자신의 상담 기록 조회")
        void successAsMentor() {
            // given
            String accessToken = "Bearer valid-token";
            
            given(authUtil.validateTokenAndGetUserId(accessToken)).willReturn(1L);
            given(mentoringRecordService.findMentoringRecordById(1L)).willReturn(mentoringRecord);

            // when
            ResponseEntity<ApiResponse<MentoringRecordResponseDto>> response = 
                    mentoringRecordController.getMentoringRecord(accessToken, 1L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getData().getMentoringRecordId()).isEqualTo(1L);
            
            verify(authUtil).validateTokenAndGetUserId(accessToken);
            verify(mentoringRecordService).findMentoringRecordById(1L);
        }

        @Test
        @DisplayName("실패: 멘토링 참여자가 아닌 사용자 조회")
        void forbidden() {
            // given
            String accessToken = "Bearer valid-token";
            
            given(authUtil.validateTokenAndGetUserId(accessToken)).willReturn(3L);
            given(mentoringRecordService.findMentoringRecordById(1L)).willReturn(mentoringRecord);

            // when
            ResponseEntity<ApiResponse<MentoringRecordResponseDto>> response = 
                    mentoringRecordController.getMentoringRecord(accessToken, 1L);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getMessage()).isEqualTo("해당 멘토링 참여자만 조회할 수 있습니다");
            
            verify(authUtil).validateTokenAndGetUserId(accessToken);
            verify(mentoringRecordService).findMentoringRecordById(1L);
        }
    }

    @Nested
    @DisplayName("멘티 피드백 작성")
    class AddMenteeFeedback {

        @Test
        @DisplayName("성공: 멘티가 피드백 작성")
        void success() {
            // given
            String accessToken = "Bearer valid-token";
            MentoringRecordRequestDto requestDto = MentoringRecordRequestDto.builder()
                    .menteeFeedback("멘토링이 매우 도움이 되었습니다!")
                    .build();

            given(authUtil.validateTokenAndGetUserId(accessToken)).willReturn(2L);
            given(reservationService.findById(1L)).willReturn(reservation);
            given(mentoringRecordService.createOrUpdateMentoringRecord(1L, requestDto)).willReturn(1L);

            // when
            ResponseEntity<ApiResponse<Long>> response = 
                    mentoringRecordController.addMenteeFeedback(accessToken, 1L, requestDto);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getData()).isEqualTo(1L);
            
            verify(authUtil).validateTokenAndGetUserId(accessToken);
            verify(reservationService).findById(1L);
            verify(mentoringRecordService).createOrUpdateMentoringRecord(1L, requestDto);
        }

        @Test
        @DisplayName("실패: 멘토가 피드백 작성 시도")
        void forbiddenMentor() {
            // given
            String accessToken = "Bearer valid-token";
            MentoringRecordRequestDto requestDto = MentoringRecordRequestDto.builder()
                    .menteeFeedback("피드백")
                    .build();

            given(authUtil.validateTokenAndGetUserId(accessToken)).willReturn(1L);
            given(reservationService.findById(1L)).willReturn(reservation);

            // when
            ResponseEntity<ApiResponse<Long>> response = 
                    mentoringRecordController.addMenteeFeedback(accessToken, 1L, requestDto);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getMessage()).isEqualTo("해당 멘토링에 참여한 멘티만 피드백을 작성할 수 있습니다");
            
            verify(authUtil).validateTokenAndGetUserId(accessToken);
            verify(reservationService).findById(1L);
            verify(mentoringRecordService, never()).createOrUpdateMentoringRecord(anyLong(), any());
        }
    }

    @Nested
    @DisplayName("멘토링 녹음 파일 업로드")
    class UploadConsultationAudio {

        @Test
        @DisplayName("성공: 멘토가 녹음 파일 업로드")
        void success() {
            // given
            String accessToken = "Bearer valid-token";
            MultipartFile audioFile = mock(MultipartFile.class);

            given(authUtil.validateTokenAndGetUserId(accessToken)).willReturn(1L);
            given(reservationService.findById(1L)).willReturn(reservation);
            given(mentoringRecordService.uploadAudioAndProcess(eq(1L), any(MultipartFile.class))).willReturn(1L);

            // when
            ResponseEntity<ApiResponse<Long>> response = 
                    mentoringRecordController.uploadConsultationAudio(accessToken, 1L, audioFile);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getData()).isEqualTo(1L);
            
            verify(authUtil).validateTokenAndGetUserId(accessToken);
            verify(reservationService).findById(1L);
            verify(mentoringRecordService).uploadAudioAndProcess(eq(1L), any(MultipartFile.class));
        }

        @Test
        @DisplayName("실패: 멘티가 녹음 파일 업로드 시도")
        void forbiddenMentee() {
            // given
            String accessToken = "Bearer valid-token";
            MultipartFile audioFile = mock(MultipartFile.class);

            given(authUtil.validateTokenAndGetUserId(accessToken)).willReturn(2L);
            given(reservationService.findById(1L)).willReturn(reservation);

            // when
            ResponseEntity<ApiResponse<Long>> response = 
                    mentoringRecordController.uploadConsultationAudio(accessToken, 1L, audioFile);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getMessage()).isEqualTo("해당 멘토링에 참여한 멘토만 음성 파일을 업로드할 수 있습니다");
            
            verify(authUtil).validateTokenAndGetUserId(accessToken);
            verify(reservationService).findById(1L);
            verify(mentoringRecordService, never()).uploadAudioAndProcess(anyLong(), any(MultipartFile.class));
        }
    }

    private Job createJob(String name) {
        Job job = Job.builder()
                .name(name)
                .build();
        return job;
    }
}
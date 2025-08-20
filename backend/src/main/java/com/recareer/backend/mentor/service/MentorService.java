package com.recareer.backend.mentor.service;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.feedback.dto.MentorFeedbackListResponseDto;
import com.recareer.backend.mentor.dto.MentorCreateRequestDto;
import com.recareer.backend.mentor.dto.MentorDetailResponseDto;
import com.recareer.backend.mentor.dto.MentorSummaryResponseDto;
import com.recareer.backend.mentor.dto.MentorSearchRequestDto;
import com.recareer.backend.mentor.dto.MentorFiltersResponseDto;
import com.recareer.backend.mentor.dto.MentorSearchResponse;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.reservation.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MentorService {
    
    Mentor createMentor(MentorCreateRequestDto requestDto);
    
    Optional<Mentor> getMentorById(Long id);
    
    Optional<MentorDetailResponseDto> getMentorDetailById(Long id);

    List<MentorSummaryResponseDto> getMentorsByProvince(Long provinceId);

    
    
    List<MentorSummaryResponseDto> getMentorsByFilters(MentorFilterRequestDto filterRequest);
    
    Optional<Mentor> updateMentor(Long id, Long positionId, String description, String introduction, Integer experience, List<Long> skillIds);
    
    List<Reservation> getMentorReservations(Long mentorId);

    List<AvailableTime> getMentorAvailableTimes(Long mentorId);
    
    AvailableTime createMentorAvailableTime(Long mentorId, LocalDateTime availableTime);
    
    MentorFeedbackListResponseDto getMentorFeedbacks(Long mentorId);
    
    MentorFiltersResponseDto getFilters();

    MentorSearchResponse searchMentorsWithPrimarySecondary(MentorSearchRequestDto searchRequest);
}
package com.recareer.backend.mentor.service;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.feedback.entity.MentorFeedback;
import com.recareer.backend.mentor.dto.MentorCreateRequestDto;
import com.recareer.backend.mentor.dto.MentorDetailResponseDto;
import com.recareer.backend.mentor.dto.MentorFilterRequestDto;
import com.recareer.backend.mentor.dto.MentorSummaryResponseDto;
import com.recareer.backend.mentor.dto.FilterOptionsResponseDto;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.reservation.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MentorService {
    
    Mentor createMentor(MentorCreateRequestDto requestDto);
    
    Optional<Mentor> getMentorById(Long id);
    
    Optional<MentorDetailResponseDto> getMentorDetailById(Long id);

    List<MentorSummaryResponseDto> getMentorsByRegion(String region);

    List<Mentor> getMentorsByRegionAndPersonalityTags(List<String> regions, String providerId);

    List<Mentor> getMentorsByPriorityFilters(String providerId, List<String> regions, String position, String experience);
    
    List<MentorSummaryResponseDto> getMentorsByFilters(MentorFilterRequestDto filterRequest);
    
    Optional<Mentor> updateMentor(Long id, String position, String description, String introduction, List<String> skills);
    
    List<Reservation> getMentorReservations(Long mentorId);

    List<AvailableTime> getMentorAvailableTimes(Long mentorId);
    
    AvailableTime createMentorAvailableTime(Long mentorId, LocalDateTime availableTime);
    
    List<MentorFeedback> getMentorFeedbacks(Long mentorId);
    
    List<FilterOptionsResponseDto> getFilterOptions();
}
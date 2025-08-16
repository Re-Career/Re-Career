package com.recareer.backend.mentor.service;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.entity.MentoringType;
import com.recareer.backend.reservation.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MentorService {
    
    Optional<Mentor> getVerifiedMentorById(Long id);

    List<Mentor> getMentorsByRegionAndPersonalityTags(String region, List<Long> personalityTagIds);

    List<Mentor> getMentorsByFilters(String region, String position, String experience, MentoringType mentoringType, List<Long> personalityTags);
    
    Optional<Mentor> updateMentor(Long id, String position, String description);
    
    List<Reservation> getMentorReservations(Long mentorId);

    List<AvailableTime> getMentorAvailableTimes(Long mentorId);
    
    AvailableTime createMentorAvailableTime(Long mentorId, LocalDateTime availableTime);
}
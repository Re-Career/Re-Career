package com.recareer.backend.mentor.service;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.availableTime.repository.AvailableTimeRepository;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final ReservationRepository reservationRepository;
    private final AvailableTimeRepository availableTimeRepository;

    private static final String DEFAULT_REGION = "서울시";

    @Override
    @Transactional(readOnly = true)
    public Optional<Mentor> getVerifiedMentorById(Long id) {
        return mentorRepository.findByIdAndIsVerifiedTrueAndUserRole(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mentor> getMentorsByRegion(String region) {
        if (region == null || region.trim().isEmpty()) {
            region = DEFAULT_REGION;
        }

        log.info("Finding mentors in region: {}", region);
        return mentorRepository.findByIsVerifiedTrueAndUserRegionContains(region);
    }

    @Override
    @Transactional
    public Optional<Mentor> updateMentor(Long id, String position, String description) {
        return mentorRepository.findById(id)
                .map(mentor -> {
                    mentor.update(position, description);
                    return mentorRepository.save(mentor);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getMentorReservations(Long mentorId) {
        return mentorRepository.findById(mentorId)
                .map(reservationRepository::findByMentor)
                .orElse(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableTime> getMentorAvailableTimes(Long mentorId) {
        return mentorRepository.findById(mentorId)
                .map(availableTimeRepository::findByMentor)
                .orElse(List.of());
    }

    @Override
    @Transactional
    public AvailableTime createMentorAvailableTime(Long mentorId, LocalDateTime availableTime) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found with id: " + mentorId));
        
        AvailableTime newAvailableTime = new AvailableTime();
        newAvailableTime.setMentor(mentor);
        newAvailableTime.setAvailableTime(availableTime);
        newAvailableTime.setBooked(false);
        
        return availableTimeRepository.save(newAvailableTime);
    }
}
package com.recareer.backend.mentor.service;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.availableTime.repository.AvailableTimeRepository;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.repository.ReservationRepository;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.user.repository.UserPersonalityTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final ReservationRepository reservationRepository;
    private final AvailableTimeRepository availableTimeRepository;
    private final UserPersonalityTagRepository userPersonalityTagRepository;

    private static final String DEFAULT_REGION = "서울시";

    @Override
    @Transactional(readOnly = true)
    public Optional<Mentor> getVerifiedMentorById(Long id) {
        return mentorRepository.findByIdAndIsVerifiedTrueAndUserRole(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mentor> getMentorsByRegionAndPersonalityTags(String region, List<Long> personalityTagIds) {
        if (region == null || region.trim().isEmpty()) {
            region = DEFAULT_REGION;
        }

        if (personalityTagIds == null || personalityTagIds.isEmpty()) {
            // personalityTagIds가 없으면 기존 방식으로 조회
            log.info("Finding mentors in region: {} (no personality filtering)", region);
            return mentorRepository.findByIsVerifiedTrueAndUserRegionContains(region);
        }

        log.info("Finding mentors in region: {} with personality tags: {}", region, personalityTagIds);
        
        // 지역별 모든 멘토 조회
        List<Mentor> allMentors = mentorRepository.findByIsVerifiedTrueAndUserRegionContains(region);
        
        // 성향 매칭을 기반으로 정렬
        return allMentors.stream()
                .sorted((mentor1, mentor2) -> {
                    // 각 멘토의 성향 태그와 요청된 성향 태그 매칭 수 계산
                    long matches1 = getPersonalityMatchesByTags(mentor1.getUser().getId(), personalityTagIds);
                    long matches2 = getPersonalityMatchesByTags(mentor2.getUser().getId(), personalityTagIds);
                    
                    // 매칭 수 내림차순, 그 다음 생성일 오름차순
                    int matchComparison = Long.compare(matches2, matches1);
                    if (matchComparison != 0) {
                        return matchComparison;
                    }
                    return mentor1.getCreatedDate().compareTo(mentor2.getCreatedDate());
                })
                .toList();
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

    private long getPersonalityMatches(Long mentorUserId, Set<Long> userTagIds) {
        List<UserPersonalityTag> mentorPersonalityTags = userPersonalityTagRepository.findByUserId(mentorUserId);
        return mentorPersonalityTags.stream()
                .mapToLong(mpt -> userTagIds.contains(mpt.getPersonalityTag().getId()) ? 1 : 0)
                .sum();
    }

    private long getPersonalityMatchesByTags(Long mentorUserId, List<Long> personalityTagIds) {
        List<UserPersonalityTag> mentorPersonalityTags = userPersonalityTagRepository.findByUserId(mentorUserId);
        return mentorPersonalityTags.stream()
                .mapToLong(mpt -> personalityTagIds.contains(mpt.getPersonalityTag().getId()) ? 1 : 0)
                .sum();
    }
}
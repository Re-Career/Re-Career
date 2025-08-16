package com.recareer.backend.mentor.service;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.availableTime.repository.AvailableTimeRepository;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.entity.MentoringType;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        return mentorRepository.findById(id);
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
        
        // 1. 지역별 멘토 조회 (User 정보까지 Fetch Join)
        List<Mentor> allMentors = mentorRepository.findByIsVerifiedTrueAndUserRegionContainsWithUser(region);
        
        // 2. 모든 멘토의 UserId 추출
        List<Long> userIds = allMentors.stream()
                .map(mentor -> mentor.getUser().getId())
                .toList();
        
        // 3. 배치로 모든 UserPersonalityTag를 한 번에 조회하여 Map으로 그룹화 (N+1 방지)
        Map<Long, List<UserPersonalityTag>> userPersonalityTagMap = userPersonalityTagRepository
                .findByUserIdInWithPersonalityTag(userIds)
                .stream()
                .collect(Collectors.groupingBy(upt -> upt.getUser().getId()));
        
        // 4. 성향 매칭을 기반으로 정렬 (추가 쿼리 없이 메모리에서 계산)
        return allMentors.stream()
                .sorted((mentor1, mentor2) -> {
                    // 각 멘토의 성향 태그와 요청된 성향 태그 매칭 수 계산 (메모리에서)
                    long matches1 = calculatePersonalityMatchesFromMap(mentor1.getUser().getId(), userPersonalityTagMap, personalityTagIds);
                    long matches2 = calculatePersonalityMatchesFromMap(mentor2.getUser().getId(), userPersonalityTagMap, personalityTagIds);
                    
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
    @Transactional(readOnly = true)
    public List<Mentor> getMentorsByFilters(String region, String position, String experience, MentoringType mentoringType, List<Long> personalityTags) {
        if (region == null || region.trim().isEmpty()) {
            region = DEFAULT_REGION;
        }

        log.info("Finding mentors with filters - region: {}, position: {}, experience: {}, mentoringType: {}, personalityTags: {}", 
                region, position, experience, mentoringType, personalityTags);

        List<Mentor> mentors;
        
        if (personalityTags != null && !personalityTags.isEmpty()) {
            // personalityTags가 있으면 기존 매칭 로직 사용
            mentors = getMentorsByRegionAndPersonalityTags(region, personalityTags);
        } else {
            // personalityTags가 없으면 기본 지역 조회
            mentors = mentorRepository.findByIsVerifiedTrueAndUserRegionContainsWithUser(region);
        }

        return mentors.stream()
                .filter(mentor -> {
                    if (position != null && !position.trim().isEmpty()) {
                        return mentor.getPosition() != null && 
                               mentor.getPosition().toLowerCase().contains(position.toLowerCase());
                    }
                    return true;
                })
                .filter(mentor -> {
                    if (experience != null && !experience.trim().isEmpty()) {
                        return matchesExperienceRange(mentor.getExperience(), experience);
                    }
                    return true;
                })
                .filter(mentor -> {
                    if (mentoringType != null) {
                        return mentor.getMentoringType() != null && 
                               (mentor.getMentoringType().equals(mentoringType) || 
                                mentor.getMentoringType().equals(MentoringType.BOTH));
                    }
                    return true;
                })
                .sorted((mentor1, mentor2) -> {
                    if (personalityTags != null && !personalityTags.isEmpty()) {
                        // personalityTags가 있으면 매칭 수에 따라 정렬 (이미 정렬된 상태)
                        return 0;
                    }
                    return mentor1.getCreatedDate().compareTo(mentor2.getCreatedDate());
                })
                .toList();
    }

    private boolean matchesExperienceRange(Integer mentorExperience, String experienceRange) {
        if (mentorExperience == null) {
            return false;
        }
        
        return switch (experienceRange) {
            case "1-3년" -> mentorExperience >= 1 && mentorExperience <= 3;
            case "4-6년" -> mentorExperience >= 4 && mentorExperience <= 6;
            case "7년 이상" -> mentorExperience >= 7;
            default -> true;
        };
    }

    @Override
    @Transactional
    public Optional<Mentor> updateMentor(Long id, String position, String description) {
        return mentorRepository.findById(id)
                .map(mentor -> {
                    mentor.update(position, description, mentor.getExperience(), mentor.getMentoringType());
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

    /**
     * 배치로 조회한 Map을 이용해 메모리에서 성향 매칭 수 계산
     * N+1 문제 해결을 위해 추가 쿼리 없이 매칭 수 계산
     */
    private long calculatePersonalityMatchesFromMap(Long userId, Map<Long, List<UserPersonalityTag>> userPersonalityTagMap, List<Long> personalityTagIds) {
        List<UserPersonalityTag> userPersonalityTags = userPersonalityTagMap.getOrDefault(userId, List.of());
        return userPersonalityTags.stream()
                .mapToLong(upt -> personalityTagIds.contains(upt.getPersonalityTag().getId()) ? 1 : 0)
                .sum();
    }

    /**
     * @deprecated N+1 문제가 있는 기존 메서드. calculatePersonalityMatches 사용 권장
     */
    @Deprecated
    private long getPersonalityMatchesByTags(Long mentorUserId, List<Long> personalityTagIds) {
        List<UserPersonalityTag> mentorPersonalityTags = userPersonalityTagRepository.findByUserId(mentorUserId);
        return mentorPersonalityTags.stream()
                .mapToLong(mpt -> personalityTagIds.contains(mpt.getPersonalityTag().getId()) ? 1 : 0)
                .sum();
    }
}
package com.recareer.backend.mentor.service;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.availableTime.repository.AvailableTimeRepository;
import com.recareer.backend.career.entity.MentorCareer;
import com.recareer.backend.career.repository.MentorCareerRepository;
import com.recareer.backend.common.entity.Company;
import com.recareer.backend.common.entity.Job;
import com.recareer.backend.common.entity.Region;
import com.recareer.backend.common.repository.CompanyRepository;
import com.recareer.backend.common.repository.JobRepository;
import com.recareer.backend.common.repository.RegionRepository;
import com.recareer.backend.feedback.entity.MentorFeedback;
import com.recareer.backend.feedback.repository.MentorFeedbackRepository;
import com.recareer.backend.mentor.dto.MentorCreateRequestDto;
import com.recareer.backend.mentor.dto.MentorDetailResponseDto;
import com.recareer.backend.mentor.dto.MentorFilterRequestDto;
import com.recareer.backend.mentor.dto.MentorSummaryResponseDto;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.entity.MentoringType;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.reservation.entity.Reservation;
import com.recareer.backend.reservation.repository.ReservationRepository;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.user.repository.UserPersonalityTagRepository;
import com.recareer.backend.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final MentorCareerRepository mentorCareerRepository;
    private final MentorFeedbackRepository mentorFeedbackRepository;
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final RegionRepository regionRepository;

    private static final String DEFAULT_REGION = "서울시";

    @Override
    @Transactional
    public Mentor createMentor(MentorCreateRequestDto requestDto) {
        // 사용자 조회
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + requestDto.getUserId()));
        
        // Job, Company, Region 조회
        Job job = requestDto.getJobId() != null ? 
            jobRepository.findById(requestDto.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found with id: " + requestDto.getJobId())) : null;
                
        Company company = requestDto.getCompanyId() != null ? 
            companyRepository.findById(requestDto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + requestDto.getCompanyId())) : null;
                
        Region region = requestDto.getRegionId() != null ? 
            regionRepository.findById(requestDto.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("Region not found with id: " + requestDto.getRegionId())) : null;
        
        // 멘토 생성
        Mentor mentor = Mentor.builder()
                .id(user.getId()) // User와 동일한 ID 사용
                .user(user)
                .job(job)
                .company(company)
                // region 필드 제거됨 - User 엔티티에서 Province/City로 관리
                .description(requestDto.getDescription())
                .introduction(requestDto.getIntroduction())
                .experience(requestDto.getExperience())
                .mentoringType(requestDto.getMentoringType())
                .skills(requestDto.getSkills() != null ? requestDto.getSkills() : List.of())
                .isVerified(true) // TODO: 건강보험 등록증 확인 로직 추가 시 false로 변경
                .build();
        
        return mentorRepository.save(mentor);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Mentor> getVerifiedMentorById(Long id) {
        return mentorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorSummaryResponseDto> getMentorsByRegion(String region) {
        if (region == null || region.trim().isEmpty()) {
            region = DEFAULT_REGION;
        }

        log.info("Finding mentors by region: {}", region);

        // 1. 지역별 멘토 조회 (User 정보까지 Fetch Join)
        List<Mentor> mentors = mentorRepository.findByIsVerifiedTrueAndUserRegionContainsWithUser(region);

        // 2. 각 멘토에 대한 추가 정보 조회
        return mentors.stream()
                .map(mentor -> {
                    // 경력 정보 조회
                    List<MentorCareer> careers = mentorCareerRepository.findByMentorOrderByDisplayOrderAscStartDateDesc(mentor);
                    
                    // 성향 태그 조회
                    List<UserPersonalityTag> userPersonalityTags = userPersonalityTagRepository.findByUserId(mentor.getUser().getId());
                    
                    return MentorSummaryResponseDto.from(mentor, userPersonalityTags, careers);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MentorDetailResponseDto> getMentorDetailById(Long id) {
        return mentorRepository.findById(id)
                .map(mentor -> {
                    // 연관관계를 통해 경력 정보 조회 (정렬 필요시 별도 쿼리 사용)
                    List<MentorCareer> careers = mentorCareerRepository.findByMentorOrderByDisplayOrderAscStartDateDesc(mentor);
                    
                    // 연관관계를 통해 피드백 정보 조회 (visible한 것만 필터링)
                    List<MentorFeedback> feedbacks = mentorFeedbackRepository.findByMentorAndIsVisibleTrueWithUser(mentor);
                    Double averageRating = mentorFeedbackRepository.getAverageRatingByMentor(mentor);
                    Integer feedbackCount = mentorFeedbackRepository.countByMentorAndIsVisibleTrue(mentor);
                    
                    // 멘토의 성향 태그 조회
                    List<UserPersonalityTag> userPersonalityTags = userPersonalityTagRepository.findByUserId(mentor.getUser().getId());
                    
                    return MentorDetailResponseDto.from(mentor, careers, feedbacks, averageRating, feedbackCount, userPersonalityTags);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mentor> getMentorsByRegionAndPersonalityTags(List<String> regions, String providerId) {
        if (regions == null || regions.isEmpty()) {
            regions = List.of(DEFAULT_REGION);
        }

        log.info("Finding mentors by user personality tags - providerId: {}, regions: {}", providerId, regions);

        // 1. providerId로 User 조회
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with providerId: " + providerId));

        // 2. 해당 유저의 성향 태그 조회
        List<UserPersonalityTag> userPersonalityTags = userPersonalityTagRepository.findByUserId(user.getId());
        List<Long> personalityTagIds = userPersonalityTags.stream()
                .map(upt -> upt.getPersonalityTag().getId())
                .toList();

        log.info("User personality tag IDs: {}", personalityTagIds);

        if (personalityTagIds.isEmpty()) {
            // personalityTags가 없으면 기존 방식으로 조회
            log.info("Finding mentors in regions: {} (no personality filtering)", regions);
            return getMentorsByRegions(regions);
        }

        log.info("Finding mentors in regions: {} with personality tags: {}", regions, personalityTagIds);
        
        // 3. 지역별 멘토 조회 (User 정보까지 Fetch Join)
        List<Mentor> allMentors = getMentorsByRegionsWithUser(regions);
        
        // 4. 모든 멘토의 UserId 추출
        List<Long> userIds = allMentors.stream()
                .map(mentor -> mentor.getUser().getId())
                .distinct()
                .toList();
        
        // 5. 배치로 모든 UserPersonalityTag를 한 번에 조회하여 Map으로 그룹화 (N+1 방지)
        Map<Long, List<UserPersonalityTag>> userPersonalityTagMap = userPersonalityTagRepository
                .findByUserIdInWithPersonalityTag(userIds)
                .stream()
                .collect(Collectors.groupingBy(upt -> upt.getUser().getId()));
        
        // 6. 성향 매칭을 기반으로 정렬 (추가 쿼리 없이 메모리에서 계산)
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
    public List<Mentor> getMentorsByPriorityFilters(String providerId, List<String> regions, String position, String experience, MentoringType mentoringType) {
        log.info("Finding mentors by priority filters - providerId: {}, regions: {}, position: {}, experience: {}, mentoringType: {}", 
                providerId, regions, position, experience, mentoringType);

        // 1순위: 사용자 기반 지역/성향 매칭
        List<Mentor> primaryMentors = getMentorsByRegionAndPersonalityTags(regions, providerId);
        
        // 2순위 필터링이 없으면 1순위 결과만 반환
        if ((position == null || position.trim().isEmpty()) && 
            (experience == null || experience.trim().isEmpty()) && 
            mentoringType == null) {
            return primaryMentors;
        }
        
        // 2순위 필터링 적용
        return primaryMentors.stream()
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
                .toList(); // 1순위에서 이미 정렬되어 있으므로 추가 정렬 불필요
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
    public Optional<Mentor> updateMentor(Long id, String position, String description, String introduction, List<String> skills) {
        return mentorRepository.findById(id)
                .map(mentor -> {
                    mentor.update(mentor.getJob(), mentor.getCompany(), description, introduction, mentor.getExperience(), mentor.getMentoringType());
                    if (skills != null) {
                        mentor.updateSkills(skills);
                    }
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

    @Override
    @Transactional(readOnly = true)
    public List<MentorFeedback> getMentorFeedbacks(Long mentorId) {
        return mentorFeedbackRepository.findByMentorIdAndIsVisibleTrue(mentorId);
    }

    private long getPersonalityMatches(Long mentorUserId, Set<Long> userTagIds) {
        List<UserPersonalityTag> mentorPersonalityTags = userPersonalityTagRepository.findByUserId(mentorUserId);
        return mentorPersonalityTags.stream()
                .mapToLong(mpt -> userTagIds.contains(mpt.getPersonalityTag().getId()) ? 1 : 0)
                .sum();
    }

    /**
     * 직접 personalityTagIds를 받아서 멘토 조회 (기존 로직)
     */
    private List<Mentor> getMentorsByRegionAndPersonalityTagIds(List<String> regions, List<Long> personalityTagIds) {
        if (regions == null || regions.isEmpty()) {
            regions = List.of(DEFAULT_REGION);
        }

        log.info("Finding mentors in regions: {} with personality tags: {}", regions, personalityTagIds);
        
        // 1. 지역별 멘토 조회 (User 정보까지 Fetch Join)
        List<Mentor> allMentors = getMentorsByRegionsWithUser(regions);
        
        // 2. 모든 멘토의 UserId 추출
        List<Long> userIds = allMentors.stream()
                .map(mentor -> mentor.getUser().getId())
                .distinct()
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
     * 여러 지역에 대한 멘토 조회
     */
    private List<Mentor> getMentorsByRegions(List<String> regions) {
        return regions.stream()
                .flatMap(region -> mentorRepository.findByIsVerifiedTrueAndUserRegionContains(region).stream())
                .distinct()
                .toList();
    }

    /**
     * 여러 지역에 대한 멘토 조회 (User 정보 포함)
     */
    private List<Mentor> getMentorsByRegionsWithUser(List<String> regions) {
        return regions.stream()
                .flatMap(region -> mentorRepository.findByIsVerifiedTrueAndUserRegionContainsWithUser(region).stream())
                .distinct()
                .toList();
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

    @Override
    @Transactional(readOnly = true)
    public List<MentorSummaryResponseDto> getMentorsByFilters(MentorFilterRequestDto filterRequest) {
        log.info("Finding mentors by filters - jobs: {}, experiences: {}, mentoringTypes: {}, provinceId: {}, cityId: {}", 
                filterRequest.getJobs(), filterRequest.getExperiences(), filterRequest.getMentoringTypes(),
                filterRequest.getProvinceId(), filterRequest.getCityId());

        // 1. 전체 검증된 멘토 조회 (User 정보까지 Fetch Join)
        List<Mentor> allMentors = mentorRepository.findAllByIsVerifiedTrueWithUser();
        
        // 2. 필터링 적용
        List<Mentor> filteredMentors = allMentors.stream()
                .filter(mentor -> {
                    // Job 필터링
                    if (filterRequest.getJobs() != null && !filterRequest.getJobs().isEmpty()) {
                        boolean jobMatches = filterRequest.getJobs().stream()
                                .anyMatch(job -> mentor.getJob() != null && 
                                        mentor.getJob().getName().toLowerCase().contains(job.toLowerCase()));
                        if (!jobMatches) return false;
                    }
                    
                    // Experience 필터링
                    if (filterRequest.getExperiences() != null && !filterRequest.getExperiences().isEmpty()) {
                        boolean experienceMatches = filterRequest.getExperiences().stream()
                                .anyMatch(exp -> matchesExperienceRange(mentor.getExperience(), exp));
                        if (!experienceMatches) return false;
                    }
                    
                    // MentoringType 필터링
                    if (filterRequest.getMentoringTypes() != null && !filterRequest.getMentoringTypes().isEmpty()) {
                        boolean mentoringTypeMatches = filterRequest.getMentoringTypes().stream()
                                .anyMatch(type -> matchesMentoringType(mentor.getMentoringType(), type));
                        if (!mentoringTypeMatches) return false;
                    }
                    
                    // Province 필터링
                    if (filterRequest.getProvinceId() != null) {
                        boolean provinceMatches = mentor.getUser() != null && 
                                mentor.getUser().getProvince() != null && 
                                mentor.getUser().getProvince().getId().equals(filterRequest.getProvinceId());
                        if (!provinceMatches) return false;
                    }
                    
                    // City 필터링
                    if (filterRequest.getCityId() != null) {
                        boolean cityMatches = mentor.getUser() != null && 
                                mentor.getUser().getCity() != null && 
                                mentor.getUser().getCity().getId().equals(filterRequest.getCityId());
                        if (!cityMatches) return false;
                    }
                    
                    return true;
                })
                .toList();

        // 3. MentorSummaryResponseDto로 변환
        return filteredMentors.stream()
                .map(mentor -> {
                    // 경력 정보 조회
                    List<MentorCareer> careers = mentorCareerRepository.findByMentorOrderByDisplayOrderAscStartDateDesc(mentor);
                    
                    // 성향 태그 조회
                    List<UserPersonalityTag> userPersonalityTags = userPersonalityTagRepository.findByUserId(mentor.getUser().getId());
                    
                    return MentorSummaryResponseDto.from(mentor, userPersonalityTags, careers);
                })
                .toList();
    }

    /**
     * 멘토링 타입 매칭 확인
     */
    private boolean matchesMentoringType(MentoringType mentorType, MentoringType requestedType) {
        if (mentorType == null || requestedType == null) {
            return false;
        }
        
        // BOTH는 모든 요청에 매칭됨
        if (mentorType == MentoringType.BOTH) {
            return true;
        }
        
        // 정확히 같은 타입
        return mentorType == requestedType;
    }
}
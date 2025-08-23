package com.recareer.backend.mentor.service;

import com.recareer.backend.availableTime.entity.AvailableTime;
import com.recareer.backend.availableTime.repository.AvailableTimeRepository;
import com.recareer.backend.career.entity.MentorCareer;
import com.recareer.backend.career.repository.MentorCareerRepository;
import com.recareer.backend.common.entity.Company;
import com.recareer.backend.position.entity.Position;
import com.recareer.backend.common.repository.CompanyRepository;
import com.recareer.backend.position.repository.PositionRepository;
import com.recareer.backend.common.repository.ProvinceRepository;
import com.recareer.backend.common.entity.Province;
import com.recareer.backend.skill.entity.MentorSkill;
import com.recareer.backend.skill.entity.Skill;
import com.recareer.backend.skill.repository.MentorSkillRepository;
import com.recareer.backend.skill.repository.SkillRepository;
import com.recareer.backend.feedback.dto.MentorFeedbackListResponseDto;
import com.recareer.backend.feedback.dto.MentorFeedbackResponseDto;
import com.recareer.backend.feedback.entity.MentorFeedback;
import com.recareer.backend.feedback.repository.MentorFeedbackRepository;
import com.recareer.backend.mentor.dto.*;
import com.recareer.backend.personality.entity.PersonalityTag;
import com.recareer.backend.personality.repository.PersonalityTagRepository;
import com.recareer.backend.mentor.entity.Mentor;
import com.recareer.backend.mentor.repository.MentorRepository;
import com.recareer.backend.session.entity.Session;
import com.recareer.backend.session.repository.SessionRepository;
import com.recareer.backend.user.entity.User;
import com.recareer.backend.user.entity.UserPersonalityTag;
import com.recareer.backend.user.repository.UserPersonalityTagRepository;
import com.recareer.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final SessionRepository sessionRepository;
    private final AvailableTimeRepository availableTimeRepository;
    private final UserPersonalityTagRepository userPersonalityTagRepository;
    private final UserRepository userRepository;
    private final MentorCareerRepository mentorCareerRepository;
    private final MentorFeedbackRepository mentorFeedbackRepository;
    private final PositionRepository positionRepository;
    private final CompanyRepository companyRepository;
    private final ProvinceRepository provinceRepository;
    private final SkillRepository skillRepository;
    private final MentorSkillRepository mentorSkillRepository;
    private final PersonalityTagRepository personalityTagRepository;

    private static final Long DEFAULT_PROVINCE_ID = 1L; // 서울특별시

    @Override
    @Transactional
    public Mentor createMentor(MentorCreateRequestDto requestDto) {
        // 사용자 조회
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + requestDto.getUserId()));
        
        // Position, Company, Region 조회
        Position position = requestDto.getPositionId() != null ? 
            positionRepository.findById(requestDto.getPositionId())
                .orElseThrow(() -> new IllegalArgumentException("직무를 찾을 수 없습니다. ID: " + requestDto.getPositionId())) : null;
                
        Company company = requestDto.getCompanyId() != null ? 
            companyRepository.findById(requestDto.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다. ID: " + requestDto.getCompanyId())) : null;
        
        // 멘토 생성
        Mentor mentor = Mentor.builder()
                .id(user.getId()) // User와 동일한 ID 사용
                .user(user)
                .position(position)
                .company(company)
                // region 필드 제거됨 - User 엔티티에서 Province/City로 관리
                .description(requestDto.getDescription())
                .introduction(requestDto.getIntroduction())
                .experience(requestDto.getExperience())
                .isVerified(true) // TODO: 건강보험 등록증 확인 로직 추가 시 false로 변경
                .build();
        
        // 멘토 저장
        Mentor savedMentor = mentorRepository.save(mentor);
        
        // MentorSkill 관계 생성
        if (requestDto.getSkillIds() != null && !requestDto.getSkillIds().isEmpty()) {
            List<Skill> skills = skillRepository.findByIdIn(requestDto.getSkillIds());
            List<MentorSkill> mentorSkills = skills.stream()
                    .map(skill -> MentorSkill.builder()
                            .mentor(savedMentor)
                            .skill(skill)
                            .build())
                    .toList();
            mentorSkillRepository.saveAll(mentorSkills);
        }
        
        return savedMentor;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Mentor> getMentorById(Long id) {
        return mentorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorSummaryResponseDto> getMentorsByProvince(Long provinceId) {
        if (provinceId == null) {
            provinceId = DEFAULT_PROVINCE_ID;
        }

        log.info("Finding mentors by province: {}", provinceId);

        // 1. 지역별 멘토 조회 (User 정보까지 Fetch Join)
        List<Mentor> mentors = mentorRepository.findByUserProvinceIdWithUser(provinceId);

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
                    // 연관 데이터 조회 (각각 별도 쿼리로 성능 최적화)
                    List<MentorCareer> careers = mentorCareerRepository.findByMentorOrderByDisplayOrderAscStartDateDesc(mentor);
                    List<MentorFeedback> feedbacks = mentorFeedbackRepository.findByMentorAndIsVisibleTrueWithUser(mentor);
                    Double averageRating = mentorFeedbackRepository.getAverageRatingByMentor(mentor);
                    Integer feedbackCount = mentorFeedbackRepository.countByMentorAndIsVisibleTrue(mentor);
                    List<UserPersonalityTag> userPersonalityTags = userPersonalityTagRepository.findByUserId(mentor.getUser().getId());
                    List<MentorSkill> mentorSkills = mentorSkillRepository.findByMentorWithSkill(mentor);
                    
                    return MentorDetailResponseDto.from(mentor, careers, feedbacks, averageRating, feedbackCount, userPersonalityTags, mentorSkills);
                });
    }

    @Override
    @Transactional
    public Optional<Mentor> updateMentor(Long id, Long positionId, String description, String introduction, Integer experience, List<Long> skillIds) {
        return mentorRepository.findById(id)
                .map(mentor -> {
                    Position position = null;
                    if (positionId != null) {
                        position = positionRepository.findById(positionId)
                                .orElseThrow(() -> new IllegalArgumentException("직무를 찾을 수 없습니다. ID: " + positionId));
                    }

                    mentor.update(position, mentor.getCompany(), description, introduction, experience);

                    // 기존 MentorSkill 관계 제거 후 새로 생성
                    mentorSkillRepository.deleteByMentor(mentor);
                    if (skillIds != null && !skillIds.isEmpty()) {
                        List<Skill> skills = skillRepository.findByIdIn(skillIds);
                        List<MentorSkill> mentorSkills = skills.stream()
                                .map(skill -> MentorSkill.builder()
                                        .mentor(mentor)
                                        .skill(skill)
                                        .build())
                                .toList();
                        mentorSkillRepository.saveAll(mentorSkills);
                    }
                    
                    return mentorRepository.save(mentor);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Session> getMentorSessions(Long mentorId) {
        return mentorRepository.findById(mentorId)
                .map(sessionRepository::findByMentor)
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
                .orElseThrow(() -> new IllegalArgumentException("멘토를 찾을 수 없습니다. ID: " + mentorId));
        
        AvailableTime newAvailableTime = new AvailableTime();
        newAvailableTime.setMentor(mentor);
        newAvailableTime.setAvailableTime(availableTime);
        newAvailableTime.setBooked(false);
        
        return availableTimeRepository.save(newAvailableTime);
    }

    @Override
    @Transactional(readOnly = true)
    public MentorFeedbackListResponseDto getMentorFeedbacks(Long mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("멘토를 찾을 수 없습니다. ID: " + mentorId));
        List<MentorFeedback> feedbacks = mentorFeedbackRepository.findByMentorAndIsVisibleTrueWithUser(mentor);
        Integer feedbackCount = mentorFeedbackRepository.countByMentorAndIsVisibleTrue(mentor);

        List<MentorFeedbackResponseDto> feedbackDtos = feedbacks.stream()
                .map(MentorFeedbackResponseDto::from)
                .collect(Collectors.toList());

        return MentorFeedbackListResponseDto.builder()
                .totalFeedbacks(feedbackCount)
                .feedbacks(feedbackDtos)
                .build();
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

    

    @Override
    @Transactional(readOnly = true)
    public List<MentorSummaryResponseDto> getMentorsByFilters(MentorFilterRequestDto filterRequest) {
        // 기본값 설정: provinceId가 null이면 서울특별시(1L)로 설정
        final MentorFilterRequestDto finalFilterRequest;
        if (filterRequest.getProvinceId() == null) {
            finalFilterRequest = MentorFilterRequestDto.builder()
                    .positions(filterRequest.getPositions())
                    .experiences(filterRequest.getExperiences())
                    .provinceId(DEFAULT_PROVINCE_ID)
                    .cityId(filterRequest.getCityId())
                    .build();
        } else {
            finalFilterRequest = filterRequest;
        }
        
        log.info("Finding mentors by filters - positions: {}, experiences: {}, provinceId: {}, cityId: {}", 
                finalFilterRequest.getPositions(), finalFilterRequest.getExperiences(),
                finalFilterRequest.getProvinceId(), finalFilterRequest.getCityId());

        // 1. 전체 검증된 멘토 조회 (User 정보까지 Fetch Join)
        List<Mentor> allMentors = mentorRepository.findAllWithUser();
        
        // 2. 필터링 적용
        List<Mentor> filteredMentors = allMentors.stream()
                .filter(mentor -> {
                    // Position 필터링 (positions로 받아서 position으로 처리)
                    if (finalFilterRequest.getPositions() != null && !finalFilterRequest.getPositions().isEmpty()) {
                        boolean positionMatches = finalFilterRequest.getPositions().stream()
                                .anyMatch(position -> mentor.getPositionEntity() != null && 
                                        mentor.getPositionEntity().getName().toLowerCase().contains(position.toLowerCase()));
                        if (!positionMatches) return false;
                    }
                    
                    // Experience 필터링
                    if (finalFilterRequest.getExperiences() != null && !finalFilterRequest.getExperiences().isEmpty()) {
                        boolean experienceMatches = finalFilterRequest.getExperiences().stream()
                                .anyMatch(exp -> matchesExperienceRange(mentor.getExperience(), exp));
                        if (!experienceMatches) return false;
                    }
                    
                    
                    // Province 필터링
                    if (finalFilterRequest.getProvinceId() != null) {
                        boolean provinceMatches = mentor.getUser() != null && 
                                mentor.getUser().getProvince() != null && 
                                mentor.getUser().getProvince().getId().equals(finalFilterRequest.getProvinceId());
                        if (!provinceMatches) return false;
                    }
                    
                    // City 필터링
                    if (finalFilterRequest.getCityId() != null) {
                        boolean cityMatches = mentor.getUser() != null && 
                                mentor.getUser().getCity() != null && 
                                mentor.getUser().getCity().getId().equals(finalFilterRequest.getCityId());
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


    @Override
    public List<FilterOptionsResponseDto> getFilters() {
        List<FilterOptionsResponseDto> filterOptions = new ArrayList<>();
        
        // Positions 필터
        List<Position> positions = positionRepository.findAll();
        List<FilterOptionDto> positionOptions = positions.stream()
                .map(position -> FilterOptionDto.builder()
                        .key(position.getId().toString())
                        .name(position.getName())
                        .build())
                .collect(Collectors.toList());
        filterOptions.add(FilterOptionsResponseDto.builder()
                .key("position")
                .title("직업")
                .options(positionOptions)
                .build());

        // Experience 필터 (하드코딩)
        List<FilterOptionDto> experienceOptions = Arrays.asList(
                FilterOptionDto.builder().key("1-3").name("1-3년").build(),
                FilterOptionDto.builder().key("4-6").name("4-6년").build(),
                FilterOptionDto.builder().key("7+").name("7년 이상").build()
        );
        filterOptions.add(FilterOptionsResponseDto.builder()
                .key("experience")
                .title("경험")
                .options(experienceOptions)
                .build());

        // Provinces 필터
        List<Province> provinces = provinceRepository.findAll();
        List<FilterOptionDto> provinceOptions = provinces.stream()
                .map(province -> FilterOptionDto.builder()
                        .key(province.getId().toString())
                        .name(province.getName())
                        .build())
                .collect(Collectors.toList());
        filterOptions.add(FilterOptionsResponseDto.builder()
                .key("region")
                .title("지역")
                .options(provinceOptions)
                .build());

        // PersonalityTags 필터
        List<PersonalityTag> personalityTags = personalityTagRepository.findAll();
        List<FilterOptionDto> personalityTagOptions = personalityTags.stream()
                .map(tag -> FilterOptionDto.builder()
                        .key(tag.getId().toString())
                        .name(tag.getName())
                        .build())
                .collect(Collectors.toList());
        filterOptions.add(FilterOptionsResponseDto.builder()
                .key("PersonalityTags")
                .title("성향")
                .options(personalityTagOptions)
                .build());

        return filterOptions;
    }

    @Override
    @Transactional(readOnly = true)
    public MentorSearchResponse searchMentorsWithPrimarySecondary(MentorSearchRequestDto searchRequest) {
        log.info("Searching mentors with primary/secondary filters - keyword: {}, positionIds: {}, experiences: {}, provinceIds: {}, personalityTagIds: {}",
                searchRequest.keyword(), searchRequest.positionIds(), searchRequest.experiences(), 
                searchRequest.provinceIds(), searchRequest.personalityTagIds());

        // 모든 검증된 멘토 조회 (N+1 방지를 위한 fetch join)
        List<Mentor> allMentors = mentorRepository.findAllWithUser();
        
        // Secondary 필터링: 직업/경험 기준만 적용
        List<Mentor> secondaryFilteredMentors = allMentors.stream()
                .filter(mentor -> {
                    // Keyword 필터링
                    if (searchRequest.keyword() != null && !searchRequest.keyword().isBlank()) {
                        String keyword = searchRequest.keyword().toLowerCase();
                        boolean keywordMatch = 
                            (mentor.getUser().getName() != null && mentor.getUser().getName().toLowerCase().contains(keyword)) ||
                            (mentor.getDescription() != null && mentor.getDescription().toLowerCase().contains(keyword)) ||
                            (mentor.getIntroduction() != null && mentor.getIntroduction().toLowerCase().contains(keyword));
                        if (!keywordMatch) return false;
                    }
                    
                    // Position 필터링
                    if (searchRequest.positionIds() != null && !searchRequest.positionIds().isEmpty()) {
                        boolean positionMatches = mentor.getPositionEntity() != null && 
                                searchRequest.positionIds().contains(mentor.getPositionEntity().getId());
                        if (!positionMatches) return false;
                    }
                    
                    // Experience 문자열 필터링
                    if (searchRequest.experiences() != null && !searchRequest.experiences().isEmpty()) {
                        if (!matchesExperienceRanges(mentor.getExperience(), searchRequest.experiences())) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());

        // Primary 필터링: 1순위(지역/성향), 2순위(직업/경험) 적용
        List<Mentor> primaryFilteredMentors = secondaryFilteredMentors.stream()
                .filter(mentor -> {
                    // Province 필터링 (1순위)
                    if (searchRequest.provinceIds() != null && !searchRequest.provinceIds().isEmpty()) {
                        boolean provinceMatches = mentor.getUser() != null && 
                                mentor.getUser().getProvince() != null && 
                                searchRequest.provinceIds().contains(mentor.getUser().getProvince().getId());
                        if (!provinceMatches) return false;
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());

        // PersonalityTag AND 필터링 (Primary 대상으로만 적용)
        List<Mentor> primaryPersonalityFiltered = primaryFilteredMentors;
        if (searchRequest.personalityTagIds() != null && !searchRequest.personalityTagIds().isEmpty()) {
            List<Long> primaryMentorIds = primaryFilteredMentors.stream()
                    .map(Mentor::getId)
                    .collect(Collectors.toList());

            // 배치로 UserPersonalityTag 조회 (N+1 방지)
            Map<Long, List<UserPersonalityTag>> personalityTagMap = userPersonalityTagRepository
                    .findByUserIdInWithPersonalityTag(primaryMentorIds)
                    .stream()
                    .collect(Collectors.groupingBy(upt -> upt.getUser().getId()));

            // 모든 personalityTagIds를 가진 멘토만 필터링 (AND 조건)
            primaryPersonalityFiltered = primaryFilteredMentors.stream()
                    .filter(mentor -> {
                        List<UserPersonalityTag> userPersonalityTags = personalityTagMap.getOrDefault(mentor.getId(), List.of());
                        Set<Long> userPersonalityIds = userPersonalityTags.stream()
                                .map(upt -> upt.getPersonalityTag().getId())
                                .collect(Collectors.toSet());
                        
                        // 모든 요구되는 personalityTagIds가 포함되어야 함 (AND 매칭)
                        return userPersonalityIds.containsAll(searchRequest.personalityTagIds());
                    })
                    .collect(Collectors.toList());
        }

        // Primary 결과를 MentorCard로 변환
        List<MentorCard> primaryCards = convertToMentorCards(primaryPersonalityFiltered);
        
        // Secondary 결과를 MentorCard로 변환
        List<MentorCard> secondaryCards = convertToMentorCards(secondaryFilteredMentors);

        return MentorSearchResponse.of(primaryCards, secondaryCards);
    }
    
    private List<MentorCard> convertToMentorCards(List<Mentor> mentors) {
        if (mentors.isEmpty()) {
            return List.of();
        }
        
        // 배치로 관련 데이터 조회 (N+1 방지)
        List<Long> mentorIds = mentors.stream()
                .map(Mentor::getId)
                .collect(Collectors.toList());

        Map<Long, List<UserPersonalityTag>> personalityTagMap = userPersonalityTagRepository
                .findByUserIdInWithPersonalityTag(mentorIds)
                .stream()
                .collect(Collectors.groupingBy(upt -> upt.getUser().getId()));

        // MentorCard로 변환
        return mentors.stream()
                .map(mentor -> {
                    List<UserPersonalityTag> userPersonalityTags = personalityTagMap.getOrDefault(mentor.getId(), List.of());
                    List<MentorCareer> careers = mentorCareerRepository.findByMentorOrderByDisplayOrderAscStartDateDesc(mentor);
                    
                    return MentorCard.from(mentor, userPersonalityTags, careers);
                })
                .collect(Collectors.toList());
    }

    private boolean matchesExperienceRanges(Integer mentorExperience, List<String> experienceRanges) {
        if (mentorExperience == null) {
            return false;
        }
        if (experienceRanges == null || experienceRanges.isEmpty()) {
            return true; // No filter, so everything matches
        }

        for (String experienceRange : experienceRanges) {
            if (matchesExperienceRange(mentorExperience, experienceRange)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesExperienceRange(Integer mentorExperience, String experienceRange) {
        if (mentorExperience == null || experienceRange == null || experienceRange.isBlank()) {
            return false;
        }
        
        return switch (experienceRange) {
            case "1-3년" -> mentorExperience >= 1 && mentorExperience <= 3;
            case "4-6년" -> mentorExperience >= 4 && mentorExperience <= 6;
            case "7년 이상" -> mentorExperience >= 7;
            default -> false;
        };
    }


}
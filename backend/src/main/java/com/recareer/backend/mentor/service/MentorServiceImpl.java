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
                        .id(position.getId())
                        .name(position.getName())
                        .build())
                .collect(Collectors.toList());
        filterOptions.add(FilterOptionsResponseDto.builder()
                .key("positions")
                .title("직업")
                .options(positionOptions)
                .build());

        // Provinces 필터
        List<Province> provinces = provinceRepository.findAll();
        List<FilterOptionDto> provinceOptions = provinces.stream()
                .map(province -> FilterOptionDto.builder()
                        .id(province.getId())
                        .name(province.getName())
                        .build())
                .collect(Collectors.toList());
        filterOptions.add(FilterOptionsResponseDto.builder()
                .key("provinces")
                .title("지역")
                .options(provinceOptions)
                .build());

        // PersonalityTags 필터
        List<PersonalityTag> personalityTags = personalityTagRepository.findAll();
        List<FilterOptionDto> personalityTagOptions = personalityTags.stream()
                .map(tag -> FilterOptionDto.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .build())
                .collect(Collectors.toList());
        filterOptions.add(FilterOptionsResponseDto.builder()
                .key("personalityTags")
                .title("성향")
                .options(personalityTagOptions)
                .build());

        return filterOptions;
    }

    @Override
    @Transactional(readOnly = true)
    public MentorSearchResponse searchMentorsWithRecommendation(MentorSearchRequestDto searchRequest, Long userId) {
        log.info("Searching mentors with recommendation - userId: {}, keyword: {}, positionIds: {}, provinceIds: {}, personalityTagIds: {}",
                userId, searchRequest.keyword(), searchRequest.positionIds(),
                searchRequest.provinceIds(), searchRequest.personalityTagIds());

        // 유저 정보 조회 (지역, 성향 정보 필요)
        User user = null;
        List<Long> userPersonalityTagIds = List.of();
        
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                // 유저의 성향 태그 조회
                List<UserPersonalityTag> userPersonalityTags = userPersonalityTagRepository
                        .findByUserIdInWithPersonalityTag(List.of(userId));
                userPersonalityTagIds = userPersonalityTags.stream()
                        .map(upt -> upt.getPersonalityTag().getId())
                        .collect(Collectors.toList());
                log.info("User {} personality tags: {}", userId, userPersonalityTagIds);
            }
        }

        // 모든 검증된 멘토 조회 (N+1 방지를 위한 fetch join)
        List<Mentor> allMentors = mentorRepository.findAllWithUser();
        
        // 3가지 시나리오 구분
        boolean hasKeyword = searchRequest.keyword() != null && !searchRequest.keyword().isBlank();
        boolean hasFilters = (searchRequest.positionIds() != null && !searchRequest.positionIds().isEmpty()) ||
                           (searchRequest.provinceIds() != null && !searchRequest.provinceIds().isEmpty()) ||
                           (searchRequest.personalityTagIds() != null && !searchRequest.personalityTagIds().isEmpty());

        List<MentorCard> recommendedList;
        List<MentorCard> searchedList;

        // 일관된 로직: 항상 필터링 + 개인화
        // searchedList: 필터링 조건만 적용
        if (hasKeyword || hasFilters) {
            searchedList = getSearchedByConditions(allMentors, searchRequest, hasKeyword, hasFilters);
        } else {
            // 검색 조건 없음: 전체 멘토 리스트
            searchedList = convertToMentorCards(allMentors);
        }
        
        // recommendedList: 필터링 조건 + 개인화 요소(지역/성향)
        if (user != null && (hasKeyword || hasFilters)) {
            recommendedList = getRecommendedWithPersonalization(allMentors, searchRequest, user, userPersonalityTagIds, hasKeyword, hasFilters);
        } else if (user != null) {
            // 검색 조건 없지만 유저 있음: 개인화된 추천만
            recommendedList = getRecommendedByUserPreferences(allMentors, user, userPersonalityTagIds);
        } else {
            // 유저 없음: 빈 추천
            recommendedList = List.of();
        }

        return MentorSearchResponse.of(recommendedList, searchedList);
    }
    
    private List<MentorCard> convertToMentorCards(List<Mentor> mentors) {
        if (mentors.isEmpty()) {
            return List.of();
        }
        
        // 배치로 관련 데이터 조회 (N+1 방지)
        List<Long> mentorIds = mentors.stream()
                .map(Mentor::getId)
                .collect(Collectors.toList());

        // 멘토의 유저 ID 리스트 생성 (PersonalityTag 조회용)
        List<Long> userIds = mentors.stream()
                .map(mentor -> mentor.getUser().getId())
                .collect(Collectors.toList());

        Map<Long, List<UserPersonalityTag>> personalityTagMap = userPersonalityTagRepository
                .findByUserIdInWithPersonalityTag(userIds)
                .stream()
                .collect(Collectors.groupingBy(upt -> upt.getUser().getId()));

        // MentorCard로 변환 (mentor_careers 테이블 사용 안함, mentors.company_id 사용)
        return mentors.stream()
                .map(mentor -> {
                    List<UserPersonalityTag> userPersonalityTags = personalityTagMap.getOrDefault(mentor.getUser().getId(), List.of());
                    // mentor_careers 사용 안함, 빈 리스트 전달
                    List<MentorCareer> careers = List.of();
                    
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

    // 유저 지역 + 성향 기반 추천
    private List<MentorCard> getRecommendedByUserPreferences(List<Mentor> allMentors, User user, List<Long> userPersonalityTagIds) {
        // 유저 정보가 없으면 빈 추천 리스트 반환
        if (user == null) {
            return List.of();
        }
        
        List<Mentor> recommended = allMentors.stream()
                .filter(mentor -> {
                    // 유저와 같은 지역인지 확인
                    boolean sameProvince = user.getProvince() != null && 
                            mentor.getUser().getProvince() != null && 
                            user.getProvince().getId().equals(mentor.getUser().getProvince().getId());
                    
                    // 성향 매칭: 유저와 공통 성향이 있는 멘토 추천
                    boolean matchesPersonality = !userPersonalityTagIds.isEmpty() && 
                            hasMatchingPersonality(mentor.getUser().getId(), userPersonalityTagIds);
                    
                    // OR 조건: 지역이 같거나 성향이 맞으면 추천
                    return sameProvince || matchesPersonality;
                })
                .collect(Collectors.toList());
        
        return convertToMentorCards(recommended);
    }


    // 검색 조건에 따른 필터링 (일관된 로직)
    private List<MentorCard> getSearchedByConditions(List<Mentor> allMentors, MentorSearchRequestDto searchRequest, boolean hasKeyword, boolean hasFilters) {
        List<Mentor> searched = allMentors.stream()
                .filter(mentor -> {
                    // 키워드 조건 확인
                    if (hasKeyword) {
                        String lowerKeyword = searchRequest.keyword().toLowerCase();
                        boolean keywordMatch = (mentor.getUser().getName() != null && mentor.getUser().getName().toLowerCase().contains(lowerKeyword)) ||
                                             (mentor.getDescription() != null && mentor.getDescription().toLowerCase().contains(lowerKeyword)) ||
                                             (mentor.getIntroduction() != null && mentor.getIntroduction().toLowerCase().contains(lowerKeyword));
                        if (!keywordMatch) return false;
                    }
                    
                    // 필터 조건 확인
                    if (hasFilters) {
                        return matchesFilters(mentor, searchRequest);
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
        
        return convertToMentorCards(searched);
    }

    // 필터링 + 개인화 요소 결합 추천
    private List<MentorCard> getRecommendedWithPersonalization(List<Mentor> allMentors, MentorSearchRequestDto searchRequest, User user, List<Long> userPersonalityTagIds, boolean hasKeyword, boolean hasFilters) {
        List<Mentor> recommended = allMentors.stream()
                .filter(mentor -> {
                    // 1단계: 필터링 조건 적용 (검색 조건과 동일)
                    if (hasKeyword) {
                        String lowerKeyword = searchRequest.keyword().toLowerCase();
                        boolean keywordMatch = (mentor.getUser().getName() != null && mentor.getUser().getName().toLowerCase().contains(lowerKeyword)) ||
                                             (mentor.getDescription() != null && mentor.getDescription().toLowerCase().contains(lowerKeyword)) ||
                                             (mentor.getIntroduction() != null && mentor.getIntroduction().toLowerCase().contains(lowerKeyword));
                        if (!keywordMatch) return false;
                    }
                    
                    if (hasFilters && !matchesFilters(mentor, searchRequest)) {
                        return false;
                    }
                    
                    // 2단계: 개인화 요소 적용 (지역 OR 성향)
                    // 유저와 같은 지역인지 확인
                    boolean sameProvince = user.getProvince() != null && 
                            mentor.getUser().getProvince() != null && 
                            user.getProvince().getId().equals(mentor.getUser().getProvince().getId());
                    
                    // 성향 매칭: 유저와 공통 성향이 있는 멘토 추천
                    boolean matchesPersonality = !userPersonalityTagIds.isEmpty() && 
                            hasMatchingPersonality(mentor.getUser().getId(), userPersonalityTagIds);
                    
                    // OR 조건: 지역이 같거나 성향이 맞으면 추천
                    return sameProvince || matchesPersonality;
                })
                .collect(Collectors.toList());
        
        return convertToMentorCards(recommended);
    }

    // 필터 조건 매칭 헬퍼 메서드
    private boolean matchesFilters(Mentor mentor, MentorSearchRequestDto searchRequest) {
        // Position 필터링
        if (searchRequest.positionIds() != null && !searchRequest.positionIds().isEmpty()) {
            boolean positionMatches = mentor.getPositionEntity() != null && 
                    searchRequest.positionIds().contains(mentor.getPositionEntity().getId());
            if (!positionMatches) return false;
        }
        
        // Province 필터링
        if (searchRequest.provinceIds() != null && !searchRequest.provinceIds().isEmpty()) {
            boolean provinceMatches = mentor.getUser() != null && 
                    mentor.getUser().getProvince() != null && 
                    searchRequest.provinceIds().contains(mentor.getUser().getProvince().getId());
            if (!provinceMatches) return false;
        }
        
        // PersonalityTag 필터링
        if (searchRequest.personalityTagIds() != null && !searchRequest.personalityTagIds().isEmpty()) {
            return hasAllPersonalityTags(mentor.getUser().getId(), searchRequest.personalityTagIds());
        }
        
        return true;
    }

    // 유저와 공통 성향이 있는지 확인 (OR 매칭)
    private boolean hasMatchingPersonality(Long mentorUserId, List<Long> userPersonalityTagIds) {
        List<UserPersonalityTag> mentorPersonalityTags = userPersonalityTagRepository
                .findByUserId(mentorUserId);
        
        Set<Long> mentorPersonalityIds = mentorPersonalityTags.stream()
                .map(upt -> upt.getPersonalityTag().getId())
                .collect(Collectors.toSet());
        
        log.info("Personality matching - mentorUserId: {}, mentorTags: {}, userTags: {}", 
                mentorUserId, mentorPersonalityIds, userPersonalityTagIds);
        
        // 유저와 멘토가 공통으로 가진 성향이 하나라도 있으면 true (OR 매칭)
        boolean matches = userPersonalityTagIds.stream()
                .anyMatch(mentorPersonalityIds::contains);
        
        log.info("Personality match result: {}", matches);
        return matches;
    }

    // 모든 성향 태그를 가지고 있는지 확인 (AND 매칭)
    private boolean hasAllPersonalityTags(Long mentorUserId, List<Long> personalityTagIds) {
        List<UserPersonalityTag> mentorPersonalityTags = userPersonalityTagRepository
                .findByUserId(mentorUserId);
        
        Set<Long> mentorPersonalityIds = mentorPersonalityTags.stream()
                .map(upt -> upt.getPersonalityTag().getId())
                .collect(Collectors.toSet());
        
        // 모든 요구되는 personalityTagIds가 포함되어야 함 (AND 매칭)
        return mentorPersonalityIds.containsAll(personalityTagIds);
    }


}
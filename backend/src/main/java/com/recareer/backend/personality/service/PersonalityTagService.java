package com.recareer.backend.personality.service;

import com.recareer.backend.personality.dto.PersonalityTagDto;
import com.recareer.backend.personality.repository.PersonalityTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalityTagService {

    private final PersonalityTagRepository personalityTagRepository;

    @Transactional(readOnly = true)
    public List<PersonalityTagDto> getAllPersonalityTags() {
        return personalityTagRepository.findAll()
                .stream()
                .map(PersonalityTagDto::from)
                .toList();
    }
}
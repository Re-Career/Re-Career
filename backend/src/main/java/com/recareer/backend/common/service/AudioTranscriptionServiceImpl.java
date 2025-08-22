package com.recareer.backend.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioTranscriptionServiceImpl implements AudioTranscriptionService {
    
    @Override
    public String transcribeAudio(MultipartFile audioFile) {
        // TODO: 실제 음성 인식 서비스 (Whisper API 등) 연동 구현
        log.info("음성 파일 전사 처리: {}", audioFile.getOriginalFilename());
        return "전사된 텍스트 (구현 예정)";
    }
    
    @Override
    public String summarizeText(String text) {
        // TODO: 실제 텍스트 요약 서비스 (GPT API 등) 연동 구현
        log.info("텍스트 요약 처리: {} characters", text.length());
        return "요약된 텍스트 (구현 예정)";
    }
}
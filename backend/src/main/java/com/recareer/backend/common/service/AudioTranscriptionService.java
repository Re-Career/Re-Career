package com.recareer.backend.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface AudioTranscriptionService {
    String transcribeAudio(MultipartFile audioFile);
    String summarizeText(String text);
}
package com.recareer.backend.mentoringRecord.service;

import org.springframework.web.multipart.MultipartFile;

public interface AudioTranscriptionService {

    String transcribeAudio(MultipartFile audioFile);

    String summarizeText(String transcribedText);
}
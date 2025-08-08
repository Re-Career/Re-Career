package com.recareer.backend.exception;

import com.recareer.backend.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // Validation 에러 (@NotNull 등)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
    log.error("🚨 Validation 에러: {}", ex.getMessage(), ex);
    String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("입력값이 올바르지 않습니다.");
    return new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
  }

  // JSON 역직렬화 에러 (잘못된 enum 값 등)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleJsonParseError(HttpMessageNotReadableException ex) {
    log.error("🚨 JSON 파싱 에러: {}", ex.getMessage(), ex);
    String message = "잘못된 요청 형식입니다";
    if (ex.getMessage() != null && ex.getMessage().contains("Role")) {
      message = "role은 MENTOR 또는 MENTEE만 허용됩니다.";
    }
    return new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
  }

  // 400
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
    return new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
  }

  // 404
  @ExceptionHandler(EntityNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFound(EntityNotFoundException ex) {
    return new ErrorResponse("해당 데이터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value());
  }

  // 500
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleAll(Exception ex) {
    return new ErrorResponse("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
  }
}
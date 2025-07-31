package com.recareer.backend.exception;

import com.recareer.backend.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
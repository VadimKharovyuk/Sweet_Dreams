package com.example.sweet_dreams.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ImageExceptionHandler {

  @ExceptionHandler(ImageProcessingException.class)
  public ResponseEntity<String> handleImageProcessingException(ImageProcessingException e) {
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(e.getMessage());
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<String> handleIOException(IOException e) {
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Ошибка при обработке файла");
  }
}

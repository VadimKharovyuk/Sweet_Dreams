package com.example.sweet_dreams.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AdminAlreadyExistsException extends RuntimeException {
  public AdminAlreadyExistsException(String message) {
    super(message);
  }
}
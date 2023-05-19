package com.example.sharefavplace.exceptions;

public class ApiNotFoundException extends RuntimeException {
  
  public ApiNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApiNotFoundException(String message) {
    super(message);
  }

}

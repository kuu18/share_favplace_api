package com.example.sharefavplace.exceptions;

public class ApiAuthException extends RuntimeException {

  public ApiAuthException(String message) {
    super(message);
  }

  public ApiAuthException(String message, Throwable cause) {
    super(message, cause);
  }
  
}

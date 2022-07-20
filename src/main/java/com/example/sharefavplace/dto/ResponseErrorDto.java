package com.example.sharefavplace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data@AllArgsConstructor
public class ResponseErrorDto {
  private int status;
  private String message;
}

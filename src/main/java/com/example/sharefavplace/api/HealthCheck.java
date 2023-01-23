package com.example.sharefavplace.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/healthcheck")
public class HealthCheck {
  
  @GetMapping
  public String healthcheck() {
    return "helth check ok";
  }

}

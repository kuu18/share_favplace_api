package com.example.sharefavplace.controller;

import com.example.sharefavplace.repository.Greeting;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloWorld {

  @RequestMapping("/hello")
  public Greeting name() {
    String content = "Hello Docker World!!";
    System.out.println(System.getenv("API_DOMAIN"));
    return new Greeting(1, content);
  }
}

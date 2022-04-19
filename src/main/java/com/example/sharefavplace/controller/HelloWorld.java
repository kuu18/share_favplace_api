package com.example.sharefavplace.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {
  @RequestMapping("/")
  public String name() {
    return "Hello Docker World!!";
  }
}
package com.example.sharefavplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String origin = System.getenv("FRONT_URL");
    registry.addMapping("/**")
            .allowedOrigins(origin)
            .allowedMethods("GET", "POST", "PUT", "DELETE");
  }

}

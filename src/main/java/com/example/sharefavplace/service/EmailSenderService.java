package com.example.sharefavplace.service;

import org.thymeleaf.context.Context;

public interface EmailSenderService {
  public void send(String to, String subject, String bodyTemplateName, Context context);
}

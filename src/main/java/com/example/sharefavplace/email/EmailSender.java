package com.example.sharefavplace.email;

import org.thymeleaf.context.Context;

public interface EmailSender {
  public void send(String to, String subject, String bodyTemplateName, Context context);
}

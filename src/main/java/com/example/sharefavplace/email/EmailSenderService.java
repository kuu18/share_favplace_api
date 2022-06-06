package com.example.sharefavplace.email;

import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailSenderService implements EmailSender{
  private final JavaMailSender mailSender;

  /**
   * メール送信処理
   * 
   * @param to
   * @param subject
   * @param bodyTemplateName
   * @param context
   */
  @Override
  @Async
  public void send(String to, String subject, String bodyTemplateName, Context context) {
    try{
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
      helper.setFrom("sharefavplace@gmail.com");
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(getMailBody(bodyTemplateName, context), true);
      mailSender.send(message);
    } catch(MessagingException e) {
      throw new IllegalStateException("faild to sent email");
    }
  }

  /**
   * tymeleafのメール本文を取得する
   * 
   * @param templateName
   * @param context
   * @return
   */
  private String getMailBody(String templateName, Context context) {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(mailTemplateResolver());
		return templateEngine.process(templateName, context);
	}

  /**
   * テンプレートを解決する
   * 
   * @return
   */
  private ClassLoaderTemplateResolver mailTemplateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setPrefix("mail/");
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		templateResolver.setCacheable(true);
		return templateResolver;
	}
}

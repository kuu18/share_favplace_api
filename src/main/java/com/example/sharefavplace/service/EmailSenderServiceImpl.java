package com.example.sharefavplace.service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

import com.example.sharefavplace.utils.JWTUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService{
  private final JavaMailSender mailSender;
  private Context context = new Context();
  private Map<String, Object> responseBody = new HashMap<>();

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
      // メール送信後トークン削除
      JWTUtils.deleteToken();
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

  /**
   * アカウント有効化メール送信
   * 
   */
  public Map<String, Object> sendAccountActivationMail(String email) {
    // メール送信
    context.setVariable("appName", System.getenv("APP_NAME"));
    context.setVariable("tokenLimit", JWTUtils.LIFETIME + "分");
    context.setVariable("url", System.getenv("FRONT_URL") + "/account/activations?token=" + JWTUtils.lifeTimeToken);
    send(email,
        "メールアドレスのご確認",
        "confirmemail",
        context);
    responseBody.put("message", "メールを送信しました。" + JWTUtils.LIFETIME + "分以内にメール認証を完了してください");
    return responseBody;
  }

  /**
   * メールアドレス更新メール送信
   * 
   */
  public Map<String, Object> sendUpdateEmailMail(String email) {
    // メール送信
    context.setVariable("appName", System.getenv("APP_NAME"));
    context.setVariable("tokenLimit", JWTUtils.LIFETIME + "分");
    context.setVariable("url", System.getenv("FRONT_URL") + "/account/activations?token=" + JWTUtils.lifeTimeToken);
    send(email,
        "メールアドレス更新のご確認",
        "updateemail",
        context);
    responseBody.put("message", "メールを送信しました。" + JWTUtils.LIFETIME + "分以内にメール認証を完了してください");
    return responseBody;
  }

  /**
   * パスワード再設定メール送信
   * 
   */
  public Map<String, Object> sendResetPasswordMail(String email) {
    // メール送信
    context.setVariable("appName", System.getenv("APP_NAME"));
    context.setVariable("tokenLimit", JWTUtils.LIFETIME + "分");
    context.setVariable("url", System.getenv("FRONT_URL") + "/password/reset?token=" + JWTUtils.lifeTimeToken);
    send(email,
        "パスワード再設定のお知らせ",
        "passwordreset",
        context);
    responseBody.put("message", "メールを送信しました。" + JWTUtils.LIFETIME + "分以内にメール認証を完了してください");
    return responseBody;
  }

}

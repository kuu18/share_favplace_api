package com.example.sharefavplace.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;


/**
 * レスポンスユーティリティクラス
 * 
 */
public class ResponseUtils {
  private static final Boolean httpsSecureFlag = System.getenv("SPRING_PROFILE_ACTIVE").equals("production");

  /**
   * レスポンスボディをjson形式のレスポンスに変換
   * 
   * @param responseBody
   * @param response
   */
  public static void jsonResponse(Object responseBody, HttpServletResponse response) {
    try {
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * 認証エラー時のレスポンス
   * 
   * @param response
   * @param message
   */
  public static void unauthorizedResponse(HttpServletRequest request, HttpServletResponse response, String message) {
    deleteTokenCookie(request, response);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("error_messages", message);
    jsonResponse(responseBody, response);
  }

  /**
   * アクセストークンをcookieに保存
   * 
   * @param token
   * @param response
   */
  public static void setAccessTokenToCookie(String token, HttpServletResponse response) {
    ResponseCookieBuilder cookieBuilder = ResponseCookie.from("access_token", token)
      .maxAge(2592000)
      .httpOnly(true)
      .secure(httpsSecureFlag)
      .path("/");
    if(httpsSecureFlag) cookieBuilder.sameSite(SameSite.NONE.attributeValue());
    ResponseCookie cookie = cookieBuilder.build();
    response.addHeader("Set-Cookie", cookie.toString());
  }

  /**
   * リフレッシュトークンをcookieに保存
   * 
   * @param token
   * @param response
   */
  public static void setRefreshTokenToCookie(String token, HttpServletResponse response) {
    ResponseCookieBuilder cookieBuilder = ResponseCookie.from("refresh_token", token)
      .maxAge(2592000)
      .httpOnly(true)
      .secure(httpsSecureFlag)
      .path("/");
    if(httpsSecureFlag) cookieBuilder.sameSite(SameSite.NONE.attributeValue());
    ResponseCookie cookie = cookieBuilder.build();
    response.addHeader("Set-Cookie", cookie.toString());
  }

  /**
   * cookieのトークンを削除
   * 
   * @param request
   * @param response
   */
  public static void deleteTokenCookie(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    Arrays.stream(cookies)
    .filter(cookie -> cookie.getName().equals("refresh_token") || cookie.getName().equals("access_token"))
    .forEach(cookie -> { 
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
      }
    );
  }
}

package com.example.sharefavplace.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;

/**
 * レスポンスユーティリティクラス
 * 
 */
public class ResponseUtils {
  private static final Boolean httpsSecureFlag = System.getenv("SPRING_PROFILE_ACTIVE") == "production";

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
  public static void unauthorizedResponse(HttpServletResponse response, String message) {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("message", message);
    jsonResponse(responseBody, response);
  }

  /**
   * アクセストークンをcookieに保存
   * 
   * @param token
   * @param response
   */
  public static void setAccessTokenToCookie(String token, HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from("access_token", token)
        .maxAge(2592000)
        .httpOnly(true)
        .secure(httpsSecureFlag)
        .path("/")
        .build();
    response.addHeader("Set-Cookie", cookie.toString());
  }

  /**
   * リフレッシュトークンをcookieに保存
   * 
   * @param token
   * @param response
   */
  public static void setRefreshTokenToCookie(String token, HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from("refresh_token", token)
        .maxAge(2592000)
        .httpOnly(true)
        .secure(httpsSecureFlag)
        .path("/")
        .build();
    response.addHeader("Set-Cookie", cookie.toString());
  }

}

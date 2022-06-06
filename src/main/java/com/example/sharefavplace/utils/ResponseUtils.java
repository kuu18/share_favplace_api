package com.example.sharefavplace.utils;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;


/**
 * レスポンスユーティリティクラス
 * 
 */
public class ResponseUtils {
  private static final Boolean SECURE = System.getenv("ENV").equals("production");

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
   * アクセストークンをcookieに保存
   * 
   * @param token
   * @param response
   */
  public static void setAccessTokenToCookie(String token, HttpServletResponse response) {
    ResponseCookieBuilder cookieBuilder = ResponseCookie.from("access_token", token)
      .maxAge(2592000)
      .httpOnly(true)
      .secure(SECURE)
      .path("/");
    if(SECURE) cookieBuilder.sameSite(SameSite.NONE.attributeValue());
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
      .secure(SECURE)
      .path("/");
    if(SECURE) cookieBuilder.sameSite(SameSite.NONE.attributeValue());
    ResponseCookie cookie = cookieBuilder.build();
    response.addHeader("Set-Cookie", cookie.toString());
  }

  /**
   * リフレッシュトークンとアクセストークンをcookieに保存
   * 
   * @param accessToken
   * @param refreshToken
   * @param response
   */
  public static void setTokensToCookie(String accessToken, String refreshToken, HttpServletResponse response) {
    setAccessTokenToCookie(accessToken, response);
    setRefreshTokenToCookie(refreshToken, response);
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

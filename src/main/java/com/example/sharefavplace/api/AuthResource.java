package com.example.sharefavplace.api;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.service.UserService;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * 認証リソースクラス
 * 
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthResource {
  private final UserService userService;

  /**
   * リフレッシュトークンによるアクセストークンの再生成
   * 
   * @param request
   * @param response
   * @param refreshToken
   * @return トークンの有効期限
   */
  @GetMapping("/token/refresh")
  public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request, HttpServletResponse response, @CookieValue(name = "refresh_token", required = false)Optional<String> refreshToken) {
    if (refreshToken.isPresent()) {
      try {
        // トークンのデコード
        DecodedJWT decodedJWT = JWTUtils.decodeToken(refreshToken.get());
        String username = decodedJWT.getSubject();
        User user = userService.findByUsername(username);
        String issure = request.getRequestURL().toString();
        // 有効期限1時間
        Date accessTokenExpiresAt = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
        // 発行日
        Date issuedAt = new Date();
        // アクセストークンの再生成
        String accessToken = JWTUtils.createAccessToken(user, issure, accessTokenExpiresAt, issuedAt);
        // トークンをcookieに保存
        ResponseUtils.setAccessTokenToCookie(accessToken, response);
        ResponseUtils.setRefreshTokenToCookie(refreshToken.get(), response);
        // レスポンスの生成
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("access_token_exp", accessTokenExpiresAt.getTime());
        responseBody.put("refresh_token_exp", decodedJWT.getExpiresAt().getTime());
        return ResponseEntity.ok().body(responseBody);
      } catch (JWTVerificationException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("error_message", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
      }
    }
    Map<String, Object> error = new HashMap<>();
    error.put("error_message", "リフレッシュトークンがありません。");
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  /**
   * ログアウト処理（Cookieのトークンを削除）
   * 
   * @param request
   * @param response
   * @return ログアウトメッセージ
   */
  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    Arrays.stream(cookies)
    .filter(cookie -> cookie.getName().equals("refresh_token") || cookie.getName().equals("access_token"))
    .forEach(cookie -> { 
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
      }
    );
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("message", "ログアウトしました。");
    return ResponseEntity.ok().body(responseBody);
  }
}

package com.example.sharefavplace.api;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.dto.ResponseUserDto;
import com.example.sharefavplace.mapper.UserToUserDtoMapper;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.service.UserService;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
        // アクセストークンの再生成
        String accessToken = JWTUtils.createAccessToken(user, issure, accessTokenExpiresAt);
        // トークンをcookieに保存
        ResponseUtils.setAccessTokenToCookie(accessToken, response);
        ResponseUtils.setRefreshTokenToCookie(refreshToken.get(), response);
        // レスポンスの生成
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("access_token_exp", accessTokenExpiresAt.getTime());
        responseBody.put("refresh_token_exp", decodedJWT.getExpiresAt().getTime());
        return ResponseEntity.ok().body(responseBody);
      } catch (JWTVerificationException e) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        throw new RuntimeException(e.getMessage());
      }
    }
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    throw new RuntimeException("リフレッシュトークンがありません。");
  }

  /**
   * ログアウト処理（Cookieのトークンを削除）
   * 
   * @param request
   * @param response
   * @return ログアウトメッセージ
   */
  @DeleteMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
    ResponseUtils.deleteTokenCookie(request, response);
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("message", "ログアウトしました。");
    return ResponseEntity.ok().body(responseBody);
  }

  /**
   * メールを認証し、アカウントを有効化する
   * 
   * @param autorizationHeader
   * @param request
   * @param response
   * @return
   */
  @GetMapping("account_activations")
  @Transactional
  public ResponseEntity<Map<String, Object>> accountActivate(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String autorizationHeader,
  HttpServletRequest request, HttpServletResponse response) {
    // ヘッダートークンのデコード
    String TOKEN_PREFIX = "Bearer ";
    DecodedJWT decodedJWT = JWTUtils.decodeToken(autorizationHeader.substring(TOKEN_PREFIX.length()));
    String username = decodedJWT.getSubject();
    User user = userService.findByUsername(username);
    if(!user.getActivated()){
      // ユーザーのactivatedを更新
      userService.updateActivated(user);
      // トークンの生成
      String issure = request.getRequestURL().toString();
      Date accessTokenExpiresAt = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      calendar.add(Calendar.MONTH, 1);
      Date refreshTokenExpiresAt = calendar.getTime();
      String accessToken = JWTUtils.createAccessToken(user, issure, accessTokenExpiresAt);
      String refreshToken = JWTUtils.createRefreshToken(user, issure, refreshTokenExpiresAt);
      // クッキーにトークンを保存
      ResponseUtils.setAccessTokenToCookie(accessToken, response);
      ResponseUtils.setRefreshTokenToCookie(refreshToken, response);
      // レスポンスの生成
      ResponseUserDto responseUser = UserToUserDtoMapper.INSTANCE.userToUserDto(user);
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("user", responseUser);
      responseBody.put("access_token_exp", accessTokenExpiresAt.getTime());
      responseBody.put("refresh_token_exp", refreshTokenExpiresAt.getTime());
      responseBody.put("message", "Welcom To ShareFavplace!!");
      return ResponseEntity.ok().body(responseBody);
    }
    Map<String, Object> error = new HashMap<>();
    error.put("error_message", "このメールアドレスは認証済みです。");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
}

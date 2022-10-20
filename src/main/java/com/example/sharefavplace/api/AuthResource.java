package com.example.sharefavplace.api;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.exceptions.ApiAuthException;
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.service.AuthService;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  private final AuthService authService;

  /**
   * リフレッシュトークンによるアクセストークンの再生成
   * 
   * @param request
   * @param response
   * @param refreshToken
   * @return トークンの有効期限
   */
  @GetMapping("/token/refresh")
  public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request, HttpServletResponse response,
      @CookieValue(name = "refresh_token", required = false) Optional<String> refreshToken) {
    if (!refreshToken.isPresent()) {
      throw new ApiAuthException("リフレッシュトークンがありません。");
    }
    // トークンのデコード
    DecodedJWT decodedJWT = JWTUtils.decodeToken(refreshToken.get());
    JWTUtils.refreshToken = refreshToken.get();
    return ResponseEntity.ok().body(authService.tokenRefresh(decodedJWT, request.getRequestURL().toString(), response));
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
    return ResponseEntity.ok().build();
  }

  /**
   * メールアドレスを認証し、アカウントを有効化する
   * 
   * @param authorizationHeader
   * @param request
   * @param response
   * @return
   */
  @GetMapping("account_activations")
  public ResponseEntity<Map<String, Object>> accountActivate(
      @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,
      HttpServletRequest request, HttpServletResponse response) {
    // ヘッダートークンのデコード
    DecodedJWT decodedJWT = JWTUtils.decodeToken(authorizationHeader.substring(JWTUtils.TOKEN_PREFIX.length()));
    String issure = request.getRequestURL().toString();
    return ResponseEntity.ok().body(authService.accountActivate(decodedJWT, issure, response));
  }

  /**
   * 新しいメールアドレスを認証し、メールアドレスを更新する
   * 
   * @param authorizationHeader
   * @param request
   * @param response
   * @return
   */
  @PostMapping("account_updateemail")
  public ResponseEntity<Map<String, Object>> accountUpdateEmail(
      @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,
      @RequestBody UserParam param,
      HttpServletRequest request, HttpServletResponse response) {
    // ヘッダートークンのデコード
    DecodedJWT decodedJWT = JWTUtils.decodeToken(authorizationHeader.substring(JWTUtils.TOKEN_PREFIX.length()));
    String issure = request.getRequestURL().toString();
    return ResponseEntity.ok().body(authService.updateEmail(decodedJWT, param, issure, response));
  }

}

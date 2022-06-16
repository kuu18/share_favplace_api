package com.example.sharefavplace.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.dto.ResponseUserDto;
import com.example.sharefavplace.mapper.UserParamToUserMapper;
import com.example.sharefavplace.mapper.UserToUserDtoMapper;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;
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
        // アクセストークンの再生成
        Map<String, Object> accessTokenMap = JWTUtils.createAccessToken(user, issure);
        String accessToken = accessTokenMap.get("token").toString();
        // トークンをcookieに保存
        ResponseUtils.setTokensToCookie(accessToken, refreshToken.get(), response);
        // レスポンスの生成
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("access_token_exp", accessTokenMap.get("exp"));
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
  @Transactional
  public ResponseEntity<Map<String, Object>> accountActivate(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,
  HttpServletRequest request, HttpServletResponse response) {
    // ヘッダートークンのデコード
    DecodedJWT decodedJWT = JWTUtils.decodeToken(authorizationHeader.substring(JWTUtils.TOKEN_PREFIX.length()));
    String username = decodedJWT.getSubject();
    User user = userService.findByUsername(username);
    if(!user.getActivated()){
      // ユーザーのactivatedを更新
      userService.updateActivated(user);
      // トークンの生成
      String issure = request.getRequestURL().toString();
      Map<String, Object> accessTokenMap = JWTUtils.createAccessToken(user, issure);
      Map<String, Object> refreshTokenMap = JWTUtils.createRefreshToken(user, issure);
      String accessToken = accessTokenMap.get("token").toString();
      String refreshToken = refreshTokenMap.get("token").toString();
      // クッキーにトークンを保存
      ResponseUtils.setTokensToCookie(accessToken, refreshToken, response);
      // レスポンスの生成
      ResponseUserDto responseUser = UserToUserDtoMapper.INSTANCE.userToUserDto(user);
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("user", responseUser);
      responseBody.put("access_token_exp", accessTokenMap.get("exp"));
      responseBody.put("refresh_token_exp", refreshTokenMap.get("exp"));
      responseBody.put("message", "Welcom To ShareFavplace!!");
      return ResponseEntity.ok().body(responseBody);
    }
    Map<String, Object> error = new HashMap<>();
    error.put("error_message", "このメールアドレスは認証済みです。");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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
  @Transactional
  public ResponseEntity<Map<String, Object>> accountUpdateEmail(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,
  @RequestBody UserParam param,
  HttpServletRequest request, HttpServletResponse response) {
    Map<String, Object> responseBody = new HashMap<>();
    // ヘッダートークンのデコード
    DecodedJWT decodedJWT = JWTUtils.decodeToken(authorizationHeader.substring(JWTUtils.TOKEN_PREFIX.length()));
    String username = decodedJWT.getSubject();
    User user = userService.findByUsername(username);
    if(user.getEmail().equals(param.getEmail())) {
      responseBody.put("error_message", "このメールアドレスは認証済みです。");
      return ResponseEntity.badRequest().body(responseBody);
    }
    // メールアドレスの更新
    User updateUser = UserParamToUserMapper.INSTANCE.userParamToUser(param);
    updateUser.setId(user.getId());
    userService.updateEmail(updateUser);
    // トークンの生成
    String issure = request.getRequestURL().toString();
    Map<String, Object> accessTokenMap = JWTUtils.createAccessToken(user, issure);
    Map<String, Object> refreshTokenMap = JWTUtils.createRefreshToken(user, issure);
    String accessToken = accessTokenMap.get("token").toString();
    String refreshToken = refreshTokenMap.get("token").toString();
    // クッキーにトークンを保存
    ResponseUtils.setTokensToCookie(accessToken, refreshToken, response);
    // レスポンスの生成
    user.setEmail(updateUser.getEmail());
    ResponseUserDto responseUser = UserToUserDtoMapper.INSTANCE.userToUserDto(user);
    responseBody.put("user", responseUser);
    responseBody.put("access_token_exp", accessTokenMap.get("exp"));
    responseBody.put("refresh_token_exp", refreshTokenMap.get("exp"));
    responseBody.put("message", "メールアドレスを更新しました。");
    return ResponseEntity.ok().body(responseBody);
  }
}

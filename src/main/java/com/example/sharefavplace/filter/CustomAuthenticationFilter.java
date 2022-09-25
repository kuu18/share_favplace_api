package com.example.sharefavplace.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.sharefavplace.mapper.ToUserMapper;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.LoginParam;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;

  /**
   * 認証処理
   * 
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
    try {
      // jsonからLogiParamへの変換
      LoginParam loginParam = new ObjectMapper().readValue(request.getInputStream(), LoginParam.class);
      String username = loginParam.getUsername();
      String password = loginParam.getPassword();
      // usernameとpasswordによる認証
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
          password);
      return authenticationManager.authenticate(authenticationToken);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 認証成功後の処理（JWTトークン生成）
   * 
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
      Authentication authentication) throws ServletException {
    // 認証されたユーザーの取得
    User user = (User) authentication.getPrincipal();
    Map<String, Object> responseBody = new HashMap<>();
    // メールアドレス認証済みでない場合
    if(!user.getActivated()){
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      responseBody.put("error_message", "メールアドレスを認証してください");
    } else {
      String requestUrl = request.getRequestURL().toString();
      // トークンの生成
      Map<String, Object> accessTokenMap = JWTUtils.createAccessToken(user, requestUrl);
      Map<String, Object> refreshTokenMap = JWTUtils.createRefreshToken(user, requestUrl);
      String accessToken = accessTokenMap.get("token").toString();
      String refreshToken = refreshTokenMap.get("token").toString();
      // クッキーにトークンを保存
      ResponseUtils.setTokensToCookie(accessToken, refreshToken, response);
      // レスポンスの生成
      User responseUser = ToUserMapper.INSTANCE.toResponseUser(user);
      responseBody.put("user", responseUser);
      responseBody.put("access_token_exp", accessTokenMap.get("exp"));
      responseBody.put("refresh_token_exp", refreshTokenMap.get("exp"));
    }
    ResponseUtils.jsonResponse(responseBody, response);
  }

  /**
   * 認証失敗時の処理
   * 
   */
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException failed) throws IOException, ServletException {
    String message = "ユーザー名またはパスワードが違います";
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("error_message", message);
    ResponseUtils.jsonResponse(responseBody, response);
  }
}

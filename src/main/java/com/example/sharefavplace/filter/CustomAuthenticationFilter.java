package com.example.sharefavplace.filter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.sharefavplace.dto.ResponseUserDto;
import com.example.sharefavplace.mapper.UserToUserDtoMapper;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.LoginParam;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    String requestUrl = request.getRequestURL().toString();
    // 有効期限1時間
    Date accessTokenExpiresAt = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
    // 有効期限iヶ月
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MONTH, 1);
    Date refreshTokenExpiresAt = calendar.getTime();
    // トークンの生成
    String accessToken = JWTUtils.createAccessToken(user, requestUrl, accessTokenExpiresAt);
    String refreshToken = JWTUtils.createRefreshToken(user, requestUrl, refreshTokenExpiresAt);
    // クッキーにトークンを保存
    ResponseUtils.setAccessTokenToCookie(accessToken, response);
    ResponseUtils.setRefreshTokenToCookie(refreshToken, response);
    // レスポンスの生成
    ResponseUserDto responseUser = UserToUserDtoMapper.INSTANCE.userToUserDto(user);
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("user", responseUser);
    responseBody.put("access_token_exp", accessTokenExpiresAt);
    responseBody.put("refresh_token_exp", refreshTokenExpiresAt);
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
    ResponseUtils.unauthorizedResponse(request, response, message);
  }
}

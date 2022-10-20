package com.example.sharefavplace.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.exceptions.ApiRequestException;
import com.example.sharefavplace.mapper.ToUserMapper;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
  private final UserService userService;
  private Map<String, Object> responseBody = new HashMap<>();

  /**
   * トークンリフレッシュ処理
   * 
   * @param refreshToken
   * @param issure
   */
  @Override
  public Map<String, Object> tokenRefresh(DecodedJWT decodedJWT, String issure, HttpServletResponse response) {
    String username = decodedJWT.getSubject();
    User user = userService.findByUsername(username);
    // アクセストークンの再生成
    JWTUtils.createAccessToken(user, issure);
    responseBody.put("access_token_exp", JWTUtils.accessTokenExp);
    responseBody.put("refresh_token_exp", decodedJWT.getExpiresAt().getTime());
    // トークンをクッキーに保存
    ResponseUtils.setTokensToCookie(JWTUtils.accessToken, JWTUtils.refreshToken, response);
    // トークン削除
    JWTUtils.deleteToken();
    return responseBody;
  }

  /**
   * アカウント有効化処理
   * 
   * @param authorizationHeader
   * @param issure
   * @param response
   * @return response
   */
  @Override
  public Map<String, Object> accountActivate(DecodedJWT decodedJWT, String issure, HttpServletResponse response) {
    String username = decodedJWT.getSubject();
    User user = userService.findByUsername(username);
    // アカウント有効化済みの場合
    if (user.getActivated()) {
      throw new ApiRequestException("このアカウントは認証済みです");
    }
    // ユーザーのactivatedを更新
    userService.updateActivated(user);
    // トークンの生成
    JWTUtils.createAccessToken(user, issure);
    JWTUtils.createRefreshToken(user, issure);
    // クッキーにトークンを保存
    ResponseUtils.setTokensToCookie(JWTUtils.accessToken, JWTUtils.refreshToken, response);
    // レスポンス生成
    responseBody.put("user", user);
    responseBody.put("access_token_exp", JWTUtils.accessTokenExp);
    responseBody.put("refresh_token_exp", JWTUtils.refreshTokenExp);
    responseBody.put("message", "Welcom To ShareFavplace!!");
    // トークン削除
    JWTUtils.deleteToken();
    return responseBody;
  }

  /**
   * メールアドレス認証＆更新処理
   * 
   * @param authorizationHeader
   * @param userparam
   * @param issure
   * @param response
   * @return responseBody
   */
  @Override
  public Map<String, Object> updateEmail(DecodedJWT decodedJWT, UserParam param, String issure, HttpServletResponse response) {
    // リクエストのメールアドレスでユーザー検索
    User user = userService.findByEmail(param.getEmail());
    // リクエストのメールアドレスのユーザーが既に存在する場合
    if (user != null) {
      throw new ApiRequestException("メールアドレスは認証済みです。");
    }
    String username = decodedJWT.getSubject();
    user = userService.findByUsername(username);
    //　ユーザーが存在しない場合
    if (user == null) {
      throw new ApiRequestException("ユーザーが見つかりません。");
    }
    // メールアドレスの更新
    User updateUser = ToUserMapper.INSTANCE.userParamToUser(param);
    updateUser.setId(user.getId());
    userService.updateEmail(updateUser);
    // トークンの生成
    JWTUtils.createAccessToken(user, issure);
    JWTUtils.createRefreshToken(user, issure);
    // クッキーにトークンを保存
    ResponseUtils.setTokensToCookie(JWTUtils.accessToken, JWTUtils.refreshToken, response);
    // レスポンスの生成
    user.setEmail(updateUser.getEmail());
    responseBody.put("user", user);
    responseBody.put("access_token_exp", JWTUtils.accessTokenExp);
    responseBody.put("refresh_token_exp", JWTUtils.refreshTokenExp);
    responseBody.put("message", "メールアドレスを更新しました。");
    // トークン削除
    JWTUtils.deleteToken();
    return responseBody;
  }
}

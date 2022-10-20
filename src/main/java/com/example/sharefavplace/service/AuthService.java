package com.example.sharefavplace.service;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.param.UserParam;

public interface AuthService {

  /**
   * トークンリフレッシュ処理
   * 
   * @param decodedJWT
   * @param issure
   * @param response
   * @return responseBody
   */
  public Map<String, Object> tokenRefresh(DecodedJWT decodedJWT, String issure, HttpServletResponse response);

  /**
   * アカウント有効化処理
   * 
   * @param decodedJWT
   * @param issure
   * @param response
   * @return responseBody
   */
  public  Map<String, Object> accountActivate(DecodedJWT decodedJWT, String issure, HttpServletResponse response);

  /**
   * メールアドレス認証＆更新処理
   * 
   * @param decodedJWT
   * @param param
   * @param issure
   * @param response
   * @return responseBody
   */
  public  Map<String, Object> updateEmail(DecodedJWT decodedJWT, UserParam param ,String issure, HttpServletResponse response);

}


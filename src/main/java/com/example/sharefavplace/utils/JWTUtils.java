package com.example.sharefavplace.utils;

import java.util.Date;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.model.Role;
import com.example.sharefavplace.model.User;

/**
 * JWTユーティリティクラス
 * 
 */
public class JWTUtils {
  private static final String secret = System.getenv("JWT_SECRET");
  private static final Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
  private static final JWTVerifier verifier = JWT.require(algorithm).build();

  /**
   * アクセストークンの作成
   * 
   * @param user
   * @param issure
   * @return アクセストークン
   */
  public static String createAccessToken(User user, String issure, Date expiresAt, Date issuedAt) {
    String accessToken = JWT.create()
        // トークンの識別子の設定
        .withSubject(user.getUsername())
        // トークンの有効期限（1時間)の設定
        .withExpiresAt(expiresAt)
        //トークンの発行日時
        .withIssuedAt(issuedAt)
        // トークンの発行者の設定
        .withIssuer(issure)
        // 承認権限の設定
        .withClaim("roles", user.getRoles().stream().map(Role::getRolename).collect(Collectors.toList()))
        // アルゴリズムで署名
        .sign(algorithm);
    return accessToken;
  }

  /**
   * リフレッシュトークンの作成
   * 
   * @param username
   * @param issure
   * @return リフレッシュトークン
   */
  public static String createRefreshToken(User user, String issure, Date expiresAt) {
    String refreshToken = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(expiresAt)
        .withIssuer(issure)
        .sign(algorithm);
    return refreshToken;
  }

  /**
   * トークンのデコード
   * 
   * @param token
   * @return デコード後のトークン
   */
  public static DecodedJWT decodeToken(String token) throws JWTVerificationException{
    return verifier.verify(token);
  }

}

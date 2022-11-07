package com.example.sharefavplace.utils;

import java.util.Calendar;
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
  private static final String SECRET = System.getenv("JWT_SECRET");
  private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET.getBytes());
  private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM).build();
  public static final int LIFETIME = 30;
  public static final String TOKEN_PREFIX = "Bearer ";
  public static String accessToken;
  public static String refreshToken;
  public static String lifeTimeToken;
  public static long accessTokenExp;
  public static long refreshTokenExp;

  /**
   * アクセストークンの作成
   * 
   * @param user
   * @param issure
   * @return アクセストークン
   */
  public static void createAccessToken(User user, String issure) {
    Date expiresAt = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
    accessToken = JWT.create()
        // トークンの識別子の設定
        .withSubject(user.getUsername())
        // トークンの有効期限（1時間)の設定
        .withExpiresAt(expiresAt)
        // トークンの発行日時
        .withIssuedAt(new Date())
        // トークンの発行者の設定
        .withIssuer(issure)
        // 承認権限の設定
        .withClaim("roles", user.getRoles().stream().map(Role::getRolename).collect(Collectors.toList()))
        // アルゴリズムで署名
        .sign(ALGORITHM);
    accessTokenExp = expiresAt.getTime();
  }

  /**
   * リフレッシュトークンの作成
   * 
   * @param username
   * @param issure
   * @return トークン
   */
  public static void createRefreshToken(User user, String issure) {
    // 有効期限1週間
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, 7);
    Date expiresAt = calendar.getTime();
    refreshToken = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(expiresAt)
        .withIssuedAt(new Date())
        .withIssuer(issure)
        .sign(ALGORITHM);
    refreshTokenExp = expiresAt.getTime();
  }

  /**
   * ヘッダートークンの作成
   * 
   * @param username
   * @param issure
   * @return トークン
   */
  public static void createLifeTimeToken(User user, String issure) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MINUTE, LIFETIME);
    Date expiresAt = calendar.getTime();
    lifeTimeToken = JWT.create()
        .withSubject(user.getUsername())
        .withExpiresAt(expiresAt)
        .withIssuedAt(new Date())
        .withIssuer(issure)
        .withClaim("roles", user.getRoles().stream().map(Role::getRolename).collect(Collectors.toList()))
        .sign(ALGORITHM);
  }

  /**
   * トークンのデコード
   * 
   * @param token
   * @return デコード後のトークン
   */
  public static DecodedJWT decodeToken(String token) {
    try {
      DecodedJWT decodedJWT = VERIFIER.verify(token);
      return decodedJWT;
    } catch (JWTVerificationException e) {
      throw new JWTVerificationException(e.getMessage());
    }
  }

  /**
   * トークンの削除
   * 
   */
  public static void deleteToken() {
    accessToken = "";
    refreshToken = "";
    lifeTimeToken = "";
  } 

}

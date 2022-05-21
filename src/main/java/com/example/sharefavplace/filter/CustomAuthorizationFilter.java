package com.example.sharefavplace.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * リソースへの承認フィルター
 * 
 */
public class CustomAuthorizationFilter extends OncePerRequestFilter {
  private static final String TOKEN_PREFIX = "Bearer ";
  /**
   * 全リクエストの最初に呼び出されるリソースへの承認を行うメソッド
   * 
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String servletPath = request.getServletPath();
    // 承認のいらないリソースへのパスの場合は何もしない
    if (servletPath.equals("/api/v1/login") || servletPath.equals("/api/v1/user/create")
        || servletPath.equals("/api/v1/token/refresh") || servletPath.equals("/api/v1/logout")) {
      // フィルターを抜ける
      filterChain.doFilter(request, response);
    } else {
      String autorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      Optional<Cookie> cookieToken = Arrays.stream(request.getCookies())
          .filter(cookie -> cookie.getName().equals("access_token")).reduce((s, cookie) -> cookie);
      // リクエストヘッダーのアクセストークンによる承認
      if (autorizationHeader != null && autorizationHeader.startsWith(TOKEN_PREFIX)) {
        try {
          // Authorizationヘッダーからトークンを取得
          String token = autorizationHeader.substring(TOKEN_PREFIX.length());
          // トークンのデコード
          DecodedJWT decodedJWT = JWTUtils.decodeToken(token);
          String username = decodedJWT.getSubject();
          String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
          Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
          Arrays.stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
          // トークンから得た情報でリソースへの承認権限を設定する
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
              null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          // フィルターを抜ける
          filterChain.doFilter(request, response);
        } catch (JWTVerificationException e) {
          // トークン認証エラー時のレスポンス
          ResponseUtils.unauthorizedResponse(response, e.getMessage());
        }
        // Cookieのアクセストークンによる承認
      } else if (cookieToken.isPresent()) {
        try {
          String token = cookieToken.get().getValue();
          // トークンのデコード
          DecodedJWT decodedJWT = JWTUtils.decodeToken(token);
          String username = decodedJWT.getSubject();
          String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
          Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
          Arrays.stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
          // トークンから得た情報でリソースへの承認権限を設定する
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
              null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          // フィルターを抜ける
          filterChain.doFilter(request, response);
        } catch (JWTVerificationException e){
          // トークン認証エラー時のレスポンス
          ResponseUtils.unauthorizedResponse(response, e.getMessage());
        }
      } else {
        // トークンがない場合のレスポンス
        String message = "トークンがありません。";
        ResponseUtils.unauthorizedResponse(response, message);
      }
    }
  }
}

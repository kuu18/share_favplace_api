package com.example.sharefavplace.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * リソースへの承認フィルター
 * 
 */
public class CustomAuthorizationFilter extends OncePerRequestFilter {
  /**
   * 全リクエストの最初に呼び出されるリソースへの承認を行うメソッド
   * 
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String servletPath = request.getServletPath();
    // 承認のいらないリソースへのパスの場合は何もしない
    if (servletPath.equals("/api/v1/login") || servletPath.equals("/api/v1/users/create")
        || servletPath.equals("/api/v1/token/refresh") || servletPath.equals("/api/v1/logout")
        || servletPath.equals("/api/v1/users/password/forget")) {
      // フィルターを抜ける
      filterChain.doFilter(request, response);
    } else {
      String autorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
      // cookieが一つも存在しない場合nullとなるため
      Optional<Cookie[]> cookies = Optional.ofNullable(request.getCookies());
      Optional<Cookie> cookieToken = Arrays.stream(cookies.orElse(new Cookie[0]))
          .filter(cookie -> cookie.getName().equals("access_token")).reduce((s, cookie) -> cookie);
      // リクエストヘッダーのアクセストークンによる承認
      if (autorizationHeader != null && autorizationHeader.startsWith(JWTUtils.TOKEN_PREFIX)) {
        try {
          // Authorizationヘッダーからトークンを取得
          String token = autorizationHeader.substring(JWTUtils.TOKEN_PREFIX.length());
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
          // トークン認証エラーの場合
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          Map<String, String> responseBody = new HashMap<>();
          if(e.getClass().equals(TokenExpiredException.class)){
            responseBody.put("error_message", "認証リンクの有効期限が切れています。");
          }else{
            responseBody.put("error_message", "認証に失敗しました。");
          }
          ResponseUtils.jsonResponse(responseBody, response);
          throw new RuntimeException(e.getMessage());
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
          // トークン認証エラーの場合
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          throw new RuntimeException(e.getMessage());
        }
      } else {
        // トークンがない場合
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        throw new RuntimeException("トークンがありません。");
      }
    }
  }
}

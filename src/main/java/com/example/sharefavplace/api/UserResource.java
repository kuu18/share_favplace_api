package com.example.sharefavplace.api;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.dto.ResponseUserDto;
import com.example.sharefavplace.email.EmailSenderService;
import com.example.sharefavplace.mapper.UserParamToUserMapper;
import com.example.sharefavplace.mapper.UserToUserDtoMapper;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UpdatePasswordParam;
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.service.UserService;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.context.Context;

import lombok.RequiredArgsConstructor;

/**
 * ユーザーリソースクラス
 * 
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserResource {

  private final UserService userService;
  private final EmailSenderService emailSenderService;
  private final PasswordEncoder passwordEncoder;

  /**
   * 全ユーザー取得
   * 
   * @return 全ユーザー
   */
  @GetMapping
  public ResponseEntity<List<User>> getUsers(HttpServletRequest request) {
    return ResponseEntity.ok().body(userService.findAllUser());
  }

  /**
   * アクセストークンのユーザー取得
   * 
   * @param accessToken
   * @param response
   * @param request
   * @return
   */
  @GetMapping("/current_user")
  public ResponseEntity<Map<String, Object>> getCurrentUser(
      @CookieValue(name = "access_token", required = false) Optional<String> accessToken,
      HttpServletResponse response,
      HttpServletRequest request) {
    DecodedJWT decodedJWT = JWTUtils.decodeToken(accessToken.get());
    String username = decodedJWT.getSubject();
    User user = userService.findByUsername(username);
    ResponseUserDto responseUser = UserToUserDtoMapper.INSTANCE.userToUserDto(user);
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("user", responseUser);
    return ResponseEntity.ok().body(responseBody);
  }

  /**
   * ユーザー新規登録
   * 
   * @param param
   * @param bindingResult
   * @return 新規登録したユーザー
   */
  @PostMapping("/create")
  @Transactional
  public ResponseEntity<Map<String, Object>> saveUser(@RequestBody @Validated UserParam param,
      BindingResult bindingResult,
      HttpServletRequest request, HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      List<String> errorMessages = bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage())
          .collect(Collectors.toList());
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("error_messages", errorMessages);
      return ResponseEntity.badRequest().body(responseBody);
    }
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/create").toUriString());
    User user = new User();
    user = UserParamToUserMapper.INSTANCE.userParamToUser(param);
    // メールアドレスまたはユーザーネームがすでに登録されているか判定し、登録する
    user = userService.checkExistsAndSaveUser(user);
    // トークンの生成
    String issure = uri.toString();
    String token = JWTUtils.createHeaderToken(user, issure);
    // メール送信
    Context context = new Context();
    context.setVariable("appName", System.getenv("APP_NAME"));
    context.setVariable("tokenLimit", JWTUtils.LIFETIME + "分");
    context.setVariable("url", System.getenv("FRONT_URL") + "/account/activations?token=" + token);
    emailSenderService.send(user.getEmail(),
      "メールアドレスのご確認",
      "confirmemail",
      context);
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("message", "メールを送信しました。" + JWTUtils.LIFETIME + "分以内にメール認証を完了してください");
    return ResponseEntity.created(uri).body(responseBody);
  }

  /**
   * パスワード更新
   * 
   * @param password
   * @param newPassword
   * @param id
   */
  @PostMapping("/update/password")
  public ResponseEntity<Map<String, String>> updatePassword(@RequestBody UpdatePasswordParam param) {
    User user = userService.findByEmail(param.getEmail());
    if(passwordEncoder.matches(param.getPassword(), user.getPassword())){
      user.setPassword(param.getNewPassword());
      userService.updatePassword(user);
      Map<String, String> responseBody = new HashMap<>();
      responseBody.put("message", "パスワードを更新しました。");
      return ResponseEntity.ok().body(responseBody);
    }
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("error_message", "パスワードが違います。");
    return ResponseEntity.badRequest().body(responseBody);
  }

  /**
   * パスワード再設定メール送信
   * 
   * @param email
   * @param request
   * @return
   */
  @PostMapping("/password/forget")
  public ResponseEntity<Map<String, String>> sendEmailResetPassword(@RequestBody UpdatePasswordParam param, HttpServletRequest request) {
    String email = param.getEmail();
    Optional<User> user = Optional.ofNullable(userService.findByEmail(email));
    if(user.isPresent()) {
      String issure = request.getRequestURL().toString();
      String token = JWTUtils.createHeaderToken(user.get(), issure);
      // メール送信
      Context context = new Context();
      context.setVariable("appName", System.getenv("APP_NAME"));
      context.setVariable("tokenLimit", JWTUtils.LIFETIME + "分");
      context.setVariable("url", System.getenv("FRONT_URL") + "/password/reset?token=" + token);
      emailSenderService.send(user.get().getEmail(),
        "パスワード再設定のお知らせ",
        "passwordreset",
        context);
      Map<String, String> responseBody = new HashMap<>();
      responseBody.put("message", "メールを送信しました。" + JWTUtils.LIFETIME + "分以内にパスワード再設定を行なってください。");
      return ResponseEntity.ok().body(responseBody);
    }
    Map<String, String> responseBody = new HashMap<>();
    responseBody.put("error_message", "メールアドレスが登録されていません。");
    return ResponseEntity.badRequest().body(responseBody);
  }

  /**
   * パスワード再設定
   * 
   * @param param
   * @param authorizationHeader
   * @return
   */
  @PostMapping("/password/reset")
  public ResponseEntity<Map<String, Object>> passwordReset(@RequestBody UpdatePasswordParam param,
  @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,
  HttpServletRequest request, HttpServletResponse response) {
    DecodedJWT decodedJWT = JWTUtils.decodeToken(authorizationHeader.substring(JWTUtils.TOKEN_PREFIX.length()));
    String username = decodedJWT.getSubject();
    User user = userService.findByUsername(username);
    user.setPassword(param.getPassword());
    userService.updatePassword(user);
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
    responseBody.put("message", "パスワードを更新しました。");
    return ResponseEntity.ok().body(responseBody);
  }
}

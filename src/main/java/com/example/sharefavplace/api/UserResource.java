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
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.service.UserService;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
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
   * アクセストークンによる現在のユーザー取得
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
  public ResponseEntity<Map<String, Object>> saveUser(
    @RequestBody @Validated(UserParam.CreateGroup.class) UserParam param,
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
   * ユーザー情報の更新
   * 
   * @param param
   * @param request
   * @return
   */
  @RequestMapping("/update")
  @Transactional
  public ResponseEntity<Map<String, Object>> updateUser(
    @RequestBody @Validated(UserParam.UpdateDeleteGroup.class) UserParam param,
    BindingResult bindingResult,
    HttpServletRequest request, HttpServletResponse response) {
    Map<String, Object> responseBody = new HashMap<>();
    // バリデーションエラーの場合
    if (bindingResult.hasErrors()) {
      List<String> errorMessages = bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage())
          .collect(Collectors.toList());
      responseBody.put("error_messages", errorMessages);
      return ResponseEntity.badRequest().body(responseBody);
    }
    User updateUser = new User();
    updateUser = UserParamToUserMapper.INSTANCE.userParamToUser(param);
    // ユーザーの更新
    updateUser = userService.updateUser(updateUser);
    // トークンの生成
    String issure = request.getRequestURL().toString();
    Map<String, Object> accessTokenMap = JWTUtils.createAccessToken(updateUser, issure);
    Map<String, Object> refreshTokenMap = JWTUtils.createRefreshToken(updateUser, issure);
    String accessToken = accessTokenMap.get("token").toString();
    String refreshToken = refreshTokenMap.get("token").toString();
    // クッキーにトークンを保存
    ResponseUtils.setTokensToCookie(accessToken, refreshToken, response);
    ResponseUserDto responseUser = UserToUserDtoMapper.INSTANCE.userToUserDto(updateUser);
    responseBody.put("user", responseUser);
    responseBody.put("access_token_exp", accessTokenMap.get("exp"));
    responseBody.put("refresh_token_exp", refreshTokenMap.get("exp"));
    responseBody.put("message", "ユーザー情報を更新しました。");
    return ResponseEntity.ok().body(responseBody);
  }

  /**
   * メールアドレス更新メール送信
   * 
   * @param param
   * @param bindingResult
   * @param request
   * @return レスポンス
   */
  @PostMapping("/update/email")
  @Transactional
  public ResponseEntity<Map<String, Object>> updateEmail(
    @RequestBody @Validated(UserParam.UpdateDeleteGroup.class) UserParam param,
    BindingResult bindingResult,
    HttpServletRequest request
  ) {
    Map<String, Object> responseBody = new HashMap<>();
    // バリデーションエラーの場合
    if (bindingResult.hasErrors()) {
      List<String> errorMessages = bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage())
          .collect(Collectors.toList());
      responseBody.put("error_messages", errorMessages);
      return ResponseEntity.badRequest().body(responseBody);
    }
    Optional<User> user = userService.findById(param.getId());
    // トークンの生成
    String issure = request.getRequestURL().toString();
    String token = JWTUtils.createHeaderToken(user.get(), issure);
    // メール送信
    Context context = new Context();
    context.setVariable("appName", System.getenv("APP_NAME"));
    context.setVariable("tokenLimit", JWTUtils.LIFETIME + "分");
    context.setVariable("url", System.getenv("FRONT_URL") + "/account/activations?token=" + token + "&email=" + param.getEmail());
    emailSenderService.send(param.getEmail(),
      "メールアドレスのご確認",
      "updateemail",
      context);
    responseBody.put("message", "新しいメールアドレスにメールを送信しました。" + JWTUtils.LIFETIME + "分以内にメール認証を完了してください");
    return ResponseEntity.ok().body(responseBody);
  }

  /**
   * パスワード更新
   * 
   * @param param
   */
  @PostMapping("/update/password")
  @Transactional
  public ResponseEntity<Map<String, String>> updatePassword(@RequestBody UserParam param) {
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
   */
  @PostMapping("/password/forget")
  public ResponseEntity<Map<String, String>> sendEmailResetPassword(@RequestBody UserParam param, HttpServletRequest request) {
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
  @Transactional
  public ResponseEntity<Map<String, Object>> passwordReset(@RequestBody UserParam param,
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

  /**
   * ユーザー削除
   * 
   * @param param
   * @param bindingResult
   * @return レスポンス
   */
  @DeleteMapping("/delete")
  public ResponseEntity<Map<String, Object>> deletUser(@RequestBody @Validated UserParam param,
    BindingResult bindingResult) {
    Map<String, Object> responseBody = new HashMap<>();
    if(bindingResult.hasErrors()) {
      List<String> errorMessages = bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage())
          .collect(Collectors.toList());
      responseBody.put("error_messages", errorMessages);
      return ResponseEntity.badRequest().body(responseBody);
    }
    Optional<User> user = userService.findById(param.getId());
    if (passwordEncoder.matches(param.getPassword(), user.get().getPassword())){
      userService.deleteUser(user.get());
      responseBody.put("message", "アカウントを削除しました。");
      return ResponseEntity.ok().body(responseBody);
    }
    responseBody.put("error_message", "パスワードが違います。");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
  }
}

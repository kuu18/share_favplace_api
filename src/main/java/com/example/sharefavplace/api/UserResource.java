package com.example.sharefavplace.api;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.exceptions.ApiAuthException;
import com.example.sharefavplace.exceptions.ApiRequestException;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.service.EmailSenderServiceImpl;
import com.example.sharefavplace.service.UserServiceImpl;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

/**
 * ユーザーリソースクラス
 * 
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserResource {

  private final UserServiceImpl userService;
  private final EmailSenderServiceImpl emailSenderService;

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
      @CookieValue(name = "access_token", required = false) Optional<String> accessToken) {
    if (!accessToken.isPresent()) {
      throw new ApiAuthException("もう一度ログインしてください。");
    }
    DecodedJWT decodedJWT = JWTUtils.decodeToken(accessToken.get());
    return ResponseEntity.ok().body(userService.getCurrentUser(decodedJWT));
  }

  /**
   * ユーザー新規登録
   * 
   * @param param
   * @param bindingResult
   * @return 新規登録したユーザー
   */
  @PostMapping("/create")
  public ResponseEntity<Map<String, Object>> createUser(
      @RequestBody @Validated(UserParam.CreateGroup.class) UserParam param,
      BindingResult bindingResult,
      HttpServletRequest request, HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body(ResponseUtils.validationErrorResponse(bindingResult));
    }
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/create").toUriString());
    User user = userService.createUserAndToken(param);
    // トークンの生成
    JWTUtils.createLifeTimeToken(user, uri.toString());
    return ResponseEntity.created(uri).body(emailSenderService.sendAccountActivationMail(user.getEmail()));
  }

  /**
   * ユーザー情報の更新
   * 
   * @param param
   * @param request
   * @return
   */
  @RequestMapping("/update")
  public ResponseEntity<Map<String, Object>> updateUser(
      @RequestBody @Validated(UserParam.UpdateDeleteGroup.class) UserParam param,
      BindingResult bindingResult) {
    // バリデーションエラーの場合
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body(ResponseUtils.validationErrorResponse(bindingResult));
    }
    return ResponseEntity.ok().body(userService.updateUser(param));
  }

  /**
   * メールアドレス更新認証メール送信
   * 
   * @param param
   * @param bindingResult
   * @param request
   * @return レスポンス
   */
  @PostMapping("/update/email")
  public ResponseEntity<Map<String, Object>> updateEmail(
      @RequestBody @Validated(UserParam.UpdateDeleteGroup.class) UserParam param,
      BindingResult bindingResult,
      HttpServletRequest request) {
    // バリデーションエラーの場合
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body(ResponseUtils.validationErrorResponse(bindingResult));
    }
    Optional<User> user = userService.findById(param.getId());
    // トークンの生成
    JWTUtils.createLifeTimeToken(user.get(), request.getRequestURL().toString());
    // メールアドレス更新認証メール送信
    return ResponseEntity.ok().body(emailSenderService.sendUpdateEmailMail(param.getEmail()));
  }

  /**
   * パスワード更新
   * 
   * @param param
   */
  @PostMapping("/update/password")
  public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody UserParam param) {
      return ResponseEntity.ok().body(userService.updatePassword(param));
  }

  /**
   * パスワード再設定メール送信
   * 
   * @param email
   * @param request
   */
  @PostMapping("/password/forget")
  public ResponseEntity<Map<String, Object>> sendEmailResetPassword(@RequestBody UserParam param,
      HttpServletRequest request) {
    String email = param.getEmail();
    User user = userService.findByEmail(email);
    if (user == null) {
      throw new ApiRequestException("アカウントが見つかりません。");
    }
    // トークンの生成
    JWTUtils.createLifeTimeToken(user, request.getRequestURL().toString());
    return ResponseEntity.ok().body(emailSenderService.sendResetPasswordMail(email));
  }

  /**
   * パスワード再設定
   * 
   * @param param
   * @param authorizationHeader
   * @return
   */
  @PostMapping("/password/reset")
  public ResponseEntity<Map<String, Object>> passwordReset(@RequestBody UserParam param,
      @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,
      HttpServletRequest request, HttpServletResponse response) {
      DecodedJWT decodedJWT = JWTUtils.decodeToken(authorizationHeader.substring(JWTUtils.TOKEN_PREFIX.length()));
    return ResponseEntity.ok().body(userService.resetPassword(decodedJWT, param, request.getRequestURL().toString(), response));
  }

  /**
   * ユーザーアバター画像更新
   * 
   * @param username
   * @param avatar
   * @return
   */
  @PostMapping("/{username}/avatar")
  public ResponseEntity<Map<String, Object>> uploadAvatar(@PathVariable("username") String username,
      @RequestParam("avatar") MultipartFile avatar) {
      return ResponseEntity.ok().body(userService.updateAvatar(username, avatar));
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
    if (bindingResult.hasErrors()) {
      return ResponseEntity.badRequest().body(ResponseUtils.validationErrorResponse(bindingResult));
    }
    return ResponseEntity.ok().body(userService.deleteUserAndAvatar(param));
  }
}

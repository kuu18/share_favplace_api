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

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    context.setVariable("url", System.getenv("FRONT_URL") + "/activations?token=" + token);
    emailSenderService.send(user.getEmail(),
        "メールアドレスのご確認",
        "confirmemail",
        context);
    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("message", "メールを送信しました。" + JWTUtils.LIFETIME + "分以内にメール認証を完了してください");
    return ResponseEntity.created(uri).body(responseBody);
  }
  
}

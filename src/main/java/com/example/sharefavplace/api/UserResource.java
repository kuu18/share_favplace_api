package com.example.sharefavplace.api;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.sharefavplace.dto.ResponseUserDto;
import com.example.sharefavplace.mapper.UserParamToUserMapper;
import com.example.sharefavplace.mapper.UserToUserDtoMapper;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

/**
 * ユーザーリソースクラス
 * 
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserResource {

  private final UserService userService;

  /**
   * 全ユーザー取得
   * 
   * @return 全ユーザー
   */
  @GetMapping("/users")
  public ResponseEntity<List<User>> getUsers(HttpServletResponse response, HttpServletRequest request) {
    return ResponseEntity.ok().body(userService.findAllUser());
  }

  /**
   * ユーザー新規登録
   * 
   * @param param
   * @param bindingResult
   * @return 新規登録したユーザー
   */
  @PostMapping("/user/create")
  public ResponseEntity<ResponseUserDto> saveUser(@RequestBody @Validated UserParam param, BindingResult bindingResult,
      HttpServletRequest request, HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      // TODO
    }
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/create").toUriString());
    User user = new User();
    user = UserParamToUserMapper.INSTANCE.userParamToUser(param);
    // メールアドレスがすでに登録されているか判定し、登録する
    user = userService.checkEmailAndSaveUser(user);
    // TODO ログイン処理の実装
    // レスポンスユーザーに変換
    ResponseUserDto responseUser = UserToUserDtoMapper.INSTANCE.userToUserDto(user);
    return ResponseEntity.created(uri).body(responseUser);
  }
}

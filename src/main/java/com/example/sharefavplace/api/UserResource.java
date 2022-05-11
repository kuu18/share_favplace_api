package com.example.sharefavplace.api;

import java.net.URI;
import java.util.List;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.service.UserServiceImpl;

import org.springframework.beans.BeanUtils;
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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserResource {

  private final UserServiceImpl userServiceImpl;

  /**
   * 全ユーザー取得
   * 
   * @return 全ユーザー
   */
  @GetMapping("/users")
  public ResponseEntity<List<User>> getUsers() {
    return ResponseEntity.ok().body(userServiceImpl.findAllUser());
  }

  /**
   * ユーザー新規登録
   * 
   * @param param
   * @param bindingResult
   * @return 新規登録したユーザー
   */
  @PostMapping("/user/create")
  public ResponseEntity<User> saveUser(@RequestBody @Validated UserParam param, BindingResult bindingResult) {
    if (bindingResult.hasErrors()){
      //TODO
    }
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/create").toUriString());
    User user = new User();
    BeanUtils.copyProperties(param, user);
    // メールアドレスがすでに登録されているかの判定を行い、登録する
    return ResponseEntity.created(uri).body(userServiceImpl.checkEmailAndSaveUser(user));
  }
}

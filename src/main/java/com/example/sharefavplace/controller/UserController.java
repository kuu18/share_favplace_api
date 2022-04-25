package com.example.sharefavplace.controller;

import java.util.List;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping
  public List<User> getAllUser() {
    return userService.findAll();
  }

  /**
   * ユーザー新規登録
   * 
   * @param param
   * @param bindingResult
   */
  @PostMapping("/create")
  public void createUser(@RequestBody @Validated UserParam param, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      //　エラー処理
    }else{
      // メールアドレスがすでに登録されているかの判定を行い、登録する
      userService.checkEmailAndSaveUser(param);
    }
  }
}

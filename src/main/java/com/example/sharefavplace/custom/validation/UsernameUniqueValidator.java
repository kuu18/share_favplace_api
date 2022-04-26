package com.example.sharefavplace.custom.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

public class UsernameUniqueValidator implements ConstraintValidator<UsernameUnique, String> {
  @Autowired
  UserService userService;
  /**
   * ユーザー名が登録済みの場合バリデーションエラー
   * 
   * @return 判定結果
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    User user = userService.findByUsername(value);
    if (user != null){
      return false;
    }
    return true;
  }
}

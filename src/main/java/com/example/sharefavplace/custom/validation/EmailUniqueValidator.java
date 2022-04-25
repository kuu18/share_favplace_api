package com.example.sharefavplace.custom.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, String> {
  @Autowired
  UserService userService;
  /**
   * emailが登録済みかつユーザーがアクティブ済みの場合バリデーションエラー
   * 
   * @return 判定結果
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    User user = userService.findByEmail(value);
    if (user != null && user.getActivated()){
      return false;
    }
    return true;
  }
}

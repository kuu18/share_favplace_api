package com.example.sharefavplace.custom.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsernameUniqueValidator implements ConstraintValidator<UsernameUnique, String> {
  private final UserService userService;
  /**
   * ユーザー名が登録済みかつユーザーがアクティブ済みの場合バリデーションエラー
   * 
   * @return 判定結果
   */
  // TODO ユーザーが新規登録後アクティブ化するまでの（15分）はactivatedがfalseでも重複を許さないようにする
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    User user = userService.findByUsername(value);
    boolean result = true;
    if (user != null && user.getActivated()){
      result = false;
    }
    return result;
  }
}

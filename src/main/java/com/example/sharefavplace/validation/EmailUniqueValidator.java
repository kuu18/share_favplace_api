package com.example.sharefavplace.validation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.service.UserService;
import com.example.sharefavplace.utils.JWTUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailUniqueValidator implements ConstraintValidator<EmailUnique, String> {
  private final UserService userService;

  /**
   * emailが登録済みかつユーザーがアクティブ済みの場合バリデーションエラー
   * ユーザーが新規登録後アクティブ化するまでの（30分）はactivatedがfalseでも重複を許さない
   * 
   * @return 判定結果
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    Optional<User> user = Optional.ofNullable(userService.findByEmail(value));
    if (!user.isPresent()) {
      return true;
    }
    LocalDateTime createTime = LocalDateTime.ofInstant(user.get().getCreatedAt().toInstant(), ZoneId.systemDefault());
    LocalDateTime currentTime = LocalDateTime.now();
    long minutes = ChronoUnit.MINUTES.between(createTime, currentTime);
    boolean isWithinExpire = minutes <= JWTUtils.LIFETIME;
    if (user.get().getActivated() || isWithinExpire) {
      return false;
    }
    return true;
  }

}

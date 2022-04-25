package com.example.sharefavplace.custom.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * emailが登録済みかつユーザーがアクティブ済みかのバリデーションアノテーション
 * 
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EmailUniqueValidator.class})
public @interface EmailUnique {
  String message() default "メールアドレスはすでに登録されています。";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}

package com.example.sharefavplace.custom.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * usernameが登録済みかのバリデーションアノテーション
 * 
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UsernameUniqueValidator.class})
public @interface UsernameUnique {
  String message() default "ユーザー名はすでに登録されています。";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}


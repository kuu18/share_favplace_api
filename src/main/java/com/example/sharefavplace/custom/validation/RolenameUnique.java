package com.example.sharefavplace.custom.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * rolenameが登録済みかのバリデーションアノテーション
 * 
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RolenameUniqueValidator.class})
public @interface RolenameUnique {
  String message() default "ロールはすでに登録されています。";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}

package com.example.sharefavplace.param;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class UpdatePasswordParam {
  @Email
  @Length(max = 255)
  private String email;
  @Length(min = 8 ,max = 72)
  @Pattern(regexp = "\\A[\\w\\-]+\\z")
  private String password;
  @Length(min = 8 ,max = 72)
  @Pattern(regexp = "\\A[\\w\\-]+\\z")
  private String newPassword;
}

package com.example.sharefavplace.param;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.example.sharefavplace.custom.validation.EmailUnique;
import com.example.sharefavplace.custom.validation.UsernameUnique;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
@Data
public class UserParam {
  private Integer id;
  @NotBlank
  @UsernameUnique
  @Length(max = 50)
  private String username;
  @NotBlank
  @Email
  @EmailUnique
  @Length(max = 255)
  private String email;
  @NotBlank
  @Length(min = 8 ,max = 72)
  @Pattern(regexp = "\\A[\\w\\-]+\\z")
  private String password;
  private Boolean activated = false;
}

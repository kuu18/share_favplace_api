package com.example.sharefavplace.param;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.example.sharefavplace.validation.EmailUnique;
import com.example.sharefavplace.validation.UsernameUnique;

import lombok.Data;
@Data
public class UserParam {
  @NotNull(groups = UpdateDeleteGroup.class)
  private Integer id;
  @NotBlank(groups = CreateGroup.class)
  @UsernameUnique(groups = { CreateGroup.class, UpdateDeleteGroup.class })
  @Length(max = 50, groups = { CreateGroup.class, UpdateDeleteGroup.class })
  private String username;
  @NotBlank(groups = CreateGroup.class)
  @Email(groups = { CreateGroup.class, UpdateDeleteGroup.class })
  @EmailUnique(groups = { CreateGroup.class, UpdateDeleteGroup.class })
  @Length(max = 255, groups = { CreateGroup.class, UpdateDeleteGroup.class })
  private String email;
  @NotBlank(groups = CreateGroup.class)
  @Length(min = 8 ,max = 72, groups = { CreateGroup.class, UpdateDeleteGroup.class })
  @Pattern(regexp = "\\A[\\w\\-]+\\z", groups = { CreateGroup.class, UpdateDeleteGroup.class })
  private String password;
  @Length(min = 8 ,max = 72, groups = { CreateGroup.class, UpdateDeleteGroup.class })
  @Pattern(regexp = "\\A[\\w\\-]+\\z", groups = { CreateGroup.class, UpdateDeleteGroup.class })
  private String newPassword;
  private Boolean activated = false;

  public interface CreateGroup{}
  public interface UpdateDeleteGroup{}
}

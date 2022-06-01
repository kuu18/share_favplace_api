package com.example.sharefavplace.param;

import javax.validation.constraints.NotBlank;

import com.example.sharefavplace.custom.validation.RolenameUnique;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class RoleParam {
  private Integer id;
  @NotBlank
  @Length(max = 50)
  @RolenameUnique
  private String rolename;
}
package com.example.sharefavplace.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.sharefavplace.model.Role;
import com.example.sharefavplace.service.RoleService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RolenameUniqueValidator implements ConstraintValidator<RolenameUnique, String> {
  private final RoleService roleService;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    Role role = roleService.findByRolename(value);
    boolean result = true;
    if (role != null){
      result = false;
    }
    return result;
  }
  
}

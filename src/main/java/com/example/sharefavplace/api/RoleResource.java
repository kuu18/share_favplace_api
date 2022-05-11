package com.example.sharefavplace.api;

import java.net.URI;

import com.example.sharefavplace.model.Role;
import com.example.sharefavplace.param.RoleParam;
import com.example.sharefavplace.service.RoleService;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoleResource {
  
  private final RoleService roleService;

  /**
   * ロール新規登録
   * 
   * @param param
   * @param bindingResult
   * @return 新規登録したRole
   */
  @PostMapping("/role/create")
  public ResponseEntity<Role> saveRole(@RequestBody @Validated RoleParam param, BindingResult bindingResult) {
    if(bindingResult.hasErrors()){
      //TODO
    }
    URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/role/create").toUriString());
    Role role = new Role();
    BeanUtils.copyProperties(param, role);
    return ResponseEntity.created(uri).body(roleService.saveRole(role));
  }
  
  /**
   * ユーザーにロールを付与する
   * 
   * @param param
   * @return 
   */
  @PostMapping("/role/addtouser")
  public ResponseEntity<?> addRoletoUser(@RequestBody RoleToUserParam param) {
    roleService.addRoleToUser(param.getUsername(), param.getRolename());
    return ResponseEntity.ok().build();
  }
}

@Data
class RoleToUserParam {
  private String username;
  private String rolename;
}
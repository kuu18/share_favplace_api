package com.example.sharefavplace.service;

import com.example.sharefavplace.model.Role;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.repository.RoleRepository;
import com.example.sharefavplace.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  /**
   * roleレコードの追加
   * 
   * @param role
   * @return Role
   */
  public Role saveRole(Role role) {
    return roleRepository.save(role);
  }

  /**
   * rolenameによるRoleの取得
   * 
   * @param rolename
   * @return Role
   */
  public Role findByRolename(String rolename) {
    return roleRepository.findByRolename(rolename);
  }

  /**
   * userにroleを付与するメソッド
   * 
   * @param username
   * @param rolename
   */
  public void addRoleToUser(String username, String rolename) {
    User user = userRepository.findByUsername(username);
    Role role = roleRepository.findByRolename(rolename);
    user.getRoles().add(role);
  }
}

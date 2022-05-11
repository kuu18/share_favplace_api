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
public class RoleServiceImpl implements RoleService {
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  /**
   * roleレコードの追加
   * 
   * @param role
   * @return Role
   */
  @Override
  public Role saveRole(Role role) {
    return roleRepository.save(role);
  }

  /**
   * rolenameによるRoleの取得
   * 
   * @param rolename
   * @return Role
   */
  @Override
  public Role findByRolename(String rolename) {
    return roleRepository.findByRolename(rolename);
  }

  /**
   * userにroleを付与するメソッド
   * 
   * @param username
   * @param rolename
   */
  @Override
  public void addRoleToUser(String username, String rolename) {
    User user = userRepository.findByUsername(username);
    Role role = roleRepository.findByRolename(rolename);
    user.getRoles().add(role);
  }
}

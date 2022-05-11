package com.example.sharefavplace.service;

import com.example.sharefavplace.model.Role;

import org.springframework.stereotype.Service;

@Service
public interface RoleService {
  /**
   * roleレコードの追加
   * 
   * @param role
   * @return Role
   */
  public Role saveRole(Role role);

  /**
   * rolenameによるRoleの取得
   * 
   * @param rolename
   * @return Role
   */
  public Role findByRolename(String rolename);

  /**
   * UserにRoleを付与するメソッド
   * 
   * @param username
   * @param rolename
   */
  public void addRoleToUser(String username, String rolename);
}

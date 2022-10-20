package com.example.sharefavplace.repository;

import com.example.sharefavplace.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
  /**
   * emailによるユーザー取得
   * 
   * @param email
   * @return User
   */
  public Role findByRolename(String rolename);
  
}

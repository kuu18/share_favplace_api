package com.example.sharefavplace.repository;

import com.example.sharefavplace.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  /**
   * emailによるユーザー取得
   * 
   * @param email
   * @return User
   */
  public User findByEmail(String email);

  /**
   * usernameによるユーザー取得
   * 
   * @param email
   * @return User
   */
  public User findByUsername(String username);
}

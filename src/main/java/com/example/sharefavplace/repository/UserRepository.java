package com.example.sharefavplace.repository;

import com.example.sharefavplace.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>, UserCustomRepository {
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

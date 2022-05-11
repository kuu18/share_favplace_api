package com.example.sharefavplace.service;

import java.util.List;

import com.example.sharefavplace.model.User;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
  /**
   * 全ユーザー取得
   * 
   * @return 全ユーザー
   */
  public List<User> findAllUser();

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
   * @param username
   * @return User
   */
  public User findByUsername(String username);

  /**
   * userレコードの追加
   * 
   * @param user
   */
  public User saveUser(User user);

  /**
   * userレコードの更新
   * 
   * @param user
   */
  public User updateUser(User user);
}

package com.example.sharefavplace.service;

import java.util.List;

import com.example.sharefavplace.model.Role;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
   * roleレコードの追加
   * 
   * @param role
   */
  public Role saveRole(Role role);

  /**
   * レコードの更新
   * 
   * @param user
   */
  public User updateUser(User user);

  /**
   * ユーザーにRoleを付与するメソッド
   * 
   * @param username
   * @param rolename
   */
  public void addRoleToUser(String username, String rolename);

  /**
   * メールアドレスがすでに登録済みならレコードの更新、登録済みでないなら新規登録
   * Userのactivatedがfalseの場合はemailの重複を許容しているため
   * Userのactivateがtrueの場合はバリデーションエラー（カスタムバリデーション）
   * 
   * @param userParam
   */
  @Transactional
  public User checkEmailAndSaveUser(UserParam userParam);
}

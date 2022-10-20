package com.example.sharefavplace.service;

import java.util.List;
import java.util.Optional;

import com.example.sharefavplace.model.User;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {

  /**
   * 全ユーザー取得
   * 
   * @return AllUser
   */
  public List<User> findAllUser();

  /**
   * idによるユーザー取得
   * 
   * @param id
   * @return
   */
  public Optional<User> findById(Integer id);

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
   * @return User
   */
  public User saveUser(User user);

  /**
   * ユーザーの更新
   * 
   * @param user
   * @return User
   */
  public User updateUser(User user);

  /**
   * アクティブ済みでないuserレコードの更新（新規登録）
   * 
   * @param user
   * @return User
   */
  public User updateNonActivatedUser(User user);

  /**
   * ユーザーのメールアドレス更新
   * 
   * @param user
   * @return 更新件数
   */
  public int updateEmail(User user);

  /**
   * ユーザーのpassword更新
   * 
   * @param user
   * @return 更新件数
   */
  public int updatePassword(User user);

  /**
   * ユーザーのactivated更新
   * 
   * @param user
   * @return 更新件数
   */
  public int updateActivated(User user);

  /**
   * ユーザーのアバター画像更新
   * 
   * @param user
   * @return
   */
  public int updateAvatarUrl(User user);

  /**
   * ユーザーレコードの削除
   * 
   * @param user
   */
  public void deleteUser(User user);

  /**
   * userにroleを付与するメソッド
   * 
   * @param username
   * @param rolename
   */
  public void addRoleToUser(String username, String rolename);

}
package com.example.sharefavplace.repository;

import com.example.sharefavplace.model.User;

import org.springframework.stereotype.Repository;

@Repository
public interface UserCustomRepository {

  /**
   * アクティブ済みでないユーザーの更新（新規登録）
   * 
   * @param user
   * @return User
   */
  public User updateNonActivatedUser(User user);

  /**
   * activatedの更新
   * 
   * @param user
   * @return 更新件数
   */
  public int updateActivated(User user);

  /**
   * パスワードの更新
   * 
   * @param user
   * @return 更新件数
   */
  public int updatePassword(User user);
  
}

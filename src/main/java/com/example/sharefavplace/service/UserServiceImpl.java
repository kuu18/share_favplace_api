package com.example.sharefavplace.service;

import java.util.List;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  /**
   * 全ユーザー取得
   * 
   * @return AllUser
   */
  @Override
  public List<User> findAllUser() {
    return userRepository.findAll();
  }

  /**
   * emailによるユーザー取得
   * 
   * @param email
   * @return User
   */
  @Override
  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }
  
  /**
   * usernameによるユーザー取得
   * 
   * @param username
   * @return User
   */
  @Override
  public User findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  /**
   * userレコードの追加（パスワードエンコーディング）
   * 
   * @param user
   * @return User
   */
  @Override
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  /**
   * userレコードの更新
   * 
   * @param user
   * @return User
   */
  @Override
  public User updateUser(User user) {
    return userRepository.save(user);
  }

  /**
   * メールアドレスがすでに登録済みならユーザーの更新、登録済みでないなら新規登録するメソッド
   * Userのactivatedがfalseの場合はemailの重複を許容するため
   * Userのactivatedがtrueの場合はバリデーションエラー（カスタムバリデーション）
   * 
   * @param newUser
   * @return User
   */
  public User checkEmailAndSaveUser(User newUser) {
    //userのemailでユーザー検索
    User user = findByEmail(newUser.getEmail());
    //　メールアドレスがすでに登録されている場合更新処理
    if (user != null) {
      newUser.setId(user.getId());
      newUser.setCreatedAt(user.getCreatedAt());
      user = updateUser(newUser);
    } else {
      // メールアドレスがすでに登録されていない場合新規登録
      user = saveUser(newUser);
    }
    return user;
  }
}
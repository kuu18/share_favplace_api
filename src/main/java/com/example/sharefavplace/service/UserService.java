package com.example.sharefavplace.service;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.repository.UserRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  @Autowired
  UserRepository userRepository;

  /**
   * emailによるユーザー取得
   * 
   * @param email
   * @return User
   */
  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }
  
  /**
   * usernameによるユーザー取得
   * 
   * @param username
   * @return User
   */
  public User findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  /**
   * レコードの追加
   * 
   * @param user
   */
  public void save(User user) {
    userRepository.save(user);
  }

  /**
   * レコードの更新
   * 
   * @param user
   */
  public void update(User user) {
    userRepository.save(user);
  }

  /**
   * メールアドレスがすでに登録済みならレコードの更新、登録済みでないなら新規登録
   * Userのactivatedがfalseの場合はemailの重複を許容しているため
   * Userのactivateがtrueの場合はバリデーションエラー（カスタムバリデーション）
   * 
   * @param userParam
   */
  @Transactional
  public void checkEmailAndSaveUser(UserParam userParam) {
    User user = findByEmail(userParam.getEmail());
    //　メールアドレスがすでに登録されている場合更新処理
    if (user != null) {
      userParam.setId(user.getId());
      BeanUtils.copyProperties(userParam, user);
      update(user);
    } else {
      // メールアドレスがすでに登録されていない場合新規登録
      user = new User();
      BeanUtils.copyProperties(userParam, user);
      save(user);
    }
  }
}

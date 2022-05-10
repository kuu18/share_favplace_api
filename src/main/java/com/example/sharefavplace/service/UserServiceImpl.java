package com.example.sharefavplace.service;

import java.util.List;

import com.example.sharefavplace.model.Role;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.repository.RoleRepository;
import com.example.sharefavplace.repository.UserRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

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
   * メールアドレスがすでに登録済みならレコードの更新、登録済みでないなら新規登録するメソッド
   * Userのactivatedがfalseの場合はemailの重複を許容しているため
   * Userのactivateがtrueの場合はバリデーションエラー（カスタムバリデーション）
   * 
   * @param userParam
   * @return User
   */
  @Override
  public User checkEmailAndSaveUser(UserParam userParam) {
    //userParamのemailでユーザー検索
    User user = findByEmail(userParam.getEmail());
    //　メールアドレスがすでに登録されている場合更新処理
    if (user != null) {
      userParam.setId(user.getId());
      BeanUtils.copyProperties(userParam, user);
      user = updateUser(user);
    } else {
      // メールアドレスがすでに登録されていない場合新規登録
      user = new User();
      BeanUtils.copyProperties(userParam, user);
      user = saveUser(user);
    }
    return user;
  }

  /**
   * userにroleを付与するメソッド
   * 
   * @param username
   * @param rolename
   */
  @Override
  public void addRoleToUser(String username, String rolename) {
    User user = userRepository.findByUsername(username);
    Role role = roleRepository.findByRolename(rolename);
    user.getRoles().add(role);
  }

  /**
   * roleレコードの追加
   * 
   * @param role
   * @return Role
   */
  @Override
  public Role saveRole(Role role) {
    return roleRepository.save(role);
  }
}
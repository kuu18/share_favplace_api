package com.example.sharefavplace.service;

import java.util.List;

import com.example.sharefavplace.model.Role;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.repository.RoleRepository;
import com.example.sharefavplace.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * springsecurityに認証するユーザーを伝えるメソッド
   * 
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) throw new UsernameNotFoundException("ユーザーが存在しません。");
    return user;
  }

  /**
   * 全ユーザー取得
   * 
   * @return AllUser
   */
  public List<User> findAllUser() {
    return userRepository.findAll();
  }

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
   * userレコードの追加（パスワードエンコーディング）
   * 
   * @param user
   * @return User
   */
  public User saveUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  /**
   * userレコードの更新
   * 
   * @param user
   * @return User
   */
  public User updateUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  /**
   * userにroleを付与するメソッド
   * 
   * @param username
   * @param rolename
   */
  public void addRoleToUser(String username, String rolename) {
    User user = userRepository.findByUsername(username);
    Role role = roleRepository.findByRolename(rolename);
    user.getRoles().add(role);
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
      user = updateUser(newUser);
    } else {
      // メールアドレスがすでに登録されていない場合新規登録
      user = saveUser(newUser);
      addRoleToUser(user.getUsername(), "ROLE_USER");
    }
    return user;
  }
}
package com.example.sharefavplace.service;

import java.util.List;
import java.util.Optional;

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
    if (user == null)
      throw new UsernameNotFoundException("ユーザーが存在しません。");
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
    User saveUser = userRepository.save(user);
    addRoleToUser(user.getUsername(), "ROLE_USER");
    return saveUser;
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
   * アクティブ済みでないuserレコードの更新（新規登録）
   * 
   * @param user
   * @return User
   */
  public User updateNonActivatedUser(User user) {
    return userRepository.updateNonActivatedUser(user);
  }

  /**
   * ユーザーのactivated更新
   * 
   * @param user
   * @return
   */
  public int updateActivated(User user) {
    return userRepository.updateActivated(user);
  }

  /**
   * userレコードの削除
   * 
   * @param user
   */
  public void deleteUser(User user) {
    userRepository.delete(user);
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
   * メールアドレスまたはユーザーネームがすでに登録済みなら更新、登録済みでないなら新規登録するメソッド
   * Userのactivatedがfalseの場合は重複を許容するため
   * Userのactivatedがtrueの場合は重複を許容しない（activatedがtrueの場合はバリデーションエラー）
   * 
   * @param newUser
   * @return User
   */
  public User checkExistsAndSaveUser(User newUser) {
    // userのemailでユーザー検索
    Optional<User> emailUser = Optional.ofNullable(findByEmail(newUser.getEmail()));
    // userのusernameでユーザー検索
    Optional<User> usernameUser = Optional.ofNullable(findByUsername(newUser.getUsername()));
    // メールアドレスまたはユーザーネームがすでに登録されている場合更新
    if (emailUser.isPresent() || usernameUser.isPresent()) {
      emailUser.ifPresent(user -> newUser.setId(user.getId()));
      usernameUser.ifPresent(user -> newUser.setId(user.getId()));
      return updateNonActivatedUser(newUser);
    }
    // メールアドレスがすでに登録されていない場合新規登録
    return saveUser(newUser);
  }
}
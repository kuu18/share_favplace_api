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

  public Optional<User> findById(Integer id) {
    return userRepository.findById(id);
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
    user.setAvatarUrl("https://" + System.getenv("AWSS3_BUCKET_NAME") + ".s3." +
      System.getenv("AWSS3_REGION") + ".amazonaws.com/default/default_avatar.png");
    User saveUser = userRepository.save(user);
    addRoleToUser(user.getUsername(), "ROLE_USER");
    return saveUser;
  }
  
  /**
   * ユーザーの更新
   * 
   * @param user
   * @return User
   */
  public User updateUser(User user) {
    return userRepository.updateUser(user);
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
   * ユーザーのメールアドレス更新
   * 
   * @param user
   * @return 更新件数
   */
  public int updateEmail(User user) {
    return userRepository.updateEmail(user);
  }

  /**
   * ユーザーのpassword更新
   * 
   * @param user
   * @return 更新件数
   */
  public int updatePassword(User user) {
    return userRepository.updatePassword(user);
  }

  /**
   * ユーザーのactivated更新
   * 
   * @param user
   * @return 更新件数
   */
  public int updateActivated(User user) {
    return userRepository.updateActivated(user);
  }

  /**
   * ユーザーのアバター画像更新
   * 
   * @param user
   * @return
   */
  public int updateAvatarUrl(User user) {
    return userRepository.updateAvatarUrl(user);
  }

  /**
   * ユーザーレコードの削除
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
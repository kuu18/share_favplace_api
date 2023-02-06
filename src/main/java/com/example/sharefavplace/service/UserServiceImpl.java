package com.example.sharefavplace.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sharefavplace.exceptions.ApiAuthException;
import com.example.sharefavplace.exceptions.ApiRequestException;
import com.example.sharefavplace.mapper.ToUserMapper;
import com.example.sharefavplace.model.Role;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.param.UserParam;
import com.example.sharefavplace.repository.RoleRepository;
import com.example.sharefavplace.repository.UserRepository;
import com.example.sharefavplace.utils.JWTUtils;
import com.example.sharefavplace.utils.ResponseUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final S3FileService s3FileService;
  private final PasswordEncoder passwordEncoder;
  private Map<String, Object> responseBody = new HashMap<>();

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
  @Override
  public List<User> findAllUser() {
    return userRepository.findAll();
  }

  /**
   * idによるユーザー取得
   * 
   */
  @Override
  public Optional<User> findById(Integer id) {
    return userRepository.findById(id);
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
    User saveUser = userRepository.save(user);
    return saveUser;
  }

  /**
   * ユーザーレコードの更新
   * 
   * @param user
   * @return User
   */
  @Override
  public User updateUser(User user) {
    return userRepository.updateUser(user);
  }

  /**
   * アクティブ済みでないuserレコードの更新（新規登録）
   * 
   * @param user
   * @return User
   */
  @Override
  public User updateNonActivatedUser(User user) {
    return userRepository.updateNonActivatedUser(user);
  }

  /**
   * ユーザーのメールアドレス更新
   * 
   * @param user
   * @return 更新件数
   */
  @Override
  public int updateEmail(User user) {
    return userRepository.updateEmail(user);
  }

  /**
   * ユーザーのpassword更新
   * 
   * @param user
   * @return 更新件数
   */
  @Override
  public int updatePassword(User user) {
    return userRepository.updatePassword(user);
  }

  /**
   * ユーザーのactivated更新
   * 
   * @param user
   * @return 更新件数
   */
  @Override
  public int updateActivated(User user) {
    return userRepository.updateActivated(user);
  }

  /**
   * ユーザーのアバター画像更新
   * 
   * @param user
   * @return
   */
  @Override
  public int updateAvatarUrl(User user) {
    return userRepository.updateAvatarUrl(user);
  }

  /**
   * ユーザーレコードの削除
   * 
   * @param user
   */
  @Override
  public void deleteUser(User user) {
    userRepository.delete(user);
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
    // パスワードエンコード
    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
    // メールアドレスがすでに登録されていない場合新規登録
    User user = saveUser(newUser);
    // 初期ロール付与
    addRoleToUser(user.getUsername(), "ROLE_USER");
    return user;
  }

  /**
   * ログイン中のユーザー取得
   * 
   * @param accessToken
   * @return ログイン中のユーザー
   */
  public Map<String, Object> getCurrentUser(DecodedJWT decodedJWT) {
    String username = decodedJWT.getSubject();
    responseBody.put("user", findByUsername(username));
    return responseBody;
  }

  /**
   * ユーザー新規登録ロジック
   * 
   * @param userparam
   * @param issure
   */
  public User createUserAndToken(UserParam param) {
    User user = new User();
    user = ToUserMapper.INSTANCE.userParamToUser(param);
    // 初期アバター設定
    user.setAvatarUrl(System.getenv("DEFAULT_AVATAR"));
    // メールアドレスまたはユーザーネームがすでに登録されているか判定し、登録する
    user = checkExistsAndSaveUser(user);
    return user;
  }

  /**
   * ユーザー情報更新ロジック
   * 
   * @param userparam
   * @return 更新ユーザー
   */
  public Map<String, Object> updateUser(UserParam param) {
    User user = new User();
    user = ToUserMapper.INSTANCE.userParamToUser(param);
    // ユーザーの更新
    // レスポンスの生成
    responseBody.put("user", updateUser(user));
    responseBody.put("message", "ユーザー情報を更新しました。");
    return responseBody;
  }

  /**
   * パスワード更新ロジック
   * 
   * @param param
   * @return
   */
  public Map<String, Object> updatePassword(UserParam param) {
    User user = findByEmail(param.getEmail());
    if (!passwordEncoder.matches(param.getPassword(), user.getPassword())) {
      throw new ApiAuthException("パスワードが違います。");
    }
    if (passwordEncoder.matches(param.getNewPassword(), user.getPassword())) {
      throw new ApiRequestException("パスワードが変更されていません。");
    }
    user.setPassword(param.getNewPassword());
    updatePassword(user);
    responseBody.put("message", "パスワードを更新しました。");
    return responseBody;
  }

  /**
   * パスワード再設定ロジック
   * 
   * @param authorizationHeader
   * @param userparam
   * @param issure
   * @return ユーザー
   */
  public Map<String, Object> resetPassword(DecodedJWT decodedJWT, UserParam param, String issure,
      HttpServletResponse response) {
    String username = decodedJWT.getSubject();
    User user = findByUsername(username);
    if (passwordEncoder.matches(param.getPassword(), user.getPassword())) {
      throw new ApiRequestException("パスワードが変更されていません。");
    }
    user.setPassword(param.getPassword());
    updatePassword(user);
    // トークンの生成
    JWTUtils.createAccessToken(user, issure);
    JWTUtils.createRefreshToken(user, issure);
    // レスポンスの生成
    responseBody.put("user", user);
    responseBody.put("access_token_exp", JWTUtils.accessTokenExp);
    responseBody.put("refresh_token_exp", JWTUtils.refreshTokenExp);
    responseBody.put("message", "パスワードを更新しました。");
    // クッキーにトークンを保存
    ResponseUtils.setTokensToCookie(JWTUtils.accessToken, JWTUtils.refreshToken, response);
    // トークン削除
    JWTUtils.deleteToken();
    return responseBody;
  }

  /**
   * アバター画像更新ロジック
   * 
   * @param username
   * @param avatar
   * @return
   */
  public Map<String, Object> updateAvatar(String username, MultipartFile avatar) {
    User user = findByUsername(username);
    // ユーザーの現在のアバター画像キー取得
    String avatarObjectKey = s3FileService.getS3ObjectKeyFromUrl(user.getAvatarUrl());
    // AWSS3へのアバター画像アップロード 
    LocalDateTime createAt = LocalDateTime.now();
    String s3Path = "/avatar";
    String avatarUrl = s3FileService.fileUpload(avatar, createAt, s3Path).toString();
    // ユーザーのアバター画像更新
    user.setAvatarUrl(avatarUrl);
    updateAvatarUrl(user);
    // デフォルト画像でない場合更新前のアバター画像をS3から削除
    if (!avatarObjectKey.startsWith("default")){
      s3FileService.fileDelete(avatarObjectKey);
    }
    responseBody.put("avatar_url", avatarUrl);
    responseBody.put("message", "プロフィール画像を更新しました。");
    return responseBody;
  }

  /**
   * ユーザー削除ロジック
   * 
   * @param param
   * @return
   */
  public Map<String, Object> deleteUserAndAvatar(UserParam param) {
    User user = findById(param.getId()).orElseThrow(() -> new RuntimeException("ユーザーが取得できません"));
    if (!passwordEncoder.matches(param.getPassword(), user.getPassword())) {
      throw new ApiRequestException("パスワードが違います。");
    }
    // ユーザーアバター画像URL取得
    String avatarUrl = user.getAvatarUrl();
    // AWSS3オブジェクトキーの取得
    String avatarObjectKey = s3FileService.getS3ObjectKeyFromUrl(avatarUrl);
    // 現在の画像がデフォルト画像でないならAWSS3からアバター画像削除する
    if (!avatarObjectKey.startsWith("default")){
      s3FileService.fileDelete(avatarObjectKey);
    }
    // AWSS3からユーザーの全てのfavplace画像削除する
    user.getFavplaces().forEach(favplace -> {
      // favplace画像URL取得
      String imageUrl = favplace.getImageUrl();
      // AWSS3オブジェクトキーの取得
      String favplaceObjectKey = s3FileService.getS3ObjectKeyFromUrl(imageUrl);
      // デフォルトのnoimage画像でないならAWSS3からfavplace画像削除する
      if (!favplaceObjectKey.startsWith("default")){
        s3FileService.fileDelete(favplaceObjectKey);
      }
    });
    // userを削除する
    deleteUser(user);
    responseBody.put("message", "アカウントを削除しました。");
    return responseBody;
  }
}

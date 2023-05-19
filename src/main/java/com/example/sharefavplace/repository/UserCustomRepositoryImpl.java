package com.example.sharefavplace.repository;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import com.example.sharefavplace.model.AbstractEntity_;
import com.example.sharefavplace.model.User;
import com.example.sharefavplace.model.User_;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

  private final EntityManager entityManager;
  private final PasswordEncoder passwordEncoder;

  /**
   * ユーザーの更新
   * 
   * @param user
   * @return User
   */
  @Override
  public User updateUser(User user) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(User.class);
    Root<User> root = criteriaUpdate.from(User.class);
    criteriaUpdate.set(root.get(User_.username), user.getUsername())
      .where(root.get(User_.id).in(user.getId()));
    Query query = this.entityManager.createQuery(criteriaUpdate);
    int count = query.executeUpdate();
    if(count == 1) {
      CriteriaQuery<User> qriteriaQuery = criteriaBuilder.createQuery(User.class);
      root = qriteriaQuery.from(User.class);
      qriteriaQuery.select(root)
            .where(root.get(User_.id).in(user.getId()));
      TypedQuery<User> typedQuery = this.entityManager.createQuery(qriteriaQuery);
      return typedQuery.getSingleResult();
    }
    throw new RuntimeException("更新に失敗しました。");
  }

  /**
   * アクティブ済みでないユーザーの更新（新規登録）
   * 
   * @param user
   * @return User
   */
  @Override
  public User updateNonActivatedUser(User user) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(User.class);
    Root<User> root = criteriaUpdate.from(User.class);
    criteriaUpdate.set(root.get(User_.username), user.getUsername())
          .set(root.get(User_.email), user.getEmail())
          .set(root.get(User_.password), passwordEncoder.encode(user.getPassword()))
          .set(root.get(AbstractEntity_.createdAt), new Date())
          .set(root.get(AbstractEntity_.updatedAt), new Date())
          .where(root.get(User_.id).in(user.getId()));
    Query query = this.entityManager.createQuery(criteriaUpdate);
    int count = query.executeUpdate();
    if(count == 1) {
      CriteriaQuery<User> qriteriaQuery = criteriaBuilder.createQuery(User.class);
      root = qriteriaQuery.from(User.class);
      qriteriaQuery.select(root)
            .where(root.get(User_.id).in(user.getId()));
      TypedQuery<User> typedQuery = this.entityManager.createQuery(qriteriaQuery);
      return typedQuery.getSingleResult();
    }
    throw new RuntimeException("更新に失敗しました。");
  }

  /**
   * activatedの更新
   * 
   * @param user
   * @return 更新件数
   */
  @Override
  public int updateActivated(User user) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(User.class);
    Root<User> root = criteriaUpdate.from(User.class);
    criteriaUpdate.set(root.get(User_.activated), !user.getActivated())
      .where(root.get(User_.id).in(user.getId()));
    Query query = this.entityManager.createQuery(criteriaUpdate);
    return query.executeUpdate();
  }

  /**
   * メールアドレスの更新
   * 
   * @param user
   * @return 更新件数
   */
  @Override
  public int updateEmail(User user) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(User.class);
    Root<User> root = criteriaUpdate.from(User.class);
    criteriaUpdate.set(root.get(User_.email), user.getEmail())
      .where(root.get(User_.id).in(user.getId()));
    Query query = this.entityManager.createQuery(criteriaUpdate);
    return query.executeUpdate();
  }

  /**
   * passwordの更新
   * 
   * @param user
   * @return 更新件数
   * 
   */
  @Override
  public int updatePassword(User user) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(User.class);
    Root<User> root = criteriaUpdate.from(User.class);
    criteriaUpdate.set(root.get(User_.password), passwordEncoder.encode(user.getPassword()))
      .where(root.get(User_.id).in(user.getId()));
    Query query = this.entityManager.createQuery(criteriaUpdate);
    return query.executeUpdate();
  }

  /**
   * アバターの更新
   * @param user
   * @return 更新件数
   */
  @Override
  public int updateAvatarUrl(User user) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(User.class);
    Root<User> root = criteriaUpdate.from(User.class);
    criteriaUpdate.set(root.get(User_.avatarUrl), user.getAvatarUrl())
      .where(root.get(User_.id).in(user.getId()));
    Query query = this.entityManager.createQuery(criteriaUpdate);
    return query.executeUpdate();
  }
  
}

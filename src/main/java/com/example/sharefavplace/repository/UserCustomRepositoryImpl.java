package com.example.sharefavplace.repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import com.example.sharefavplace.model.User;
import com.example.sharefavplace.model.User_;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
public class UserCustomRepositoryImpl implements UserCustomRepository {

  private final EntityManager entityManager;
  private final PasswordEncoder passwordEncoder;

  /**
   * アクティブ済みでないユーザーの更新（新規登録）
   * 
   * @param user
   * @return 更新済みUser
   */
  @Override
  public User updateNonActivatedUser(User user) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<User> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(User.class);
    Root<User> root = criteriaUpdate.from(User.class);
    criteriaUpdate.set(root.get(User_.username), user.getUsername())
          .set(root.get(User_.email), user.getEmail())
          .set(root.get(User_.password), passwordEncoder.encode(user.getPassword()))
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
  
}

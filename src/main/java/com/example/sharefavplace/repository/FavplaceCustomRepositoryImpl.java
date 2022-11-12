package com.example.sharefavplace.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import com.example.sharefavplace.model.Favplace;
import com.example.sharefavplace.model.Favplace_;
import com.example.sharefavplace.model.User_;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FavplaceCustomRepositoryImpl implements FavplaceCustomRepository {

  private final EntityManager entityManager;

  /**
   * user_idによるFavplace取得（ページネーション）
   * 
   * @param userId
   * @return List<Favplace>
   */
  @Override
  public List<Favplace> selectFavplacesbyUserId(Integer userId, final int pPageIndex, final int pCountPerPage) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Favplace> qriteriaQuery = criteriaBuilder.createQuery(Favplace.class);
    Root<Favplace> root = qriteriaQuery.from(Favplace.class);
    qriteriaQuery.select(root)
        .where(criteriaBuilder.equal(root.get(Favplace_.user).get(User_.id), userId))
        .orderBy(criteriaBuilder.asc(root.get(Favplace_.id)))
        .distinct(true);
    root.fetch(Favplace_.user, JoinType.INNER);
    root.fetch(Favplace_.category, JoinType.LEFT);
    root.fetch(Favplace_.schedule, JoinType.LEFT);
    TypedQuery<Favplace> typedQuery = this.entityManager.createQuery(qriteriaQuery);
    return typedQuery.setFirstResult(pPageIndex * pCountPerPage)
        .setMaxResults(pCountPerPage)
        .getResultList();
  }

  /**
   * user_idによるFavplace数取得
   * 
   * @param userId
   * @return Favplace数
   */
  @Override
  public long getUsersFavplacesCount(Integer userId) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> qriteriaQuery = criteriaBuilder.createQuery(Long.class);
    Root<Favplace> root = qriteriaQuery.from(Favplace.class);
    qriteriaQuery.select(criteriaBuilder.count(root))
        .where(criteriaBuilder.equal(root.get(Favplace_.user).get(User_.id), userId));
    TypedQuery<Long> typedQuery = this.entityManager.createQuery(qriteriaQuery);
    return typedQuery.getSingleResult();
  }

  /**
   * user_idによる予定済みのFavplace一覧取得
   * 
   * @param userId
   * @return List<Favplace>
   */
  @Override
  public List<Favplace> selectScheduledFavplacesbyUserId(Integer userId) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Favplace> qriteriaQuery = criteriaBuilder.createQuery(Favplace.class);
    Root<Favplace> root = qriteriaQuery.from(Favplace.class);
    qriteriaQuery.select(root)
      .where(criteriaBuilder.equal(root.get(Favplace_.user).get(User_.id), userId))
      .orderBy(criteriaBuilder.asc(root.get(Favplace_.id)))
      .distinct(true);
    root.fetch(Favplace_.user, JoinType.INNER);
    root.fetch(Favplace_.category, JoinType.LEFT);
    root.fetch(Favplace_.schedule, JoinType.INNER);
    TypedQuery<Favplace> typedQuery = this.entityManager.createQuery(qriteriaQuery);
    return typedQuery.getResultList();
  }

}

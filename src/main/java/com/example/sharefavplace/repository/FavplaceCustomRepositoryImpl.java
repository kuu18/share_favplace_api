package com.example.sharefavplace.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
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
   * idによるFavplace取得（1件）
   * 
   * @param userId
   * @return Favplace
   */
  @Override
  public Favplace selectFavplacesbyId(Integer id) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Favplace> qriteriaQuery = criteriaBuilder.createQuery(Favplace.class);
    Root<Favplace> root = qriteriaQuery.from(Favplace.class);
    qriteriaQuery.select(root)
      .where(criteriaBuilder.equal(root.get(Favplace_.id), id));
    root.fetch(Favplace_.user, JoinType.INNER);
    root.fetch(Favplace_.category, JoinType.LEFT);
    root.fetch(Favplace_.schedule, JoinType.LEFT);
    TypedQuery<Favplace> typedQuery = this.entityManager.createQuery(qriteriaQuery);
    return typedQuery.getSingleResult();
  }

  /**
   * Favplaceのスケジュール更新
   * 
   * @param favplace
   * @return Favplace
   */
  @Override
  public Favplace updateSchedule(Favplace favplace) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Favplace> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Favplace.class);
    Root<Favplace> root = criteriaUpdate.from(Favplace.class);
    criteriaUpdate.set(root.get(Favplace_.schedule), favplace.getSchedule())
      .where(root.get(Favplace_.id).in(favplace.getId()));
    Query query = this.entityManager.createQuery(criteriaUpdate);
    int count = query.executeUpdate();
    if(count == 1) {
      CriteriaQuery<Favplace> qriteriaQuery = criteriaBuilder.createQuery(Favplace.class);
      root = qriteriaQuery.from(Favplace.class);
      qriteriaQuery.select(root)
            .where(root.get(Favplace_.id).in(favplace.getId()));
      TypedQuery<Favplace> typedQuery = this.entityManager.createQuery(qriteriaQuery);
      return typedQuery.getSingleResult();
    }
    throw new RuntimeException("更新に失敗しました。");
  }

}

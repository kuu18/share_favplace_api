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
public class FavplaceCustomRepositoryImpl implements FavplaceCustomRepository{

  private final EntityManager entityManager;

  /**
   * idによるFavplace取得
   * 
   * @param id
   * @return Favplace
   */
  @Override
  public Favplace selectFavplacebyId(Integer id) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Favplace> qriteriaQuery = criteriaBuilder.createQuery(Favplace.class);
    Root<Favplace> root = qriteriaQuery.from(Favplace.class);
    qriteriaQuery.select(root)
          .where(criteriaBuilder.equal(root.get(Favplace_.id), id)).distinct(true);;
    root.fetch(Favplace_.user, JoinType.INNER);
    root.fetch(Favplace_.categories, JoinType.INNER);
    TypedQuery<Favplace> typedQuery = this.entityManager.createQuery(qriteriaQuery);
    return typedQuery.getSingleResult();
  }

  /**
   * user_idによるFavplace全件取得
   * 
   * @param userId
   * @return List<Favplace>
   */
  @Override
  public List<Favplace> selectAllFavplacesbyUserId(Integer userId) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Favplace> qriteriaQuery = criteriaBuilder.createQuery(Favplace.class);
    Root<Favplace> root = qriteriaQuery.from(Favplace.class);
    qriteriaQuery.select(root)
          .where(criteriaBuilder.equal(root.get(Favplace_.user).get(User_.id), userId)).distinct(true);;
    root.fetch(Favplace_.user, JoinType.INNER);
    root.fetch(Favplace_.categories, JoinType.INNER);
    TypedQuery<Favplace> typedQuery = this.entityManager.createQuery(qriteriaQuery);
    return typedQuery.getResultList();
  }
  
}

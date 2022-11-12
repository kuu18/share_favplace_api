package com.example.sharefavplace.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import com.example.sharefavplace.model.Favplace_;
import com.example.sharefavplace.model.Schedule;
import com.example.sharefavplace.model.Schedule_;
import com.example.sharefavplace.model.User_;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScheduleCustomRepositoryImpl implements ScheduleCustomRepository {

  private final EntityManager entityManager;

  /**
   * user_idによるスケジュール一覧取得
   * 
   * @param userId
   * @return List<Schedule>
   */
  @Override
  public List<Schedule> selectSchedulesByUserId(Integer userId) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Schedule> qriteriaQuery = criteriaBuilder.createQuery(Schedule.class);
    Root<Schedule> root = qriteriaQuery.from(Schedule.class);
    qriteriaQuery.select(root)
        .where(criteriaBuilder.equal(root.get(Schedule_.user).get(User_.id), userId))
        .distinct(true);
    root.fetch(Schedule_.user, JoinType.INNER);
    root.fetch(Schedule_.favplace, JoinType.INNER).fetch(Favplace_.category, JoinType.LEFT);
    TypedQuery<Schedule> typedQuery = this.entityManager.createQuery(qriteriaQuery);
    return typedQuery.getResultList();
  }

}

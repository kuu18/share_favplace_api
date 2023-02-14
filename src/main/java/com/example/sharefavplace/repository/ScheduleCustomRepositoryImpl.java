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

  /**
   * Schedule更新
   * 
   * @param schedule
   * @return Schedule
   */
  @Override
  public Schedule updateSchedule(Schedule schedule) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Schedule> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Schedule.class);
    Root<Schedule> root = criteriaUpdate.from(Schedule.class);
    criteriaUpdate.set(root.get(Schedule_.start), schedule.getStartWithDateType())
      .set(root.get(Schedule_.end), schedule.getEndWithDateType())
      .set(root.get(Schedule_.timed), schedule.getTimed())
        .where(root.get(Schedule_.id).in(schedule.getId()));
    Query query = this.entityManager.createQuery(criteriaUpdate);
    int count = query.executeUpdate();
    if (count == 1) {
      CriteriaQuery<Schedule> qriteriaQuery = criteriaBuilder.createQuery(Schedule.class);
      root = qriteriaQuery.from(Schedule.class);
      qriteriaQuery.select(root)
          .where(root.get(Schedule_.id).in(schedule.getId()));
      TypedQuery<Schedule> typedQuery = this.entityManager.createQuery(qriteriaQuery);
      return typedQuery.getSingleResult();
    }
    throw new RuntimeException("更新に失敗しました。");
  }

}

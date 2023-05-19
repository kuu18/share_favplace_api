package com.example.sharefavplace.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.sharefavplace.model.Schedule;

@Repository
public interface ScheduleCustomRepository {
  
  /**
   * usre_idによるスケジュール一覧取得
   * 
   * @param userId
   * @return responseBody
   */
  public List<Schedule> selectSchedulesByUserId(Integer userId);

  /**
   * Schedule更新
   * 
   * @param schedule
   * @return Schedule
   */
  public Schedule updateSchedule(Schedule schedule);

}

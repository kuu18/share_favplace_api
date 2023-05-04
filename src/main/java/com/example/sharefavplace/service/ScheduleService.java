package com.example.sharefavplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sharefavplace.model.Schedule;

@Service
public interface ScheduleService {

  /**
   * ユーザーのスケジュール一覧取得
   * 
   * @param userId
   * @return responseBody
   */
  public List<Schedule> getSchedulesByUserId(Integer userId);

  /**
   * スケジュール新規登録
   * 
   * @param schedule
   * @return Schdule
   */
  public Schedule saveSchedule(Schedule schedule);

  /**
   * スケジュール更新
   * 
   * @param schedule
   * @return Schdule
   */
  public Schedule updateSchedule(Schedule schedule);

  /**
   * スケジュール削除
   * 
   * @param favplace
   */
  public void deleteSchedule(Schedule schedule);

}


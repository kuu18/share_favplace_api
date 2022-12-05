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
   * @return
   */
  public Schedule saveSchedule(Schedule schedule);

}


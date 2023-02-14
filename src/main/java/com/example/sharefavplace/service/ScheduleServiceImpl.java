package com.example.sharefavplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sharefavplace.model.Schedule;
import com.example.sharefavplace.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

  private final ScheduleRepository scheduleRepository;

  /**
   * ユーザーのスケジュール一覧取得
   * 
   * @param userId
   * @return responseBody
   */
  @Override
  public List<Schedule> getSchedulesByUserId(Integer userId) {
    return scheduleRepository.selectSchedulesByUserId(userId);
  }

  /**
   * スケジュール新規登録
   * 
   * @param schedule
   * @return
   */
  @Override
  public Schedule saveSchedule(Schedule schedule) {
    return scheduleRepository.save(schedule);
  }

  @Override
  public Schedule updateSchedule(Schedule schedule) {
    return scheduleRepository.updateSchedule(schedule);
  }

}

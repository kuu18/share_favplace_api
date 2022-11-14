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

  @Override
  public List<Schedule> getSchedulesByUserId(Integer userId) {
    return scheduleRepository.selectSchedulesByUserId(userId);
  }

}

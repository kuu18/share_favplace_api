package com.example.sharefavplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sharefavplace.model.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer>, ScheduleCustomRepository {
}

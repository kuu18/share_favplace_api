package com.example.sharefavplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sharefavplace.model.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer>, ScheduleCustomRepository {
}

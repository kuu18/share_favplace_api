package com.example.sharefavplace.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sharefavplace.model.Schedule;
import com.example.sharefavplace.service.ScheduleServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleResource {

  private final ScheduleServiceImpl sucheduleService;
  
  /**
   * ユーザーのFavplace取得
   * @param id
   * @param pageIndex
   * @return ユーザーのFavplace一覧（ページネーション）
   */
  @GetMapping("/user/{id}")
  public ResponseEntity<List<Schedule>> getSchedulesByUserId(@PathVariable Integer id) {
    return ResponseEntity.ok().body(sucheduleService.getSchedulesByUserId(id));
  }

}

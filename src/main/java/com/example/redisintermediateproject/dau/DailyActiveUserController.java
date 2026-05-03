package com.example.redisintermediateproject.dau;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dau")
@RequiredArgsConstructor
public class DailyActiveUserController {

  private final DailyActiveUserService dailyActiveUserService;

  // 활동 유저 기록 API
  @PostMapping("/record")
  public void recordActiveUser(@RequestBody RecordActiveUserRequestDto recordActiveUserRequestDto) {
    dailyActiveUserService.recordActiveUser(recordActiveUserRequestDto.getUserId());
  }

  // DAU 조회 API
  @GetMapping("/count")
  public long getDau(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return dailyActiveUserService.getDau(date);
  }

  @PostMapping("/record/redis")
  public void recordActiveUserWithRedis(
      @RequestBody RecordActiveUserRequestDto recordActiveUserRequestDto) {
    dailyActiveUserService.recordActiveUserWithRedis(recordActiveUserRequestDto.getUserId());
  }

  @GetMapping("/count/redis")
  public long getDauWithRedis(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return dailyActiveUserService.getDauWithRedis(date);
  }
}

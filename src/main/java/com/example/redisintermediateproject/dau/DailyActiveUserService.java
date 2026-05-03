package com.example.redisintermediateproject.dau;

import java.time.LocalDate;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyActiveUserService {

  private final DailyActiveUserRepository dailyActiveUserRepository;
  private final RedisTemplate<String, String> redisTemplate;

  @Transactional
  public void recordActiveUser(Long userId) {
    LocalDate today = LocalDate.now();
    // 이미 활동 기록이 있는 유저라면 저장하지 않는다.
    if (!dailyActiveUserRepository.existsByUserIdAndActiveDate(userId, today)) {
      // 활동 기록 저장
      dailyActiveUserRepository.save(new DailyActiveUser(userId, today));
    }
  }

  @Transactional(readOnly = true)
  public long getDau(LocalDate date) {
    // 특정 날짜의 DAU를 SQL문의 COUNT를 활용해 계산
    return dailyActiveUserRepository.countByActiveDate(date);
  }

  public void recordActiveUserWithRedis(Long userId) {
    LocalDate today = LocalDate.now();
    String key = "dau:" + today.toString(); // dau:2024-06-01

    // Redis의 Set 자료구조를 활용하여 중복 없이 사용자 ID 저장
    // SADD [key] [value]
    redisTemplate.opsForSet().add(key, userId.toString());

    // DAU 데이터는 하루 동안만 유효하므로, 키의 유효 기간을 1일로 설정
    redisTemplate.expire(key, 1, java.util.concurrent.TimeUnit.DAYS);
  }

  public long getDauWithRedis(LocalDate date) {
    String key = "dau:" + date.toString();

    // Redis의 Set 자료구조의 크기를 반환하여 DAU 계산
    // SCARD [key]
    Long dau = redisTemplate.opsForSet().size(key);
    return dau != null ? dau : 0;
  }
}

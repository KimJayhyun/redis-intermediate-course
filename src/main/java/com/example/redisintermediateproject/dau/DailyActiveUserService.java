package com.example.redisintermediateproject.dau;

import java.time.LocalDate;

import org.springframework.data.redis.core.RedisCallback;
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

    // SETBIT [key] [offset] [value]
    redisTemplate.opsForValue().setBit(key, userId, true);

    // DAU 데이터는 하루 동안만 유효하므로, 키의 유효 기간을 1일로 설정
    redisTemplate.expire(key, 1, java.util.concurrent.TimeUnit.DAYS);
  }

  public long getDauWithRedis(LocalDate date) {
    String key = "dau:" + date.toString();

    // Redis에 저장된 비트맵을 활용하여 DAU를 계산
    // Redis 명령어의 'BITCOUNT [key]'와 동일하다.
    return redisTemplate
        .execute((RedisCallback<Long>) (connection) -> connection.bitCount(key.getBytes()));
  }
}

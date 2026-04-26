package com.example.redisintermediateproject.keyword;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void search(String keyword) {
        SearchKeyword searchKeyword =
                searchRepository.findByKeyword(keyword).orElse(new SearchKeyword(keyword));
        searchKeyword.increaseCount();
        searchRepository.save(searchKeyword);
    }

    @Transactional(readOnly = true)
    public List<String> getTop10Keywords() {
        return searchRepository.findTop10ByOrderByCountDesc().stream()
                .map(SearchKeyword::getKeyword).toList();
    }

    public void seatchWithRedis(String keyword) {
        // ZINCRBY [search_keyword_ranking] 1 [keyword]
        redisTemplate.opsForZSet().incrementScore("search_keyword_ranking", keyword, 1.0);
    }

    public List<String> getTop10KeywordsWithRedis() {
        // ZREVRANGE [search_keyword_ranking] 0 9
        // ZRANGE [search_keyword_ranking] 0 9 REV
        return redisTemplate.opsForZSet().reverseRange("search_keyword_ranking", 0, 9).stream()
                .toList();
    }
}

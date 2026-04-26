package com.example.redisintermediateproject.keyword;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping()
    public void search(@RequestParam String keyword) {
        searchService.search(keyword);
    }

    @GetMapping("/top10")
    public List<String> getTop10Keywords() {
        return searchService.getTop10Keywords();
    }

    @GetMapping("/top10/redis")
    public List<String> getTop10KeywordsWithRedis() {
        return searchService.getTop10KeywordsWithRedis();
    }
}

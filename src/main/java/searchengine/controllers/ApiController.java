package searchengine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexingService;
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final SearchService searchService;
    private static final int DEFAULT_LIMIT = 20;


    public ApiController(StatisticsService statisticsService, IndexingService indexingService, SearchService searchService) {
        this.statisticsService = statisticsService;
        this.indexingService = indexingService;
        this.searchService = searchService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<?> startIndexing() {
        HashMap<String, Object> result = new HashMap<>();
        String error = indexingService.execute();
        if (error == null || error.isEmpty()) {
            result.put("result", true);
        } else {
            result.put("result", false);
            result.put("error", error);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<?> stopIndexing() {
        HashMap<String, Object> result = new HashMap<>();
        String error = indexingService.stop();
        if (error == null || error.isEmpty()) {
            result.put("result", true);
        } else {
            result.put("result", false);
            result.put("error", "Индексация не запущена");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/indexPage")
    public ResponseEntity<?> indexPage(@RequestParam("url") String url) {
        HashMap<String, Object> result = new HashMap<>();
        String error;
        if (url == null || url.isEmpty()) {
            error = "Задан пустой поисковый запрос";
        } else {
            error = indexingService.indexPage(url);
        }
        if (error == null || error.isEmpty()) {
            result.put("result", true);
        } else {
            result.put("result", false);
            result.put("error", error);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("query") String query, @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit,
                                    @RequestParam("site") Optional<String> site) {
        if (offset == null) {
            offset = 0;
        }
        if (limit == null) {
            limit = DEFAULT_LIMIT;
        }
        SearchResponse result = new SearchResponse();
        if (query == null || query.isEmpty()) {
            result.setError("Задан пустой поисковый запрос");
        } else {
            result = searchService.doSearch(query, offset, limit, site.orElse(""));
        }
        if (result.isResult()) {
            return ResponseEntity.ok(result);
        } else {
            HashMap<String, Object> errorObj = new HashMap<>();
            errorObj.put("result", false);
            errorObj.put("error", result.getError());
            return new ResponseEntity<>(errorObj, HttpStatus.OK);
        }
    }

}

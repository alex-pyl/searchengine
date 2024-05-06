package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.search.Relevance;
import searchengine.dto.search.SearchBuffer;
import searchengine.dto.search.SearchData;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.site.IndexDto;
import searchengine.dto.site.LemmaDto;
import searchengine.dto.site.PageDto;
import searchengine.dto.site.SiteDto;
import searchengine.utils.Utils;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SiteService siteService;
    private final LemmaService lemmaService;
    private final IndexService indexService;
    private static final int MAX_RELEVANCE = 100;

    public SearchResponse doSearch(String query, int offset, int limit, String url) {
        SearchResponse response = new SearchResponse();
        response.setResult(false);
        if (query.isEmpty()) {
            return response;
        }
        SiteDto siteDto;
        if (!url.isEmpty()) {
            siteDto = siteService.findByUrl(url);
        } else {
            siteDto = null;
        }
        List<SearchBuffer> bufferList = new ArrayList<>();
        Map<String, Long> lemmaMap;
        try {
            lemmaMap = Utils.doLucene(query);
        } catch (IOException e) {
            response.setError("Ошибка при лемматизации: " + e.getMessage());
            return response;
        }
        try {
            lemmaMap.forEach((lemma, rank) -> {
                List<LemmaDto> lemmaList = lemmaService.findByLemma(siteDto, lemma);
                int count = 0;
                int frequency = 0;
                for (LemmaDto lemmaObj : lemmaList) {
                    count += indexService.countByLemma(lemmaObj);
                    frequency += lemmaObj.getFrequency();
                }
                if (count > 0 && count <= MAX_RELEVANCE) {
                    bufferList.add(new SearchBuffer(lemma, frequency, lemmaList));
                }
            });
        } catch (Exception e) {
            response.setError("Ошибка при буферизации: " + e.getMessage());
            return response;
        }
        if (bufferList.isEmpty()) {
            return response;
        }
        bufferList.sort(Comparator.comparing(SearchBuffer::getFrequency));
        Map<PageDto, Relevance> pages = new HashMap<>();
        Map<PageDto, Relevance> newPages = new HashMap<>();
        boolean firstLemma = true;
        try {
            for (SearchBuffer buffer : bufferList) {
                for (LemmaDto lemmaObj : buffer.getLemmaList()) {
                    for (IndexDto indexDto : indexService.findAllByLemma(lemmaObj)) {
                        Relevance relevance = new Relevance();
                        List<String> lemmas = new ArrayList<>();
                        if (!firstLemma) {
                            relevance = pages.get(indexDto.getPage());
                        }
                        if (firstLemma || relevance != null) {
                            if (!firstLemma) {
                                lemmas = relevance.getLemmas();
                            }
                            lemmas.add(lemmaObj.getLemma());
                            relevance.setLemmas(lemmas);
                            relevance.setPageDto(indexDto.getPage());
                            relevance.setRelevance(relevance.getRelevance() + indexDto.getRank());
                            newPages.put(indexDto.getPage(), relevance);
                        }
                    }
                }
                firstLemma = false;
                pages = newPages;
                if (pages.isEmpty()) {
                    return response;
                }
            }
        } catch (Exception e) {
            response.setError("Ошибка при поиске страниц: " + e.getMessage());
            return response;
        }
        try {
            List<Relevance> relevanceList = new ArrayList<>(pages.values());
            relevanceList.sort(Comparator.comparing(Relevance::getRelevance).reversed());
            response.setCount(relevanceList.size());
            float maxRelevance = relevanceList.get(0).getRelevance();
            List<SearchData> searchData = new ArrayList<>();
            for (int i = offset; i < Math.min(limit, relevanceList.size()); i++) {
                Relevance relevance = relevanceList.get(i);
                SearchData data = new SearchData();
                data.setSite(relevance.getPageDto().getSiteId().getUrl());
                data.setSiteName(relevance.getPageDto().getSiteId().getName());
                data.setUri(relevance.getPageDto().getPath());
                data.setTitle(Utils.getTitle(relevance.getPageDto().getContent()));
                data.setSnippet(Utils.getSnippet(relevance.getPageDto().getContent(), bufferList.stream().map(SearchBuffer::getLemma).toList()));
                data.setRelevance(relevance.getRelevance() / maxRelevance);
                searchData.add(data);
            }
            response.setData(searchData);
            response.setResult(true);
        } catch (Exception e) {
            response.setError("Ошибка при формировании результата: " + e.getMessage());
            return response;
        }
        return response;
    }

}

package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.site.*;
import searchengine.repositories.LemmaRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LemmaService {
    private final LemmaRepository repository;
    private final LemmaMapper mapper;
    private final SiteMapper siteMapper;

    static Map<String, Object> locks = new ConcurrentHashMap<>();

    public LemmaDto create(SiteDto siteDto, String lemma) {
        LemmaDto lemmaDto;
        synchronized (locks.computeIfAbsent(siteDto.getId().toString() + lemma, k -> new Object())) {
            lemmaDto = findBySiteIdAndLemma(siteDto, lemma);
            if (lemmaDto == null) {
                lemmaDto = new LemmaDto(null, siteDto, lemma, 1);
            } else {
                lemmaDto.setFrequency(lemmaDto.getFrequency() + 1);
            }
            lemmaDto = mapper.convertToDto(repository.save(mapper.convertToEntity(lemmaDto)));
        }
        return lemmaDto;
    }

    public synchronized void deleteIndexLemma(List<IndexDto> indexDtoList) {
        for (IndexDto indexDto : indexDtoList) {
            LemmaDto lemmaDto = indexDto.getLemma();
            Integer frequency = lemmaDto.getFrequency();
            if (frequency == 1) {
                repository.delete(mapper.convertToEntity(lemmaDto));
            } else {
                lemmaDto.setFrequency(frequency - 1);
                repository.save(mapper.convertToEntity(lemmaDto));
            }
        }
    }

    public LemmaDto findBySiteIdAndLemma(SiteDto siteDto, String lemma) {
        return mapper.convertToDto(repository.findBySiteIdAndLemma(siteMapper.convertToEntity(siteDto), lemma));
    }

    public int countBySiteId(SiteDto siteDto) {
        return repository.countBySiteId(siteMapper.convertToEntity(siteDto));
    }

    public List<LemmaDto> findByLemma(SiteDto siteDto, String lemma) {
        if (siteDto == null) {
            return repository.findByLemma(lemma).stream().map(mapper::convertToDto).toList();
        } else {
            return List.of(findBySiteIdAndLemma(siteDto, lemma));
        }
    }
}
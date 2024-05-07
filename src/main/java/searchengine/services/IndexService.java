package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.site.*;
import searchengine.repositories.IndexRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexService {
    private final IndexRepository repository;
    private final IndexMapper mapper;
    private final PageMapper pageMapper;
    private final LemmaMapper lemmaMapper;

    List<IndexDto> findAllByPage(PageDto pageDto) {
        return repository.findAllByPage(pageMapper.convertToEntity(pageDto)).stream().map(mapper::convertToDto).toList();
    }

    public void create(PageDto pageDto, LemmaDto lemmaDto, Long rank) {
        IndexDto indexDto = new IndexDto(null, pageDto, lemmaDto, rank);
        repository.save(mapper.convertToEntity(indexDto));
    }

    public int countByLemma(LemmaDto lemma) {
        return repository.countByLemma(lemmaMapper.convertToEntity(lemma));
    }

    List<IndexDto> findAllByLemma(LemmaDto lemma) {
        return repository.findAllByLemma(lemmaMapper.convertToEntity(lemma)).stream().map(mapper::convertToDto).toList();
    }

}

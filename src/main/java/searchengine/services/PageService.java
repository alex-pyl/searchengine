package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.site.PageDto;
import searchengine.dto.site.PageMapper;
import searchengine.dto.site.SiteDto;
import searchengine.dto.site.SiteMapper;
import searchengine.model.Page;
import searchengine.repositories.PageRepository;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository repository;
    private final PageMapper mapper;
    private final IndexService indexService;
    private final LemmaService lemmaService;
    private final SiteMapper siteMapper;

    public PageDto findByPath(String path) {
        return mapper.convertToDto(repository.findByPath(path));
    }

    public PageDto create(SiteDto site, String path, int code, String content) {
        PageDto pageDto;
        Page page;
        try {
            pageDto = new PageDto(null, site, path.replaceAll("/+$", ""), code, content);
            page = repository.save(mapper.convertToEntity(pageDto));
        } catch (Exception e) {
            if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                return null;
            }
            throw e;
        }
        return mapper.convertToDto(page);
    }

    public void updateResponse(PageDto pageDto, int code, String content) {
        pageDto.setCode(code);
        pageDto.setContent(content);
        repository.save(mapper.convertToEntity(pageDto));
    }

    public void delete(String url) {
        PageDto pageDto = findByPath(url);
        if (pageDto != null) {
            var indexes = indexService.findAllByPage(pageDto);
            lemmaService.deleteIndexLemma(indexes);
            repository.delete(mapper.convertToEntity(pageDto));
        }
    }

    public int countBySiteId(SiteDto siteDto) {
        return repository.countBySiteId(siteMapper.convertToEntity(siteDto));
    }
}

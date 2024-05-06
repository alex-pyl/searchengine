package searchengine.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.dto.site.SiteDto;
import searchengine.dto.site.SiteMapper;
import searchengine.model.SiteModel;
import searchengine.model.SiteStatus;
import searchengine.repositories.SiteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SiteService {
    private final SiteRepository repository;
    private final SiteMapper mapper;
    private static final String SITE_ROOT = "(https?:\\/\\/)(www\\.)?([^\\/]+)";

    public void delete(Site site) {
        SiteModel model = repository.findByUrl(site.getUrl());
        if (model != null) {
            repository.delete(model);
        }
    }

    public SiteDto create(Site site, SiteStatus status) {
        SiteDto siteDto = new SiteDto(null, status, LocalDateTime.now(), null, site.getUrl(), site.getName(), "");
        siteDto = mapper.convertToDto(repository.save(mapper.convertToEntity(siteDto)));
        siteDto.setTemplate(calcTemplate(site.getUrl()));
        return siteDto;
    }

    public void updateStatus(SiteDto siteDto, SiteStatus status) {
        siteDto.setStatus(status);
        siteDto.setStatusTime(LocalDateTime.now());
        repository.save(mapper.convertToEntity(siteDto));
    }

    public void putError(SiteDto siteDto, String error) {
        siteDto.setStatus(SiteStatus.FAILED);
        siteDto.setLastError(error);
        siteDto.setStatusTime(LocalDateTime.now());
        repository.save(mapper.convertToEntity(siteDto));
    }

    private String calcTemplate(String url) {
        Pattern pattern = Pattern.compile(SITE_ROOT);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(3);
        }
        return "";
    }

    public SiteDto findByUrl(String url) {
        return mapper.convertToDto(repository.findByUrl(url));
    }
}

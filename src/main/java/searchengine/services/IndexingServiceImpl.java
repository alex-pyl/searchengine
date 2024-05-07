package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.ConnectConfig;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.site.LemmaDto;
import searchengine.dto.site.PageDto;
import searchengine.dto.site.SiteDto;
import searchengine.indexing.RunIndexing;
import searchengine.utils.Utils;
import searchengine.utils.WebResponse;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    private final SitesList sites;
    private final SiteService siteService;
    private final ConnectConfig config;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final IndexService indexService;
    private static ExecutorService executor;
    private static boolean shutdown;

    public static void setShutdown(boolean shutdown) {
        IndexingServiceImpl.shutdown = shutdown;
    }


    @Override
    public String execute() {
        try {
            if (executor != null && !executor.isTerminated()) {
                return "Индексация уже запущена";
            }
            setShutdown(false);
            executor = Executors.newFixedThreadPool(sites.getSites().size());
            sites.getSites().forEach(site -> executor.execute(new RunIndexing(site, config, siteService, pageService, this)));
            executor.shutdown();
        } catch (Exception e) {
            return "Ошибка при распараллеливании индексации: " + e.getMessage();
        }
        return "";
    }

    @Override
    public String stop() {
        if (executor == null || executor.isTerminated()) {
            return "Индексация не запущена";
        }
        setShutdown(true);
        try {
            executor.shutdownNow();
        } catch (Exception e) {
            return "Ошибка при остановке параллельной индексации: " + e.getMessage();
        }
        return "";
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public String indexPage(String url) {
        var seekSite = sites.getSites().stream().filter(s -> url.contains(s.getUrl().replaceAll("^(https?://)?(www\\.)?", ""))).findAny();
        if (seekSite.isEmpty()) {
            return "Данная страница находится за пределами сайтов, указанных в конфигурационном файле";
        }
        Site site = seekSite.get();
        SiteDto siteDto = siteService.findByUrl(site.getUrl());
        WebResponse response = Utils.connect(config, url);
        if (response.getError() != null && !response.getError().isEmpty()) {
            return "Ошибка подключения по адресу " + url + ": " + response.getError();
        }
        PageDto pageDto;
        try {
            pageService.delete(url);
            pageDto = pageService.create(siteDto, url, response.getStatus(), response.getDocument().html());
        } catch (Exception e) {
            return "Ошибка при сохранении страницы в БД + " + e.getMessage();
        }
        return indexPage(pageDto);
    }

    @Override
    public String indexPage(PageDto pageDto) {
        if (pageDto.getCode() >= 400 && pageDto.getCode() < 600) {
            return "";
        }
        Map<String, Long> lemmaMap;
        try {
            lemmaMap = Utils.doLucene(Utils.removeTags(pageDto.getContent()));
        } catch (Exception e) {
            return "Ошибка при лемматизации страницы: " + e.getMessage();
        }
        try {
            lemmaMap.forEach((lemma, rank) -> {
                LemmaDto lemmaDto = lemmaService.create(pageDto.getSiteId(), lemma);
                indexService.create(pageDto, lemmaDto, rank);
            });
        } catch (Exception e) {
            return "Ошибка при сохранении лемм БД + " + e.getMessage();
        }
        return "";
    }
}

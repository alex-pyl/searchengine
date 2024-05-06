package searchengine.indexing;

import lombok.RequiredArgsConstructor;
import searchengine.config.ConnectConfig;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.site.SiteDto;
import searchengine.model.SiteStatus;
import searchengine.services.IndexingService;
import searchengine.services.IndexingServiceImpl;
import searchengine.services.PageService;
import searchengine.services.SiteService;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
public class RunIndexing implements Runnable {
    private final Site site;
    private final ConnectConfig config;
    private final SiteService siteService;
    private final PageService pageService;
    private final IndexingService indexingService;

    @Override
    public void run() {
        System.out.println("Run = " + site.getUrl());
        siteService.delete(site);
        SiteDto siteDto = siteService.create(site, SiteStatus.INDEXING);
        try {
            String result = new ForkJoinPool().invoke(new SiteProcessor(siteDto.getUrl(), config, siteDto, pageService, siteService, indexingService));
            if (result == null) {
                siteService.updateStatus(siteDto, SiteStatus.INDEXED);
            } else {
                siteService.putError(siteDto, result);
            }
        } catch (CancellationException e) {
            System.out.println("Cancel = " + site.getUrl());
        }
    }
}

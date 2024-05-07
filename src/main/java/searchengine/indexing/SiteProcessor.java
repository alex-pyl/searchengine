package searchengine.indexing;

import lombok.RequiredArgsConstructor;
import searchengine.config.ConnectConfig;
import searchengine.dto.site.PageDto;
import searchengine.dto.site.SiteDto;
import searchengine.model.SiteStatus;
import searchengine.services.IndexingService;
import searchengine.services.PageService;
import searchengine.services.SiteService;
import searchengine.utils.Utils;
import searchengine.utils.WebResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

@RequiredArgsConstructor
public class SiteProcessor  extends RecursiveTask<String> {
    private final String url;
    private final ConnectConfig config;
    private final SiteDto siteDto;
    private final PageService pageService;
    private final SiteService siteService;
    private final IndexingService indexingService;
    private static final int SLEEP_MIN = 500;
    private static final int SLEEP_MAX = 4500;


    @Override
    protected String compute() {
        if (indexingService.isShutdown()) {
            siteService.putError(siteDto, "индексирование прервано пользователем");
            this.cancel(true);
            return "";
        }
        PageDto pageDto = pageService.create(siteDto, url, -1, "");
        if (pageDto == null) {
            return "";
        }
        System.out.println("Compute = " + url);
        List<SiteProcessor> tasks = new ArrayList<>();
        try {
            Thread.sleep(SLEEP_MIN + (int) (Math.random() * SLEEP_MAX));
        } catch (InterruptedException e) {
            return e.getMessage();
        }
        WebResponse response = Utils.connect(config, url);
        if (response.getError() != null) {
            pageService.updateResponse(pageDto, -1, response.getError());
            return "Ошибка подключения по адресу " + url + ": " + response.getError();
        }
        pageService.updateResponse(pageDto, response.getStatus(), response.getDocument().html());
        indexingService.indexPage(pageDto);
        siteService.updateStatus(siteDto, SiteStatus.INDEXING);
        List<String> refs = response.getDocument().getAllElements().stream().map(el -> el.absUrl("href")).distinct().toList();
        for (String ref : refs) {
            if (ref.endsWith("/") && ref.contains(siteDto.getTemplate())) {
                SiteProcessor task = new SiteProcessor(ref, config, siteDto, pageService, siteService, indexingService);
                task.fork();
                tasks.add(task);
            }
        }
        StringBuilder result = new StringBuilder();
        for (SiteProcessor item : tasks) {
            result.append(item.join());
        }
        return result.toString();
    }
}

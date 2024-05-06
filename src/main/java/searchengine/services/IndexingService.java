package searchengine.services;

import searchengine.dto.site.PageDto;

public interface IndexingService {
    String execute();

    String stop();

    boolean isShutdown();

    String indexPage(String url);

    String indexPage(PageDto pageDto);
}

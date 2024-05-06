package searchengine.dto.search;

import lombok.Data;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.TotalStatistics;

import java.util.List;

@Data
public class SearchData {
    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private float relevance;
}

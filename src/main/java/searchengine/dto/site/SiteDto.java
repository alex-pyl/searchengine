package searchengine.dto.site;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import searchengine.model.Page;
import searchengine.model.SiteStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiteDto {
    private Integer id;
    private SiteStatus status;
    private LocalDateTime statusTime;
    private String lastError;
    private String url;
    private String name;
    private String template;
}

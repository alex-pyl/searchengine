package searchengine.dto.site;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import searchengine.model.SiteStatus;

import java.time.LocalDateTime;

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

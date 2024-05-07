package searchengine.dto.site;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {
    private Integer id;
    private SiteDto siteId;
    private String path;
    private int code;
    private String content;
}

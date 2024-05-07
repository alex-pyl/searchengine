package searchengine.dto.site;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LemmaDto {
    private Integer id;
    private SiteDto siteId;
    private String lemma;
    private Integer frequency;
}

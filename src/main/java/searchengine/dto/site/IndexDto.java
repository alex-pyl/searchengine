package searchengine.dto.site;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndexDto {
    private Integer id;
    private PageDto page;
    private LemmaDto lemma;
    private Long rank;
}

package searchengine.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import searchengine.dto.site.LemmaDto;

import java.util.List;

@AllArgsConstructor
@Getter
public class SearchBuffer {
    String lemma;
    Integer frequency;
    List<LemmaDto> lemmaList;
}

package searchengine.dto.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import searchengine.dto.site.PageDto;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Relevance {
    PageDto pageDto;
    List<String> lemmas;
    float Relevance;
}

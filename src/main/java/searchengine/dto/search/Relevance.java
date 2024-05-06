package searchengine.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import searchengine.dto.site.PageDto;
import searchengine.model.Page;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Relevance {
    PageDto pageDto;
    List<String> lemmas;
    float Relevance;
}

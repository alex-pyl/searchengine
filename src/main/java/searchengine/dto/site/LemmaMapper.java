package searchengine.dto.site;

import org.mapstruct.Mapper;
import searchengine.model.Lemma;

@Mapper(componentModel = "spring")
public interface LemmaMapper {
    LemmaDto convertToDto(Lemma lemma);

    Lemma convertToEntity(LemmaDto lemmaDto);
}

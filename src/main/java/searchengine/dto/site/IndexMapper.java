package searchengine.dto.site;

import org.mapstruct.Mapper;
import searchengine.model.Index;

@Mapper(componentModel = "spring")
public interface IndexMapper {
    IndexDto convertToDto(Index index);

    Index convertToEntity(IndexDto indexDto);
}

package searchengine.dto.site;

import org.mapstruct.Mapper;
import searchengine.model.SiteModel;

@Mapper(componentModel = "spring")
public interface SiteMapper {
    SiteDto convertToDto(SiteModel site);

    SiteModel convertToEntity(SiteDto siteDto);
}

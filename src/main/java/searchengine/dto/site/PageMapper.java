package searchengine.dto.site;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import searchengine.model.Page;
import searchengine.model.SiteModel;

@Mapper(componentModel = "spring")
public interface PageMapper {
    PageDto convertToDto(Page page);

    Page convertToEntity(PageDto pageDto);
}

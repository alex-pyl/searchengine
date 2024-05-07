package searchengine.dto.site;
import org.mapstruct.Mapper;
import searchengine.model.Page;

@Mapper(componentModel = "spring")
public interface PageMapper {
    PageDto convertToDto(Page page);

    Page convertToEntity(PageDto pageDto);
}

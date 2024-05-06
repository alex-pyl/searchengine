package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;
import searchengine.model.SiteModel;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    Page findByPath(String path);

    int countBySiteId(SiteModel site);
}

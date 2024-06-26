package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;
import searchengine.model.SiteModel;

import java.util.List;

@Repository
public interface LemmaRepository  extends JpaRepository<Lemma, Integer> {
    Lemma findBySiteIdAndLemma(SiteModel site, String lemma);

    int countBySiteId(SiteModel site);

    List<Lemma> findByLemma(String lemma);
}

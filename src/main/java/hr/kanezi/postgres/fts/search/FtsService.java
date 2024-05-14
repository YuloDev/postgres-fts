package hr.kanezi.postgres.fts.search;

import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@Value
@Log4j2

public class FtsService {

    FtsDocumentRepository ftsDocumentRepository;

    FtsWordsViewRepository ftsWordsViewRepository;

    ActorRepository actorRepository;

    public List<FtsDocuments> search(String query, Long limit) {
        return StringUtils.isEmptyOrWhitespace(query)
                ? Collections.emptyList()
                : ftsDocumentRepository.ftsSearch(query, limit);
    }

    public List<FtsWordView> misspellings(String query) {
        return ftsWordsViewRepository.findSimilar(query);
    }

    public List<FtsDocuments> powerSearch(String author, String actors, String genre, String keywords, Double min, Double max) {
        return ftsDocumentRepository.ftsSearchWithRanking(keywords,author,actors,genre, min, max);
    }


}
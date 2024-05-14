package hr.kanezi.postgres.fts.search;

import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Value
public class EntityService {
    GenreRepository genreRepository;
    ActorRepository actorRepository;
    QuoteRepository quoteRepository;

    public List<String> getAllGenres() {
        return genreRepository.getAllGenres();
    }

    public List<String> getAllAuthors() {
        return quoteRepository.getAllAuthors();
    }
}

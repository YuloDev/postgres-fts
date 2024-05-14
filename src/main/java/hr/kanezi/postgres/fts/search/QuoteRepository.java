package hr.kanezi.postgres.fts.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, String> {
    @Query(value = """
            select author from quotes
            """, nativeQuery = true)
    List<String> getAllAuthors();
}

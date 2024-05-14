package hr.kanezi.postgres.fts.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query(value = """
            select name from genres
            """, nativeQuery = true)
    List<String> getAllGenres();
}

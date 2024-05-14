package hr.kanezi.postgres.fts.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    @Query(value = """
            SELECT a.name FROM actors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """, nativeQuery = true)
    List<String> search(@Param("name") String name);

}
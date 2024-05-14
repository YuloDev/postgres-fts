package hr.kanezi.postgres.fts.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FtsDocumentRepository extends JpaRepository<FtsDocuments, String> {

    @Query(value = """
            SELECT q.id,
                   q.type,
                   q.url,
                   fts doc,
                   ts_headline(title, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') as title,
                   ts_headline(description, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') as description,
                   ts_headline(meta, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') as meta,
                   greatest(ts_rank_cd(fts, websearch_to_tsquery(:q)), ts_rank_cd(fts, websearch_to_tsquery(:q))) ranking
            FROM fts_documents q
            WHERE fts @@ websearch_to_tsquery(:q) OR fts @@ websearch_to_tsquery(:q)
            UNION ALL
            SELECT q.id,
                   q.type,
                   q.url,
                   fts doc,
                   ts_headline(title, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as title,
                   ts_headline(description, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as description,
                   ts_headline(meta, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as meta,
                   ts_rank_cd(fts, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*')) ranking
            FROM fts_documents q
            WHERE fts @@ to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*') ||':*')
            AND :q ~* '^([[:word:]]|[[:digit:]])*$'
            ORDER BY ranking DESC LIMIT :limit
            """, nativeQuery = true)
    List<FtsDocuments> ftsSearch(@Param("q") String query, @Param("limit") Long limit);

    @Query(value = """
            WITH ranked_documents AS (
                SELECT q.id,
                       q.type,
                       q.url,
                       fts doc,
                       ts_headline(title, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') as title,
                       ts_headline(description, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') as description,
                       ts_headline(meta, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') as meta,
                       greatest(ts_rank_cd(fts, websearch_to_tsquery(:q)), ts_rank_cd(fts, websearch_to_tsquery(:q))) ranking
                FROM fts_documents q
                WHERE fts @@ websearch_to_tsquery(:q) OR fts @@ websearch_to_tsquery(:q)
                      OR fts @@ websearch_to_tsquery(:author) OR fts @@ websearch_to_tsquery(:actor)
                      AND meta @@ websearch_to_tsquery(:genre) OR fts @@ websearch_to_tsquery(:genre)
                UNION ALL
                SELECT q.id,
                       q.type,
                       q.url,
                       fts doc,
                       ts_headline(title, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as title,
                       ts_headline(description, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as description,
                       ts_headline(meta, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as meta,
                       ts_rank_cd(fts, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*')) ranking
                FROM fts_documents q
                WHERE fts @@ to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*') ||':*')
                      OR fts @@ to_tsquery(regexp_substr(:actor,  '([[:word:]]|[[:digit:]])*') ||':*')
                      AND meta @@ to_tsquery(regexp_substr(:genre,  '([[:word:]]|[[:digit:]])*') ||':*')
                      AND :q ~* '^([[:word:]]|[[:digit:]])*$'
                ORDER BY ranking DESC
            )
            SELECT *
            FROM ranked_documents
            WHERE ranking BETWEEN :minRanking AND :maxRanking
            """, nativeQuery = true)
    List<FtsDocuments> ftsSearchWithRanking(@Param("q") String query, @Param("author") String author, @Param("actor") String actors, @Param("genre") String genre, @Param("minRanking") Double minRanking, @Param("maxRanking") Double maxRanking);
}

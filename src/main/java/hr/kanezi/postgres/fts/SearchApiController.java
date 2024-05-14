package hr.kanezi.postgres.fts;

import hr.kanezi.postgres.fts.search.Actor;
import hr.kanezi.postgres.fts.search.ActorRepository;
import hr.kanezi.postgres.fts.search.FtsDocuments;
import hr.kanezi.postgres.fts.search.FtsService;
import lombok.Data;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@Value
@Log4j2
public class SearchApiController {

    FtsService ftsService;
    ActorRepository actorRepository;

    record Result(String title, String description, String url, String price) {
    }

    @Data
    public static class Results {
        List<Result> results = new ArrayList<>();

        public static Results from(List<FtsDocuments> documents) {
            Results res = new Results();

            documents.forEach(doc -> {
                res.results.add(new Result(doc.getTitle(), doc.getDescription(), doc.getUrl(), doc.getType().toLowerCase()));
            });

            return res;
        }
    }


    @GetMapping
    public Results search(@RequestParam(required = false) String q) {
        return Results.from(ftsService.search(q, 8L));
    }

    @GetMapping("/actors")
    public List<String> searchActors(@RequestParam(required = false) String q) {
        List<String> actorNames = actorRepository.search(q);
        log.info(actorNames);
        return actorNames;
    }


}

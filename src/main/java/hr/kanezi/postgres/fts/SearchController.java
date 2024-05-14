package hr.kanezi.postgres.fts;

import hr.kanezi.postgres.fts.quotes.QuotesService;
import hr.kanezi.postgres.fts.search.EntityService;
import hr.kanezi.postgres.fts.search.FtsDocuments;
import hr.kanezi.postgres.fts.search.FtsService;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
@Value
@Log4j2


public class SearchController {

    FtsService ftsService;
    EntityService entityService;

    @GetMapping
    public String home(Model model) {
        String[] authors = entityService.getAllAuthors().toArray(new String[0]);
        String[] genres = entityService.getAllGenres().toArray(new String[0]);
        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);
        return "search";
    }

    @PostMapping("/search")
    public String search(String q, RedirectAttributes attributes) {
        log.info("search for : {}", q);
        attributes.addFlashAttribute("q", q);

        List<FtsDocuments> docs = ftsService.search(q, 25L);
        attributes.addFlashAttribute("docs", docs);

        if (docs.isEmpty()) {
            attributes.addFlashAttribute("misspelling", ftsService.misspellings(q));
        }

        return "redirect:/";
    }

    @PostMapping("/powerSearch")
    public String powerSearch(@RequestParam(value = "author",required = false)  String author,
                              @RequestParam(value = "actors", required = false) String actors,
                              @RequestParam(value ="genre",required = false) String genre,
                              @RequestParam(value ="keywords",required = false) String keywords,
                              @RequestParam(value ="min",required = false) Double min,
                              @RequestParam(value ="max",required = false) Double max,
                              RedirectAttributes attributes) {

        log.info("Power search for author: {}, actors: {}, genre: {}, keywords: {}, min: {}, max: {}", author, actors, genre, keywords, min, max);
        String q = author + actors + genre + keywords;
        List<FtsDocuments> docs = ftsService.powerSearch(author, actors, genre, keywords, min, max);
        attributes.addFlashAttribute("docs", docs);

        if (docs.isEmpty()) {
            attributes.addFlashAttribute("misspelling", ftsService.misspellings(q));
        }

        return "redirect:/";
    }


}
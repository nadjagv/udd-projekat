package com.udd.uddprojekat.controller.law;


import com.udd.uddprojekat.dto.SearchQueryDTO;
import com.udd.uddprojekat.indexmodel.LawIndex;
import com.udd.uddprojekat.service.SearchService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/law/search")
public class LawSearchController {

    @Qualifier("LawSearchService")
    private final SearchService<LawIndex> searchService;

    public LawSearchController(@Qualifier("LawSearchService") SearchService<LawIndex> searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/simple")
    public Page<LawIndex> simpleSearch(@RequestBody SearchQueryDTO simpleSearchQuery,
                                       Pageable pageable) {
        return searchService.simpleSearch(simpleSearchQuery.keywords(), pageable);
    }

    @PostMapping("/advanced")
    public Page<LawIndex> advancedSearch(@RequestBody SearchQueryDTO advancedSearchQuery,
                                         Pageable pageable) {
        return searchService.advancedSearch(advancedSearchQuery.keywords(), pageable);
    }
}


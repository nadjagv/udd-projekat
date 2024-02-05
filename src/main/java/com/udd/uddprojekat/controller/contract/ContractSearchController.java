package com.udd.uddprojekat.controller.contract;

import com.udd.uddprojekat.dto.SearchQueryDTO;
import com.udd.uddprojekat.indexmodel.ContractIndex;
import com.udd.uddprojekat.service.SearchService;
import com.udd.uddprojekat.util.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contract/search")
public class ContractSearchController {

    @Qualifier("ContractSearchService")
    private final SearchService<ContractIndex> searchService;

    public ContractSearchController(@Qualifier("ContractSearchService") SearchService<ContractIndex> searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/simple/employee")
    public Page<ContractIndex> simpleEmployeeSearch(@RequestBody SearchQueryDTO simpleSearchQuery,
                                                    Pageable pageable) {

        return searchService.simpleSearch(simpleSearchQuery.keywords(),
                List.of(Constants.EMPLOYEE_NAME_FIELD_NAME), pageable, simpleSearchQuery.phraseQuery());
    }

    @PostMapping("/simple/government")
    public Page<ContractIndex> simpleGovernmentSearch(@RequestBody SearchQueryDTO simpleSearchQuery,
                                                      Pageable pageable) {
        return searchService.simpleSearch(simpleSearchQuery.keywords(),
                List.of(Constants.GOVERNMENT_NAME_FIELD_NAME, Constants.GOVERNMENT_LEVEL_FIELD_NAME),
                pageable, simpleSearchQuery.phraseQuery());
    }

    @PostMapping("/simple/content")
    public Page<ContractIndex> simpleContentSearch(@RequestBody SearchQueryDTO simpleSearchQuery,
                                                   Pageable pageable) {
        return searchService.simpleSearch(simpleSearchQuery.keywords(),
                List.of(Constants.CONTENT_SR_FIELD_NAME, Constants.CONTENT_EN_FIELD_NAME),
                pageable, simpleSearchQuery.phraseQuery());
    }

    @PostMapping("/simple")
    public Page<ContractIndex> simpleSearch(@RequestBody SearchQueryDTO simpleSearchQuery,
                                            Pageable pageable) {
        return searchService.simpleSearch(simpleSearchQuery.keywords(),
                List.of(Constants.CONTENT_SR_FIELD_NAME, Constants.CONTENT_EN_FIELD_NAME),
                pageable, simpleSearchQuery.phraseQuery());
    }

    @PostMapping("/advanced")
    public Page<ContractIndex> advancedSearch(@RequestBody SearchQueryDTO advancedSearchQuery,
                                              Pageable pageable) {
        return searchService.advancedSearch(advancedSearchQuery.keywords(), pageable);
    }
}



package com.udd.uddprojekat.service.impl.law;

import com.udd.uddprojekat.indexmodel.ContractIndex;
import com.udd.uddprojekat.indexmodel.LawIndex;
import com.udd.uddprojekat.service.SearchService;

import java.util.ArrayList;
import java.util.List;

import com.udd.uddprojekat.util.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

@Service("LawSearchService")
@RequiredArgsConstructor
public class LawSearchService implements SearchService<LawIndex> {

    private final ElasticsearchOperations elasticsearchTemplate;

    @Override
    public SearchPage<LawIndex> simpleSearch(List<String> keywords, List<String> fieldNames, Pageable pageable, boolean isPhraseSearch) {
        var highlightFields = new ArrayList<HighlightField>();
        for (var fieldName : fieldNames) {
            var hf = new HighlightField(fieldName);
            highlightFields.add(hf);
        }
        var highlight = new Highlight(highlightFields);
        var highliQuery = new HighlightQuery(highlight, ContractIndex.class);

        var searchQueryBuilder =
                new NativeQueryBuilder().withQuery(
                                isPhraseSearch
                                        ? QueryBuilder.buildPhraseQuery(String.join(" ", keywords), fieldNames)
                                        : QueryBuilder.buildSimpleSearchQuery(keywords, fieldNames))
                        .withPageable(pageable)
                        .withHighlightQuery(highliQuery);

        return runQuery(searchQueryBuilder.build());
    }

    @Override
    public SearchPage<LawIndex> advancedSearch(List<String> expression, Pageable pageable) {

        var searchQueryBuilder =
                new NativeQueryBuilder().withQuery(QueryBuilder.buildAdvancedSearchQuery(expression))
                        .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }


    private SearchPage<LawIndex> runQuery(NativeQuery searchQuery) {

        var searchHits = elasticsearchTemplate.search(searchQuery, LawIndex.class,
                IndexCoordinates.of("law_index"));

        SearchPage<LawIndex> searchHitsPaged = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());

        return searchHitsPaged;
    }
}
